package com.dmt.app.service;

import com.dmt.app.dto.StudyGroupDto;
import com.dmt.app.entity.StudyGroup;
import com.dmt.app.entity.StudyGroupMember;
import com.dmt.app.entity.User;
import com.dmt.app.repository.StudyGroupRepository;
import com.dmt.app.repository.StudyGroupMemberRepository;
import com.dmt.app.repository.UserRepository;
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
class StudyGroupServiceTest {

    @Mock
    private StudyGroupRepository studyGroupRepository;

    @Mock
    private StudyGroupMemberRepository studyGroupMemberRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private StudyGroupService studyGroupService;

    private User testUser;
    private StudyGroup testStudyGroup;
    private StudyGroupMember testMember;
    private StudyGroupDto.CreateRequest createRequest;
    private StudyGroupDto.UpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .nickname("테스트유저")
                .password("password123")
                .role(User.UserRole.USER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testStudyGroup = StudyGroup.builder()
                .id(1L)
                .name("테스트 스터디 그룹")
                .description("테스트용 스터디 그룹입니다")
                .maxMembers(5)
                .minMembers(2)
                .status(StudyGroup.StudyGroupStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testMember = StudyGroupMember.builder()
                .id(1L)
                .user(testUser)
                .studyGroup(testStudyGroup)
                .role(StudyGroupMember.MemberRole.LEADER)
                .isActive(true)
                .joinedAt(LocalDateTime.now())
                .build();

        createRequest = StudyGroupDto.CreateRequest.builder()
                .name("새로운 스터디 그룹")
                .description("새로 만든 스터디 그룹")
                .maxMembers(6)
                .build();

        updateRequest = StudyGroupDto.UpdateRequest.builder()
                .name("수정된 스터디 그룹")
                .description("수정된 설명")
                .maxMembers(8)
                .build();
    }

    @Test
    @DisplayName("스터디 그룹 생성 성공")
    void createStudyGroup_Success() {
        // given
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(studyGroupRepository.save(any(StudyGroup.class))).thenReturn(testStudyGroup);
        when(studyGroupMemberRepository.save(any(StudyGroupMember.class))).thenReturn(testMember);

        // when
        StudyGroupDto.Response response = studyGroupService.createStudyGroup(testUser.getId(), createRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo(testStudyGroup.getName());
        assertThat(response.getDescription()).isEqualTo(testStudyGroup.getDescription());
        assertThat(response.getMaxMembers()).isEqualTo(testStudyGroup.getMaxMembers());
        assertThat(response.getMinMembers()).isEqualTo(2); // 기본값

        verify(userRepository).findById(testUser.getId());
        verify(studyGroupRepository).save(any(StudyGroup.class));
        verify(studyGroupMemberRepository).save(any(StudyGroupMember.class));
    }

    @Test
    @DisplayName("스터디 그룹 생성 실패 - 사용자를 찾을 수 없음")
    void createStudyGroup_Fail_UserNotFound() {
        // given
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> studyGroupService.createStudyGroup(testUser.getId(), createRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");

        verify(userRepository).findById(testUser.getId());
        verify(studyGroupRepository, never()).save(any(StudyGroup.class));
    }

    @Test
    @DisplayName("스터디 그룹 조회 성공")
    void getStudyGroupById_Success() {
        // given
        when(studyGroupRepository.findById(testStudyGroup.getId())).thenReturn(Optional.of(testStudyGroup));

        // when
        StudyGroupDto.Response response = studyGroupService.getStudyGroupById(testStudyGroup.getId());

        // then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(testStudyGroup.getId());
        assertThat(response.getName()).isEqualTo(testStudyGroup.getName());

        verify(studyGroupRepository).findById(testStudyGroup.getId());
    }

    @Test
    @DisplayName("스터디 그룹 조회 실패 - 그룹을 찾을 수 없음")
    void getStudyGroupById_Fail_GroupNotFound() {
        // given
        when(studyGroupRepository.findById(testStudyGroup.getId())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> studyGroupService.getStudyGroupById(testStudyGroup.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("스터디 그룹을 찾을 수 없습니다.");

        verify(studyGroupRepository).findById(testStudyGroup.getId());
    }

    @Test
    @DisplayName("모든 스터디 그룹 조회 성공")
    void getAllStudyGroups_Success() {
        // given
        List<StudyGroup> groups = Arrays.asList(testStudyGroup);
        when(studyGroupRepository.findAll()).thenReturn(groups);

        // when
        List<StudyGroupDto.Response> responses = studyGroupService.getAllStudyGroups();

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getName()).isEqualTo(testStudyGroup.getName());

        verify(studyGroupRepository).findAll();
    }

    @Test
    @DisplayName("활성 스터디 그룹 조회 성공")
    void getActiveStudyGroups_Success() {
        // given
        List<StudyGroup> activeGroups = Arrays.asList(testStudyGroup);
        when(studyGroupRepository.findByStatus(StudyGroup.StudyGroupStatus.ACTIVE)).thenReturn(activeGroups);

        // when
        List<StudyGroupDto.Response> responses = studyGroupService.getActiveStudyGroups();

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getStatus()).isEqualTo(StudyGroup.StudyGroupStatus.ACTIVE);

        verify(studyGroupRepository).findByStatus(StudyGroup.StudyGroupStatus.ACTIVE);
    }

    @Test
    @DisplayName("사용자별 스터디 그룹 조회 성공")
    void getStudyGroupsByUserId_Success() {
        // given
        List<StudyGroup> userGroups = Arrays.asList(testStudyGroup);
        when(studyGroupRepository.findActiveGroupsByUserId(testUser.getId())).thenReturn(userGroups);

        // when
        List<StudyGroupDto.Response> responses = studyGroupService.getStudyGroupsByUserId(testUser.getId());

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getName()).isEqualTo(testStudyGroup.getName());

        verify(studyGroupRepository).findActiveGroupsByUserId(testUser.getId());
    }

    @Test
    @DisplayName("스터디 그룹 수정 성공 - 리더 권한")
    void updateStudyGroup_Success_LeaderRole() {
        // given
        when(studyGroupRepository.findById(testStudyGroup.getId())).thenReturn(Optional.of(testStudyGroup));
        when(studyGroupMemberRepository.findByUserIdAndStudyGroupId(testUser.getId(), testStudyGroup.getId()))
                .thenReturn(Optional.of(testMember));

        // when
        StudyGroupDto.Response response = studyGroupService.updateStudyGroup(testStudyGroup.getId(), testUser.getId(), updateRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo(updateRequest.getName());
        assertThat(response.getDescription()).isEqualTo(updateRequest.getDescription());
        assertThat(response.getMaxMembers()).isEqualTo(updateRequest.getMaxMembers());

        verify(studyGroupRepository).findById(testStudyGroup.getId());
        verify(studyGroupMemberRepository).findByUserIdAndStudyGroupId(testUser.getId(), testStudyGroup.getId());
    }

    @Test
    @DisplayName("스터디 그룹 수정 실패 - 그룹을 찾을 수 없음")
    void updateStudyGroup_Fail_GroupNotFound() {
        // given
        when(studyGroupRepository.findById(testStudyGroup.getId())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> studyGroupService.updateStudyGroup(testStudyGroup.getId(), testUser.getId(), updateRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("스터디 그룹을 찾을 수 없습니다.");

        verify(studyGroupRepository).findById(testStudyGroup.getId());
        verify(studyGroupMemberRepository, never()).findByUserIdAndStudyGroupId(any(), any());
    }

    @Test
    @DisplayName("스터디 그룹 수정 실패 - 그룹 멤버가 아님")
    void updateStudyGroup_Fail_NotMember() {
        // given
        when(studyGroupRepository.findById(testStudyGroup.getId())).thenReturn(Optional.of(testStudyGroup));
        when(studyGroupMemberRepository.findByUserIdAndStudyGroupId(testUser.getId(), testStudyGroup.getId()))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> studyGroupService.updateStudyGroup(testStudyGroup.getId(), testUser.getId(), updateRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("그룹 멤버가 아닙니다.");

        verify(studyGroupRepository).findById(testStudyGroup.getId());
        verify(studyGroupMemberRepository).findByUserIdAndStudyGroupId(testUser.getId(), testStudyGroup.getId());
    }

    @Test
    @DisplayName("스터디 그룹 수정 실패 - 리더 권한 없음")
    void updateStudyGroup_Fail_NotLeader() {
        // given
        StudyGroupMember memberMember = StudyGroupMember.builder()
                .role(StudyGroupMember.MemberRole.MEMBER)
                .build();

        when(studyGroupRepository.findById(testStudyGroup.getId())).thenReturn(Optional.of(testStudyGroup));
        when(studyGroupMemberRepository.findByUserIdAndStudyGroupId(testUser.getId(), testStudyGroup.getId()))
                .thenReturn(Optional.of(memberMember));

        // when & then
        assertThatThrownBy(() -> studyGroupService.updateStudyGroup(testStudyGroup.getId(), testUser.getId(), updateRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("그룹 리더만 수정할 수 있습니다.");

        verify(studyGroupRepository).findById(testStudyGroup.getId());
        verify(studyGroupMemberRepository).findByUserIdAndStudyGroupId(testUser.getId(), testStudyGroup.getId());
    }
} 