package com.dmt.app.service;

import com.dmt.app.dto.StudySessionDto;
import com.dmt.app.entity.StudyGroup;
import com.dmt.app.entity.StudyGroupMember;
import com.dmt.app.entity.StudySession;
import com.dmt.app.entity.PhoneRestrictionException;
import com.dmt.app.exception.StudyGroupException;
import com.dmt.app.exception.StudySessionException;
import com.dmt.app.repository.StudyGroupRepository;
import com.dmt.app.repository.StudyGroupMemberRepository;
import com.dmt.app.repository.StudySessionRepository;
import com.dmt.app.repository.PhoneRestrictionExceptionRepository;
import com.dmt.app.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StudySessionService {
    
    private final StudySessionRepository studySessionRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final StudyGroupMemberRepository studyGroupMemberRepository;
    private final PhoneRestrictionExceptionRepository phoneRestrictionExceptionRepository;
    private final NotificationService notificationService;
    
    @Transactional
    public StudySessionDto.Response createStudySession(Long userId, StudySessionDto.CreateRequest request) {
        // 스터디 그룹 존재 확인
        StudyGroup studyGroup = studyGroupRepository.findById(request.getStudyGroupId())
                .orElseThrow(() -> new StudyGroupException.StudyGroupNotFoundException(request.getStudyGroupId()));
        
        // 그룹 멤버인지 확인
        StudyGroupMember member = studyGroupMemberRepository.findByUserIdAndStudyGroupId(userId, request.getStudyGroupId())
                .orElseThrow(() -> new StudyGroupException.NotGroupMemberException(userId, request.getStudyGroupId()));
        
        // 리더만 세션 생성 가능
        if (member.getRole() != StudyGroupMember.MemberRole.LEADER) {
            throw new StudyGroupException.NotGroupLeaderException(userId, request.getStudyGroupId());
        }
        
        // 시간 유효성 검사
        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new StudySessionException.InvalidSessionTimeException("시작 시간은 종료 시간보다 빨라야 합니다.");
        }
        
        if (request.getStartTime().isBefore(LocalDateTime.now())) {
            throw new StudySessionException.InvalidSessionTimeException("시작 시간은 현재 시간보다 늦어야 합니다.");
        }
        
        // 기간 계산
        Integer durationMinutes = (int) ChronoUnit.MINUTES.between(request.getStartTime(), request.getEndTime());
        
        StudySession studySession = StudySession.builder()
                .studyGroup(studyGroup)
                .sessionName(request.getSessionName())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .durationMinutes(durationMinutes)
                .status(StudySession.SessionStatus.SCHEDULED)
                .build();
        
        StudySession savedSession = studySessionRepository.save(studySession);
        log.info("새로운 스터디 세션이 생성되었습니다: {} (그룹: {})", savedSession.getSessionName(), studyGroup.getName());
        
        return StudySessionDto.Response.from(savedSession);
    }
    
    public StudySessionDto.Response getStudySessionById(Long sessionId) {
        StudySession studySession = studySessionRepository.findById(sessionId)
                .orElseThrow(() -> new StudySessionException.StudySessionNotFoundException(sessionId));
        
        return StudySessionDto.Response.from(studySession);
    }
    
    public List<StudySessionDto.Response> getStudySessionsByGroupId(Long groupId) {
        return studySessionRepository.findByStudyGroupId(groupId).stream()
                .map(StudySessionDto.Response::from)
                .collect(Collectors.toList());
    }
    
    public List<StudySessionDto.Response> getUpcomingSessionsByGroupId(Long groupId) {
        return studySessionRepository.findUpcomingSessionsByGroupId(groupId, LocalDateTime.now()).stream()
                .map(StudySessionDto.Response::from)
                .collect(Collectors.toList());
    }
    
    public List<StudySessionDto.Response> getCurrentSessions() {
        return studySessionRepository.findCurrentSessions(LocalDateTime.now()).stream()
                .map(StudySessionDto.Response::from)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public StudySessionDto.Response updateStudySession(Long sessionId, Long userId, StudySessionDto.UpdateRequest request) {
        StudySession studySession = studySessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("스터디 세션을 찾을 수 없습니다."));
        
        // 그룹 멤버인지 확인
        StudyGroupMember member = studyGroupMemberRepository.findByUserIdAndStudyGroupId(userId, studySession.getStudyGroup().getId())
                .orElseThrow(() -> new IllegalArgumentException("그룹 멤버가 아닙니다."));
        
        // 리더만 수정 가능
        if (member.getRole() != StudyGroupMember.MemberRole.LEADER) {
            throw new IllegalArgumentException("그룹 리더만 세션을 수정할 수 있습니다.");
        }
        
        // 이미 진행 중이거나 완료된 세션은 수정 불가
        if (studySession.getStatus() != StudySession.SessionStatus.SCHEDULED) {
            throw new IllegalArgumentException("이미 진행 중이거나 완료된 세션은 수정할 수 없습니다.");
        }
        
        // 시간 유효성 검사
        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new IllegalArgumentException("시작 시간은 종료 시간보다 빨라야 합니다.");
        }
        
        studySession.setSessionName(request.getSessionName());
        studySession.setStartTime(request.getStartTime());
        studySession.setEndTime(request.getEndTime());
        studySession.setDurationMinutes((int) ChronoUnit.MINUTES.between(request.getStartTime(), request.getEndTime()));
        
        StudySession updatedSession = studySessionRepository.save(studySession);
        log.info("스터디 세션이 수정되었습니다: {}", updatedSession.getSessionName());
        
        return StudySessionDto.Response.from(updatedSession);
    }
    
    @Transactional
    public StudySessionDto.Response updateSessionStatus(Long sessionId, Long userId, StudySessionDto.StatusUpdateRequest request) {
        StudySession studySession = studySessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("스터디 세션을 찾을 수 없습니다."));
        
        // 그룹 멤버인지 확인
        StudyGroupMember member = studyGroupMemberRepository.findByUserIdAndStudyGroupId(userId, studySession.getStudyGroup().getId())
                .orElseThrow(() -> new IllegalArgumentException("그룹 멤버가 아닙니다."));
        
        // 리더만 상태 변경 가능
        if (member.getRole() != StudyGroupMember.MemberRole.LEADER) {
            throw new IllegalArgumentException("그룹 리더만 세션 상태를 변경할 수 있습니다.");
        }
        
        // 상태 변경 로직
        switch (request.getStatus()) {
            case IN_PROGRESS:
                if (studySession.getStatus() != StudySession.SessionStatus.SCHEDULED) {
                    throw new IllegalArgumentException("예정된 세션만 진행 상태로 변경할 수 있습니다.");
                }
                if (LocalDateTime.now().isBefore(studySession.getStartTime())) {
                    throw new IllegalArgumentException("아직 시작 시간이 되지 않았습니다.");
                }
                break;
            case COMPLETED:
                if (studySession.getStatus() != StudySession.SessionStatus.IN_PROGRESS) {
                    throw new IllegalArgumentException("진행 중인 세션만 완료 상태로 변경할 수 있습니다.");
                }
                break;
            case CANCELLED:
                if (studySession.getStatus() != StudySession.SessionStatus.SCHEDULED) {
                    throw new IllegalArgumentException("예정된 세션만 취소할 수 있습니다.");
                }
                break;
            default:
                throw new IllegalArgumentException("유효하지 않은 상태입니다.");
        }
        
        studySession.setStatus(request.getStatus());
        StudySession updatedSession = studySessionRepository.save(studySession);
        log.info("스터디 세션 상태가 변경되었습니다: {} -> {}", studySession.getSessionName(), request.getStatus());
        
        return StudySessionDto.Response.from(updatedSession);
    }
    
    @Transactional
    public void deleteStudySession(Long sessionId, Long userId) {
        StudySession studySession = studySessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("스터디 세션을 찾을 수 없습니다."));
        
        // 그룹 멤버인지 확인
        StudyGroupMember member = studyGroupMemberRepository.findByUserIdAndStudyGroupId(userId, studySession.getStudyGroup().getId())
                .orElseThrow(() -> new IllegalArgumentException("그룹 멤버가 아닙니다."));
        
        // 리더만 삭제 가능
        if (member.getRole() != StudyGroupMember.MemberRole.LEADER) {
            throw new IllegalArgumentException("그룹 리더만 세션을 삭제할 수 있습니다.");
        }
        
        // 이미 진행 중이거나 완료된 세션은 삭제 불가
        if (studySession.getStatus() != StudySession.SessionStatus.SCHEDULED) {
            throw new IllegalArgumentException("이미 진행 중이거나 완료된 세션은 삭제할 수 없습니다.");
        }
        
        studySessionRepository.delete(studySession);
        log.info("스터디 세션이 삭제되었습니다: {}", studySession.getSessionName());
    }
    
    // 스케줄러를 위한 메서드 - 예정된 세션을 진행 상태로 변경
    @Transactional
    public void startScheduledSessions() {
        List<StudySession> sessionsToStart = studySessionRepository.findSessionsToStart(LocalDateTime.now());
        
        for (StudySession session : sessionsToStart) {
            session.setStatus(StudySession.SessionStatus.IN_PROGRESS);
            studySessionRepository.save(session);
            log.info("스터디 세션이 자동으로 시작되었습니다: {}", session.getSessionName());
            
            // 실시간 알림 전송
            notificationService.notifySessionStart(session);
        }
    }

    // 폰 사용 제한 관련 메서드들
    
    /**
     * 사용자의 현재 폰 사용 제한 상태 확인
     */
    public boolean isPhoneRestricted(Long userId) {
        List<StudySession> activeSessions = studySessionRepository.findCurrentSessions(LocalDateTime.now());
        
        for (StudySession session : activeSessions) {
            // 사용자가 해당 세션에 참여하고 있는지 확인
            if (isUserParticipatingInSession(userId, session.getId())) {
                // 폰 사용 제한 예외가 있는지 확인
                if (!hasActivePhoneException(userId, session.getId())) {
                    return true; // 폰 사용 제한됨
                }
            }
        }
        return false; // 폰 사용 제한되지 않음
    }
    
    /**
     * 사용자가 특정 세션에 참여하고 있는지 확인
     */
    private boolean isUserParticipatingInSession(Long userId, Long sessionId) {
        // 출석 기록에서 확인
        // TODO: AttendanceService와 연동하여 구현
        return true; // 임시로 true 반환
    }
    
    /**
     * 사용자가 특정 세션에 대해 활성화된 폰 사용 제한 예외가 있는지 확인
     */
    private boolean hasActivePhoneException(Long userId, Long sessionId) {
        return phoneRestrictionExceptionRepository.findByUserIdAndStudySessionId(userId, sessionId)
                .map(exception -> exception.getStatus() == PhoneRestrictionException.ExceptionStatus.APPROVED)
                .orElse(false);
    }
    
    /**
     * 세션별 폰 사용 제한 상태 요약
     */
    public StudySessionDto.PhoneRestrictionStatus getPhoneRestrictionStatus(Long sessionId) {
        StudySession session = studySessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("스터디 세션을 찾을 수 없습니다."));
        
        if (session.getStatus() != StudySession.SessionStatus.IN_PROGRESS) {
                    return StudySessionDto.PhoneRestrictionStatus.create(sessionId, false, "세션이 진행 중이 아닙니다.");
        }
        
        // 현재 시간이 세션 시간 내에 있는지 확인
        LocalDateTime now = LocalDateTime.now();
        boolean isInSessionTime = !now.isBefore(session.getStartTime()) && !now.isAfter(session.getEndTime());
        
        if (!isInSessionTime) {
            return StudySessionDto.PhoneRestrictionStatus.create(sessionId, false, "세션 시간이 아닙니다.");
        }
        
        return StudySessionDto.PhoneRestrictionStatus.create(sessionId, true, "세션 진행 중 - 폰 사용 제한");
    }
} 