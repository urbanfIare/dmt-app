package com.dmt.app.service;

import com.dmt.app.dto.PhoneRestrictionExceptionDto;
import com.dmt.app.entity.PhoneRestrictionException;
import com.dmt.app.entity.StudyGroupMember;
import com.dmt.app.entity.StudySession;
import com.dmt.app.entity.User;
import com.dmt.app.repository.PhoneRestrictionExceptionRepository;
import com.dmt.app.repository.StudyGroupMemberRepository;
import com.dmt.app.repository.StudySessionRepository;
import com.dmt.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PhoneRestrictionExceptionService {
    
    private final PhoneRestrictionExceptionRepository phoneRestrictionExceptionRepository;
    private final StudySessionRepository studySessionRepository;
    private final UserRepository userRepository;
    private final StudyGroupMemberRepository studyGroupMemberRepository;
    
    @Transactional
    public PhoneRestrictionExceptionDto.Response createException(PhoneRestrictionExceptionDto.CreateRequest request) {
        // 사용자 존재 확인
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        // 스터디 세션 존재 확인
        StudySession studySession = studySessionRepository.findById(request.getStudySessionId())
                .orElseThrow(() -> new IllegalArgumentException("스터디 세션을 찾을 수 없습니다."));
        
        // 이미 예외 신청이 있는지 확인
        Optional<PhoneRestrictionException> existingException = phoneRestrictionExceptionRepository
                .findByUserIdAndStudySessionId(request.getUserId(), request.getStudySessionId());
        
        if (existingException.isPresent()) {
            throw new IllegalArgumentException("이미 폰 사용 제한 예외를 신청했습니다.");
        }
        
        // 시간 유효성 검사
        if (request.getExceptionStartTime() != null && request.getExceptionEndTime() != null) {
            if (request.getExceptionStartTime().isAfter(request.getExceptionEndTime())) {
                throw new IllegalArgumentException("예외 시작 시간은 종료 시간보다 빨라야 합니다.");
            }
            
            if (request.getExceptionStartTime().isBefore(studySession.getStartTime()) || 
                request.getExceptionEndTime().isAfter(studySession.getEndTime())) {
                throw new IllegalArgumentException("예외 시간은 스터디 세션 시간 내에 있어야 합니다.");
            }
        }
        
        PhoneRestrictionException exception = PhoneRestrictionException.builder()
                .user(user)
                .studySession(studySession)
                .status(PhoneRestrictionException.ExceptionStatus.PENDING)
                .reason(request.getReason())
                .exceptionStartTime(request.getExceptionStartTime())
                .exceptionEndTime(request.getExceptionEndTime())
                .build();
        
        PhoneRestrictionException savedException = phoneRestrictionExceptionRepository.save(exception);
        log.info("폰 사용 제한 예외가 신청되었습니다: {} - {}", user.getNickname(), studySession.getSessionName());
        
        return PhoneRestrictionExceptionDto.Response.from(savedException);
    }
    
    public PhoneRestrictionExceptionDto.Response getExceptionById(Long exceptionId) {
        PhoneRestrictionException exception = phoneRestrictionExceptionRepository.findById(exceptionId)
                .orElseThrow(() -> new IllegalArgumentException("폰 사용 제한 예외를 찾을 수 없습니다."));
        
        return PhoneRestrictionExceptionDto.Response.from(exception);
    }
    
    public List<PhoneRestrictionExceptionDto.Response> getExceptionsByUserId(Long userId) {
        return phoneRestrictionExceptionRepository.findByUserId(userId).stream()
                .map(PhoneRestrictionExceptionDto.Response::from)
                .collect(Collectors.toList());
    }
    
    public List<PhoneRestrictionExceptionDto.Response> getExceptionsByStudySessionId(Long sessionId) {
        return phoneRestrictionExceptionRepository.findByStudySessionId(sessionId).stream()
                .map(PhoneRestrictionExceptionDto.Response::from)
                .collect(Collectors.toList());
    }
    
    public List<PhoneRestrictionExceptionDto.Response> getPendingExceptionsByGroupId(Long groupId) {
        return phoneRestrictionExceptionRepository.findPendingExceptionsByGroupId(groupId).stream()
                .map(PhoneRestrictionExceptionDto.Response::from)
                .collect(Collectors.toList());
    }
    
    public List<PhoneRestrictionExceptionDto.Response> getPendingExceptionsByLeaderId(Long leaderId) {
        return phoneRestrictionExceptionRepository.findPendingExceptionsByLeaderId(leaderId).stream()
                .map(PhoneRestrictionExceptionDto.Response::from)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public PhoneRestrictionExceptionDto.Response approveException(Long exceptionId, Long leaderId, 
                                                               PhoneRestrictionExceptionDto.ApprovalRequest request) {
        PhoneRestrictionException exception = phoneRestrictionExceptionRepository.findById(exceptionId)
                .orElseThrow(() -> new IllegalArgumentException("폰 사용 제한 예외를 찾을 수 없습니다."));
        
        // 리더 권한 확인
        StudyGroupMember leaderMember = studyGroupMemberRepository.findByUserIdAndStudyGroupId(
                leaderId, exception.getStudySession().getStudyGroup().getId())
                .orElseThrow(() -> new IllegalArgumentException("그룹 멤버가 아닙니다."));
        
        if (leaderMember.getRole() != StudyGroupMember.MemberRole.LEADER) {
            throw new IllegalArgumentException("그룹 리더만 예외를 승인할 수 있습니다.");
        }
        
        // 이미 처리된 예외인지 확인
        if (exception.getStatus() != PhoneRestrictionException.ExceptionStatus.PENDING) {
            throw new IllegalArgumentException("이미 처리된 예외입니다.");
        }
        
        // 승인/거절 처리
        exception.setStatus(request.getStatus());
        exception.setApprovedBy(userRepository.findById(leaderId).orElse(null));
        exception.setApprovedAt(LocalDateTime.now());
        
        PhoneRestrictionException updatedException = phoneRestrictionExceptionRepository.save(exception);
        
        String action = request.getStatus() == PhoneRestrictionException.ExceptionStatus.APPROVED ? "승인" : "거절";
        log.info("폰 사용 제한 예외가 {}되었습니다: {} - {}", action, 
                exception.getUser().getNickname(), exception.getStudySession().getSessionName());
        
        return PhoneRestrictionExceptionDto.Response.from(updatedException);
    }
    
    @Transactional
    public PhoneRestrictionExceptionDto.Response updateException(Long exceptionId, Long userId, 
                                                               PhoneRestrictionExceptionDto.UpdateRequest request) {
        PhoneRestrictionException exception = phoneRestrictionExceptionRepository.findById(exceptionId)
                .orElseThrow(() -> new IllegalArgumentException("폰 사용 제한 예외를 찾을 수 없습니다."));
        
        // 본인만 수정 가능
        if (!exception.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인만 예외 신청을 수정할 수 있습니다.");
        }
        
        // 이미 처리된 예외는 수정 불가
        if (exception.getStatus() != PhoneRestrictionException.ExceptionStatus.PENDING) {
            throw new IllegalArgumentException("이미 처리된 예외는 수정할 수 없습니다.");
        }
        
        // 시간 유효성 검사
        if (request.getExceptionStartTime() != null && request.getExceptionEndTime() != null) {
            if (request.getExceptionStartTime().isAfter(request.getExceptionEndTime())) {
                throw new IllegalArgumentException("예외 시작 시간은 종료 시간보다 빨라야 합니다.");
            }
            
            if (request.getExceptionStartTime().isBefore(exception.getStudySession().getStartTime()) || 
                request.getExceptionEndTime().isAfter(exception.getStudySession().getEndTime())) {
                throw new IllegalArgumentException("예외 시간은 스터디 세션 시간 내에 있어야 합니다.");
            }
        }
        
        exception.setReason(request.getReason());
        exception.setExceptionStartTime(request.getExceptionStartTime());
        exception.setExceptionEndTime(request.getExceptionEndTime());
        
        PhoneRestrictionException updatedException = phoneRestrictionExceptionRepository.save(exception);
        log.info("폰 사용 제한 예외가 수정되었습니다: {}", exceptionId);
        
        return PhoneRestrictionExceptionDto.Response.from(updatedException);
    }
    
    @Transactional
    public void deleteException(Long exceptionId, Long userId) {
        PhoneRestrictionException exception = phoneRestrictionExceptionRepository.findById(exceptionId)
                .orElseThrow(() -> new IllegalArgumentException("폰 사용 제한 예외를 찾을 수 없습니다."));
        
        // 본인만 삭제 가능
        if (!exception.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인만 예외 신청을 삭제할 수 있습니다.");
        }
        
        // 이미 처리된 예외는 삭제 불가
        if (exception.getStatus() != PhoneRestrictionException.ExceptionStatus.PENDING) {
            throw new IllegalArgumentException("이미 처리된 예외는 삭제할 수 없습니다.");
        }
        
        phoneRestrictionExceptionRepository.delete(exception);
        log.info("폰 사용 제한 예외가 삭제되었습니다: {}", exceptionId);
    }
    
    // 현재 활성화된 예외 조회 (폰 사용 제한 확인용)
    public List<PhoneRestrictionExceptionDto.Response> getActiveExceptions() {
        return phoneRestrictionExceptionRepository.findActiveExceptions(LocalDateTime.now()).stream()
                .map(PhoneRestrictionExceptionDto.Response::from)
                .collect(Collectors.toList());
    }
    
    // 만료된 예외 처리 (스케줄러용)
    @Transactional
    public void processExpiredExceptions() {
        List<PhoneRestrictionException> expiredExceptions = phoneRestrictionExceptionRepository
                .findExpiredExceptions(LocalDateTime.now());
        
        for (PhoneRestrictionException exception : expiredExceptions) {
            exception.setStatus(PhoneRestrictionException.ExceptionStatus.EXPIRED);
            phoneRestrictionExceptionRepository.save(exception);
            log.info("폰 사용 제한 예외가 만료되었습니다: {}", exception.getId());
        }
    }
    
    // 폰 사용 제한 상태 관련 메서드들
    
    /**
     * 사용자가 특정 세션에서 폰 사용이 제한되는지 확인
     */
    public boolean isUserPhoneRestrictedInSession(Long userId, Long sessionId) {
        // 해당 세션이 진행 중인지 확인
        StudySession session = studySessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("스터디 세션을 찾을 수 없습니다."));
        
        if (session.getStatus() != StudySession.SessionStatus.IN_PROGRESS) {
            return false; // 세션이 진행 중이 아니면 제한되지 않음
        }
        
        // 현재 시간이 세션 시간 내에 있는지 확인
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(session.getStartTime()) || now.isAfter(session.getEndTime())) {
            return false; // 세션 시간이 아니면 제한되지 않음
        }
        
        // 사용자가 해당 세션에 참여하고 있는지 확인 (출석 기록 기반)
        // TODO: AttendanceService와 연동하여 구현
        
        // 활성화된 폰 사용 제한 예외가 있는지 확인
        Optional<PhoneRestrictionException> activeException = phoneRestrictionExceptionRepository
                .findByUserIdAndStudySessionId(userId, sessionId);
        
        if (activeException.isPresent() && 
            activeException.get().getStatus() == PhoneRestrictionException.ExceptionStatus.APPROVED) {
            return false; // 승인된 예외가 있으면 제한되지 않음
        }
        
        return true; // 폰 사용 제한됨
    }
    
    /**
     * 사용자의 현재 전체 폰 사용 제한 상태 확인
     */
    public boolean isUserCurrentlyPhoneRestricted(Long userId) {
        // 현재 진행 중인 모든 세션에 대해 확인
        List<StudySession> currentSessions = studySessionRepository.findCurrentSessions(LocalDateTime.now());
        
        for (StudySession session : currentSessions) {
            if (isUserPhoneRestrictedInSession(userId, session.getId())) {
                return true; // 하나라도 제한되는 세션이 있으면 제한됨
            }
        }
        
        return false; // 제한되지 않음
    }
    
    /**
     * 세션별 폰 사용 제한 상태 요약
     */
    public PhoneRestrictionExceptionDto.SessionRestrictionSummary getSessionRestrictionSummary(Long sessionId) {
        StudySession session = studySessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("스터디 세션을 찾을 수 없습니다."));
        
        // 해당 세션의 모든 예외 신청 조회
        List<PhoneRestrictionException> exceptions = phoneRestrictionExceptionRepository
                .findByStudySessionId(sessionId);
        
        long pendingCount = exceptions.stream()
                .filter(e -> e.getStatus() == PhoneRestrictionException.ExceptionStatus.PENDING)
                .count();
        
        long approvedCount = exceptions.stream()
                .filter(e -> e.getStatus() == PhoneRestrictionException.ExceptionStatus.APPROVED)
                .count();
        
        long rejectedCount = exceptions.stream()
                .filter(e -> e.getStatus() == PhoneRestrictionException.ExceptionStatus.REJECTED)
                .count();
        
        return PhoneRestrictionExceptionDto.SessionRestrictionSummary.create(
                sessionId, 
                session.getSessionName(), 
                (int) exceptions.size(), 
                (int) pendingCount, 
                (int) approvedCount, 
                (int) rejectedCount, 
                session.getStatus() == StudySession.SessionStatus.IN_PROGRESS
        );
    }
} 