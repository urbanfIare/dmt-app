package com.dmt.app.service;

import com.dmt.app.dto.StudySessionDto;
import com.dmt.app.entity.StudyGroup;
import com.dmt.app.entity.StudyGroupMember;
import com.dmt.app.entity.StudySession;
import com.dmt.app.entity.User;
import com.dmt.app.repository.StudyGroupRepository;
import com.dmt.app.repository.StudyGroupMemberRepository;
import com.dmt.app.repository.StudySessionRepository;
import com.dmt.app.repository.PhoneRestrictionExceptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudySessionServiceTest {

    @Mock
    private StudySessionRepository studySessionRepository;

    @Mock
    private StudyGroupRepository studyGroupRepository;

    @Mock
    private StudyGroupMemberRepository studyGroupMemberRepository;

    @Mock
    private PhoneRestrictionExceptionRepository phoneRestrictionExceptionRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private StudySessionService studySessionService;

    private User testUser;
    private StudyGroup testStudyGroup;
    private StudyGroupMember testMember;
    private StudySession testStudySession;
    private StudySessionDto.CreateRequest createRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .nickname("테스트유저")
                .role(User.UserRole.USER)
                .build();

        testStudyGroup = StudyGroup.builder()
                .id(1L)
                .name("테스트 스터디 그룹")
                .status(StudyGroup.StudyGroupStatus.ACTIVE)
                .build();

        testMember = StudyGroupMember.builder()
                .id(1L)
                .user(testUser)
                .studyGroup(testStudyGroup)
                .role(StudyGroupMember.MemberRole.LEADER)
                .isActive(true)
                .build();

        testStudySession = StudySession.builder()
                .id(1L)
                .studyGroup(testStudyGroup)
                .sessionName("테스트 세션")
                .startTime(LocalDateTime.now().plusHours(1))
                .endTime(LocalDateTime.now().plusHours(2))
                .durationMinutes(60)
                .status(StudySession.SessionStatus.SCHEDULED)
                .build();

        createRequest = StudySessionDto.CreateRequest.builder()
                .studyGroupId(1L)
                .sessionName("새로운 세션")
                .startTime(LocalDateTime.now().plusHours(1))
                .endTime(LocalDateTime.now().plusHours(2))
                .build();
    }

    @Test
    @DisplayName("스터디 세션 생성 성공")
    void createStudySession_Success() {
        // given
        when(studyGroupRepository.findById(createRequest.getStudyGroupId())).thenReturn(Optional.of(testStudyGroup));
        when(studyGroupMemberRepository.findByUserIdAndStudyGroupId(testUser.getId(), createRequest.getStudyGroupId()))
                .thenReturn(Optional.of(testMember));
        when(studySessionRepository.save(any(StudySession.class))).thenReturn(testStudySession);

        // when
        StudySessionDto.Response response = studySessionService.createStudySession(testUser.getId(), createRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getSessionName()).isEqualTo(testStudySession.getSessionName());
        assertThat(response.getStudyGroupId()).isEqualTo(testStudyGroup.getId());

        verify(studyGroupRepository).findById(createRequest.getStudyGroupId());
        verify(studyGroupMemberRepository).findByUserIdAndStudyGroupId(testUser.getId(), createRequest.getStudyGroupId());
        verify(studySessionRepository).save(any(StudySession.class));
    }

    @Test
    @DisplayName("스터디 세션 생성 실패 - 그룹을 찾을 수 없음")
    void createStudySession_Fail_GroupNotFound() {
        // given
        when(studyGroupRepository.findById(createRequest.getStudyGroupId())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> studySessionService.createStudySession(testUser.getId(), createRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("스터디 그룹을 찾을 수 없습니다.");

        verify(studyGroupRepository).findById(createRequest.getStudyGroupId());
        verify(studyGroupMemberRepository, never()).findByUserIdAndStudyGroupId(any(), any());
    }

    @Test
    @DisplayName("스터디 세션 생성 실패 - 그룹 멤버가 아님")
    void createStudySession_Fail_NotMember() {
        // given
        when(studyGroupRepository.findById(createRequest.getStudyGroupId())).thenReturn(Optional.of(testStudyGroup));
        when(studyGroupMemberRepository.findByUserIdAndStudyGroupId(testUser.getId(), createRequest.getStudyGroupId()))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> studySessionService.createStudySession(testUser.getId(), createRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("그룹 멤버가 아닙니다.");

        verify(studyGroupRepository).findById(createRequest.getStudyGroupId());
        verify(studyGroupMemberRepository).findByUserIdAndStudyGroupId(testUser.getId(), createRequest.getStudyGroupId());
    }

    @Test
    @DisplayName("스터디 세션 생성 실패 - 리더 권한 없음")
    void createStudySession_Fail_NotLeader() {
        // given
        StudyGroupMember memberMember = StudyGroupMember.builder()
                .role(StudyGroupMember.MemberRole.MEMBER)
                .build();

        when(studyGroupRepository.findById(createRequest.getStudyGroupId())).thenReturn(Optional.of(testStudyGroup));
        when(studyGroupMemberRepository.findByUserIdAndStudyGroupId(testUser.getId(), createRequest.getStudyGroupId()))
                .thenReturn(Optional.of(memberMember));

        // when & then
        assertThatThrownBy(() -> studySessionService.createStudySession(testUser.getId(), createRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("그룹 리더만 세션을 생성할 수 있습니다.");

        verify(studyGroupRepository).findById(createRequest.getStudyGroupId());
        verify(studyGroupMemberRepository).findByUserIdAndStudyGroupId(testUser.getId(), createRequest.getStudyGroupId());
    }

    @Test
    @DisplayName("스터디 세션 생성 실패 - 시작 시간이 종료 시간보다 늦음")
    void createStudySession_Fail_InvalidTimeRange() {
        // given
        StudySessionDto.CreateRequest invalidRequest = StudySessionDto.CreateRequest.builder()
                .studyGroupId(1L)
                .sessionName("잘못된 시간 세션")
                .startTime(LocalDateTime.now().plusHours(2))
                .endTime(LocalDateTime.now().plusHours(1))
                .build();

        when(studyGroupRepository.findById(invalidRequest.getStudyGroupId())).thenReturn(Optional.of(testStudyGroup));
        when(studyGroupMemberRepository.findByUserIdAndStudyGroupId(testUser.getId(), invalidRequest.getStudyGroupId()))
                .thenReturn(Optional.of(testMember));

        // when & then
        assertThatThrownBy(() -> studySessionService.createStudySession(testUser.getId(), invalidRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("시작 시간은 종료 시간보다 빨라야 합니다.");

        verify(studyGroupRepository).findById(invalidRequest.getStudyGroupId());
        verify(studyGroupMemberRepository).findByUserIdAndStudyGroupId(testUser.getId(), invalidRequest.getStudyGroupId());
    }

    @Test
    @DisplayName("스터디 세션 생성 실패 - 시작 시간이 과거")
    void createStudySession_Fail_PastStartTime() {
        // given
        StudySessionDto.CreateRequest pastRequest = StudySessionDto.CreateRequest.builder()
                .studyGroupId(1L)
                .sessionName("과거 시간 세션")
                .startTime(LocalDateTime.now().minusHours(1))
                .endTime(LocalDateTime.now().plusHours(1))
                .build();

        when(studyGroupRepository.findById(pastRequest.getStudyGroupId())).thenReturn(Optional.of(testStudyGroup));
        when(studyGroupMemberRepository.findByUserIdAndStudyGroupId(testUser.getId(), pastRequest.getStudyGroupId()))
                .thenReturn(Optional.of(testMember));

        // when & then
        assertThatThrownBy(() -> studySessionService.createStudySession(testUser.getId(), pastRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("시작 시간은 현재 시간보다 늦어야 합니다.");

        verify(studyGroupRepository).findById(pastRequest.getStudyGroupId());
        verify(studyGroupMemberRepository).findByUserIdAndStudyGroupId(testUser.getId(), pastRequest.getStudyGroupId());
    }

    @Test
    @DisplayName("스터디 세션 조회 성공")
    void getStudySessionById_Success() {
        // given
        when(studySessionRepository.findById(testStudySession.getId())).thenReturn(Optional.of(testStudySession));

        // when
        StudySessionDto.Response response = studySessionService.getStudySessionById(testStudySession.getId());

        // then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(testStudySession.getId());
        assertThat(response.getSessionName()).isEqualTo(testStudySession.getSessionName());

        verify(studySessionRepository).findById(testStudySession.getId());
    }

    @Test
    @DisplayName("스터디 세션 조회 실패 - 세션을 찾을 수 없음")
    void getStudySessionById_Fail_SessionNotFound() {
        // given
        when(studySessionRepository.findById(testStudySession.getId())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> studySessionService.getStudySessionById(testStudySession.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("스터디 세션을 찾을 수 없습니다.");

        verify(studySessionRepository).findById(testStudySession.getId());
    }

    @Test
    @DisplayName("그룹별 스터디 세션 조회 성공")
    void getStudySessionsByGroupId_Success() {
        // given
        List<StudySession> sessions = Arrays.asList(testStudySession);
        when(studySessionRepository.findByStudyGroupId(testStudyGroup.getId())).thenReturn(sessions);

        // when
        List<StudySessionDto.Response> responses = studySessionService.getStudySessionsByGroupId(testStudyGroup.getId());

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getSessionName()).isEqualTo(testStudySession.getSessionName());

        verify(studySessionRepository).findByStudyGroupId(testStudyGroup.getId());
    }

    @Test
    @DisplayName("예정된 세션 조회 성공")
    void getUpcomingSessionsByGroupId_Success() {
        // given
        List<StudySession> upcomingSessions = Arrays.asList(testStudySession);
        when(studySessionRepository.findUpcomingSessionsByGroupId(testStudyGroup.getId(), LocalDateTime.now()))
                .thenReturn(upcomingSessions);

        // when
        List<StudySessionDto.Response> responses = studySessionService.getUpcomingSessionsByGroupId(testStudyGroup.getId());

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getSessionName()).isEqualTo(testStudySession.getSessionName());

        verify(studySessionRepository).findUpcomingSessionsByGroupId(testStudyGroup.getId(), LocalDateTime.now());
    }

    @Test
    @DisplayName("현재 진행 중인 세션 조회 성공")
    void getCurrentSessions_Success() {
        // given
        List<StudySession> currentSessions = Arrays.asList(testStudySession);
        when(studySessionRepository.findCurrentSessions(LocalDateTime.now())).thenReturn(currentSessions);

        // when
        List<StudySessionDto.Response> responses = studySessionService.getCurrentSessions();

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getSessionName()).isEqualTo(testStudySession.getSessionName());

        verify(studySessionRepository).findCurrentSessions(LocalDateTime.now());
    }
} 