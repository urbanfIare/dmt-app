package com.dmt.app.service;

import com.dmt.app.dto.AttendanceDto;
import com.dmt.app.entity.Attendance;
import com.dmt.app.entity.StudyGroupMember;
import com.dmt.app.entity.StudySession;
import com.dmt.app.entity.User;
import com.dmt.app.repository.AttendanceRepository;
import com.dmt.app.repository.StudyGroupMemberRepository;
import com.dmt.app.repository.StudySessionRepository;
import com.dmt.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AttendanceService {
    
    private final AttendanceRepository attendanceRepository;
    private final StudySessionRepository studySessionRepository;
    private final UserRepository userRepository;
    private final StudyGroupMemberRepository studyGroupMemberRepository;
    
    @Transactional
    public AttendanceDto.Response createAttendance(AttendanceDto.CreateRequest request) {
        // 사용자 존재 확인
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        // 스터디 세션 존재 확인
        StudySession studySession = studySessionRepository.findById(request.getStudySessionId())
                .orElseThrow(() -> new IllegalArgumentException("스터디 세션을 찾을 수 없습니다."));
        
        // 이미 출석 기록이 있는지 확인
        Optional<Attendance> existingAttendance = attendanceRepository.findByUserIdAndStudySessionId(
                request.getUserId(), request.getStudySessionId());
        
        if (existingAttendance.isPresent()) {
            throw new IllegalArgumentException("이미 출석 기록이 존재합니다.");
        }
        
        // 출석 상태에 따른 시간 설정
        LocalDateTime arrivalTime = null;
        LocalDateTime departureTime = null;
        Integer lateMinutes = null;
        Integer earlyLeaveMinutes = null;
        
        if (request.getStatus() == Attendance.AttendanceStatus.PRESENT) {
            arrivalTime = LocalDateTime.now();
            // 지각 여부 계산
            if (arrivalTime.isAfter(studySession.getStartTime())) {
                lateMinutes = (int) ChronoUnit.MINUTES.between(studySession.getStartTime(), arrivalTime);
            }
        } else if (request.getStatus() == Attendance.AttendanceStatus.LATE) {
            arrivalTime = LocalDateTime.now();
            lateMinutes = (int) ChronoUnit.MINUTES.between(studySession.getStartTime(), arrivalTime);
        }
        
        Attendance attendance = Attendance.builder()
                .user(user)
                .studySession(studySession)
                .status(request.getStatus())
                .arrivalTime(arrivalTime)
                .departureTime(departureTime)
                .lateMinutes(lateMinutes)
                .earlyLeaveMinutes(earlyLeaveMinutes)
                .note(request.getNote())
                .build();
        
        Attendance savedAttendance = attendanceRepository.save(attendance);
        log.info("출석 기록이 생성되었습니다: {} - {}", user.getNickname(), studySession.getSessionName());
        
        return AttendanceDto.Response.from(savedAttendance);
    }
    
    public AttendanceDto.Response getAttendanceById(Long attendanceId) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new IllegalArgumentException("출석 기록을 찾을 수 없습니다."));
        
        return AttendanceDto.Response.from(attendance);
    }
    
    public List<AttendanceDto.Response> getAttendancesByUserId(Long userId) {
        return attendanceRepository.findByUserId(userId).stream()
                .map(AttendanceDto.Response::from)
                .collect(Collectors.toList());
    }
    
    public List<AttendanceDto.Response> getAttendancesByStudySessionId(Long sessionId) {
        return attendanceRepository.findByStudySessionId(sessionId).stream()
                .map(AttendanceDto.Response::from)
                .collect(Collectors.toList());
    }
    
    public List<AttendanceDto.Response> getAttendancesByStudyGroupId(Long groupId) {
        return attendanceRepository.findByStudyGroupId(groupId).stream()
                .map(AttendanceDto.Response::from)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public AttendanceDto.Response updateAttendance(Long attendanceId, Long userId, AttendanceDto.UpdateRequest request) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new IllegalArgumentException("출석 기록을 찾을 수 없습니다."));
        
        // 본인 또는 그룹 리더만 수정 가능
        if (!attendance.getUser().getId().equals(userId)) {
            StudyGroupMember member = studyGroupMemberRepository.findByUserIdAndStudyGroupId(
                    userId, attendance.getStudySession().getStudyGroup().getId())
                    .orElseThrow(() -> new IllegalArgumentException("권한이 없습니다."));
            
            if (member.getRole() != StudyGroupMember.MemberRole.LEADER) {
                throw new IllegalArgumentException("본인 또는 그룹 리더만 출석 기록을 수정할 수 있습니다.");
            }
        }
        
        // 출석 상태 변경 시 시간 재계산
        if (request.getStatus() != attendance.getStatus()) {
            attendance.setStatus(request.getStatus());
            
            if (request.getStatus() == Attendance.AttendanceStatus.PRESENT || 
                request.getStatus() == Attendance.AttendanceStatus.LATE) {
                attendance.setArrivalTime(request.getArrivalTime() != null ? request.getArrivalTime() : LocalDateTime.now());
                
                // 지각 시간 계산
                if (attendance.getArrivalTime().isAfter(attendance.getStudySession().getStartTime())) {
                    attendance.setLateMinutes((int) ChronoUnit.MINUTES.between(
                            attendance.getStudySession().getStartTime(), attendance.getArrivalTime()));
                } else {
                    attendance.setLateMinutes(null);
                }
            } else if (request.getStatus() == Attendance.AttendanceStatus.EARLY_LEAVE) {
                attendance.setDepartureTime(request.getDepartureTime() != null ? request.getDepartureTime() : LocalDateTime.now());
                
                // 조퇴 시간 계산
                if (attendance.getDepartureTime().isBefore(attendance.getStudySession().getEndTime())) {
                    attendance.setEarlyLeaveMinutes((int) ChronoUnit.MINUTES.between(
                            attendance.getDepartureTime(), attendance.getStudySession().getEndTime()));
                }
            }
        }
        
        attendance.setNote(request.getNote());
        
        Attendance updatedAttendance = attendanceRepository.save(attendance);
        log.info("출석 기록이 수정되었습니다: {}", updatedAttendance.getId());
        
        return AttendanceDto.Response.from(updatedAttendance);
    }
    
    @Transactional
    public void deleteAttendance(Long attendanceId, Long userId) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new IllegalArgumentException("출석 기록을 찾을 수 없습니다."));
        
        // 본인 또는 그룹 리더만 삭제 가능
        if (!attendance.getUser().getId().equals(userId)) {
            StudyGroupMember member = studyGroupMemberRepository.findByUserIdAndStudyGroupId(
                    userId, attendance.getStudySession().getStudyGroup().getId())
                    .orElseThrow(() -> new IllegalArgumentException("권한이 없습니다."));
            
            if (member.getRole() != StudyGroupMember.MemberRole.LEADER) {
                throw new IllegalArgumentException("본인 또는 그룹 리더만 출석 기록을 삭제할 수 있습니다.");
            }
        }
        
        attendanceRepository.delete(attendance);
        log.info("출석 기록이 삭제되었습니다: {}", attendanceId);
    }
    
    // 출석률 통계 계산
    public AttendanceDto.AttendanceSummary getAttendanceSummary(Long userId, Long groupId) {
        List<Attendance> attendances = attendanceRepository.findByStudyGroupIdAndUserId(groupId, userId);
        
        long totalSessions = attendances.size();
        long presentCount = attendances.stream()
                .filter(a -> a.getStatus() == Attendance.AttendanceStatus.PRESENT)
                .count();
        long absentCount = attendances.stream()
                .filter(a -> a.getStatus() == Attendance.AttendanceStatus.ABSENT)
                .count();
        long lateCount = attendances.stream()
                .filter(a -> a.getStatus() == Attendance.AttendanceStatus.LATE)
                .count();
        long earlyLeaveCount = attendances.stream()
                .filter(a -> a.getStatus() == Attendance.AttendanceStatus.EARLY_LEAVE)
                .count();
        long excusedCount = attendances.stream()
                .filter(a -> a.getStatus() == Attendance.AttendanceStatus.EXCUSED)
                .count();
        
        double attendanceRate = totalSessions > 0 ? 
                (double) (presentCount + lateCount) / totalSessions * 100 : 0.0;
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        return AttendanceDto.AttendanceSummary.builder()
                .userId(userId)
                .userNickname(user.getNickname())
                .totalSessions(totalSessions)
                .presentCount(presentCount)
                .absentCount(absentCount)
                .lateCount(lateCount)
                .earlyLeaveCount(earlyLeaveCount)
                .excusedCount(excusedCount)
                .attendanceRate(Math.round(attendanceRate * 100.0) / 100.0)
                .build();
    }
    
    // 자동 출석 체크 (스케줄러용)
    @Transactional
    public void checkAttendanceForCurrentSessions() {
        List<StudySession> currentSessions = studySessionRepository.findCurrentSessions(LocalDateTime.now());
        
        for (StudySession session : currentSessions) {
            // 그룹의 모든 활성 멤버에 대해 출석 체크
            List<StudyGroupMember> activeMembers = studyGroupMemberRepository.findByStudyGroupIdAndIsActiveTrue(session.getStudyGroup().getId());
            
            for (StudyGroupMember member : activeMembers) {
                // 이미 출석 기록이 있는지 확인
                Optional<Attendance> existingAttendance = attendanceRepository.findByUserIdAndStudySessionId(
                        member.getUser().getId(), session.getId());
                
                if (existingAttendance.isEmpty()) {
                    // 자동으로 결석 처리
                    Attendance attendance = Attendance.builder()
                            .user(member.getUser())
                            .studySession(session)
                            .status(Attendance.AttendanceStatus.ABSENT)
                            .build();
                    
                    attendanceRepository.save(attendance);
                    log.info("자동 출석 체크: {} - {} (결석)", member.getUser().getNickname(), session.getSessionName());
                }
            }
        }
    }
} 