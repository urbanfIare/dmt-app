package com.dmt.app.service;

import com.dmt.app.dto.NotificationDto;
import com.dmt.app.entity.StudyGroupMember;
import com.dmt.app.entity.StudySession;
import com.dmt.app.entity.User;
import com.dmt.app.repository.StudyGroupMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final StudyGroupMemberRepository studyGroupMemberRepository;

    /**
     * 특정 사용자에게 개인 알림 전송
     */
    public void sendPersonalNotification(Long userId, NotificationDto.NotificationMessage notification) {
        String destination = "/user/" + userId + "/queue/notifications";
        messagingTemplate.convertAndSendToUser(
            userId.toString(),
            "/queue/notifications",
            notification
        );
        log.info("개인 알림 전송 완료: 사용자 {} - {}", userId, notification.getTitle());
    }

    /**
     * 스터디 그룹 전체에 알림 전송
     */
    public void sendGroupNotification(Long groupId, NotificationDto.NotificationMessage notification) {
        String destination = "/topic/study-group/" + groupId;
        messagingTemplate.convertAndSend(destination, notification);
        log.info("그룹 알림 전송 완료: 그룹 {} - {}", groupId, notification.getTitle());
    }

    /**
     * 스터디 세션 시작 알림
     */
    public void notifySessionStart(StudySession session) {
        NotificationDto.NotificationMessage notification = NotificationDto.NotificationMessage.builder()
                .id(UUID.randomUUID().toString())
                .type(NotificationDto.NotificationType.SESSION_START)
                .title("스터디 세션이 시작되었습니다")
                .message(session.getSessionName() + " 세션이 시작되었습니다. 폰 사용이 제한됩니다.")
                .studyGroupId(session.getStudyGroup().getId())
                .studySessionId(session.getId())
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .priority(NotificationDto.NotificationPriority.HIGH)
                .build();

        // 그룹 전체에 알림
        sendGroupNotification(session.getStudyGroup().getId(), notification);
        
        // 개별 사용자에게 폰 사용 제한 알림
        notifyPhoneRestrictionOn(session);
    }

    /**
     * 스터디 세션 종료 알림
     */
    public void notifySessionEnd(StudySession session) {
        NotificationDto.NotificationMessage notification = NotificationDto.NotificationMessage.builder()
                .id(UUID.randomUUID().toString())
                .type(NotificationDto.NotificationType.SESSION_END)
                .title("스터디 세션이 종료되었습니다")
                .message(session.getSessionName() + " 세션이 종료되었습니다. 폰 사용 제한이 해제됩니다.")
                .studyGroupId(session.getStudyGroup().getId())
                .studySessionId(session.getId())
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .priority(NotificationDto.NotificationPriority.NORMAL)
                .build();

        // 그룹 전체에 알림
        sendGroupNotification(session.getStudyGroup().getId(), notification);
        
        // 개별 사용자에게 폰 사용 제한 해제 알림
        notifyPhoneRestrictionOff(session);
    }

    /**
     * 폰 사용 제한 시작 알림
     */
    public void notifyPhoneRestrictionOn(StudySession session) {
        // 그룹의 모든 멤버에게 개별 알림
        List<StudyGroupMember> members = studyGroupMemberRepository.findByStudyGroupId(session.getStudyGroup().getId());
        
        for (StudyGroupMember member : members) {
            NotificationDto.NotificationMessage notification = NotificationDto.NotificationMessage.builder()
                    .id(UUID.randomUUID().toString())
                    .type(NotificationDto.NotificationType.PHONE_RESTRICTION_ON)
                    .title("폰 사용 제한 시작")
                    .message(session.getSessionName() + " 세션 중 폰 사용이 제한됩니다.")
                    .userId(member.getUser().getId())
                    .studyGroupId(session.getStudyGroup().getId())
                    .studySessionId(session.getId())
                    .createdAt(LocalDateTime.now())
                    .isRead(false)
                    .priority(NotificationDto.NotificationPriority.HIGH)
                    .build();

            sendPersonalNotification(member.getUser().getId(), notification);
        }
    }

    /**
     * 폰 사용 제한 해제 알림
     */
    public void notifyPhoneRestrictionOff(StudySession session) {
        // 그룹의 모든 멤버에게 개별 알림
        List<StudyGroupMember> members = studyGroupMemberRepository.findByStudyGroupId(session.getStudyGroup().getId());
        
        for (StudyGroupMember member : members) {
            NotificationDto.NotificationMessage notification = NotificationDto.NotificationMessage.builder()
                    .id(UUID.randomUUID().toString())
                    .type(NotificationDto.NotificationType.PHONE_RESTRICTION_OFF)
                    .title("폰 사용 제한 해제")
                    .message(session.getSessionName() + " 세션이 종료되어 폰 사용 제한이 해제되었습니다.")
                    .userId(member.getUser().getId())
                    .studyGroupId(session.getStudyGroup().getId())
                    .studySessionId(session.getId())
                    .createdAt(LocalDateTime.now())
                    .isRead(false)
                    .priority(NotificationDto.NotificationPriority.NORMAL)
                    .build();

            sendPersonalNotification(member.getUser().getId(), notification);
        }
    }

    /**
     * 폰 사용 제한 예외 신청 알림 (리더에게)
     */
    public void notifyExceptionRequested(User user, StudySession session) {
        // 그룹 리더에게 알림
        StudyGroupMember leader = studyGroupMemberRepository.findLeaderByStudyGroupId(session.getStudyGroup().getId())
                .orElse(null);
        
        if (leader != null) {
            NotificationDto.NotificationMessage notification = NotificationDto.NotificationMessage.builder()
                    .id(UUID.randomUUID().toString())
                    .type(NotificationDto.NotificationType.EXCEPTION_REQUESTED)
                    .title("폰 사용 제한 예외 신청")
                    .message(user.getNickname() + "님이 폰 사용 제한 예외를 신청했습니다.")
                    .userId(leader.getUser().getId())
                    .studyGroupId(session.getStudyGroup().getId())
                    .studySessionId(session.getId())
                    .createdAt(LocalDateTime.now())
                    .isRead(false)
                    .priority(NotificationDto.NotificationPriority.HIGH)
                    .build();

            sendPersonalNotification(leader.getUser().getId(), notification);
        }
    }

    /**
     * 폰 사용 제한 예외 처리 결과 알림 (신청자에게)
     */
    public void notifyExceptionProcessed(User user, StudySession session, String result, String note) {
        String message = result.equals("APPROVED") ? 
            "폰 사용 제한 예외가 승인되었습니다." : 
            "폰 사용 제한 예외가 거절되었습니다.";
        
        if (note != null && !note.trim().isEmpty()) {
            message += " 사유: " + note;
        }

        NotificationDto.NotificationMessage notification = NotificationDto.NotificationMessage.builder()
                .id(UUID.randomUUID().toString())
                .type(result.equals("APPROVED") ? 
                    NotificationDto.NotificationType.EXCEPTION_APPROVED : 
                    NotificationDto.NotificationType.EXCEPTION_REJECTED)
                .title("폰 사용 제한 예외 처리 완료")
                .message(message)
                .userId(user.getId())
                .studyGroupId(session.getStudyGroup().getId())
                .studySessionId(session.getId())
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .priority(NotificationDto.NotificationPriority.HIGH)
                .build();

        sendPersonalNotification(user.getId(), notification);
    }

    /**
     * 출석 알림 (세션 시작 5분 전)
     */
    public void notifyAttendanceReminder(StudySession session) {
        NotificationDto.NotificationMessage notification = NotificationDto.NotificationMessage.builder()
                .id(UUID.randomUUID().toString())
                .type(NotificationDto.NotificationType.ATTENDANCE_REMINDER)
                .title("출석 알림")
                .message(session.getSessionName() + " 세션이 5분 후 시작됩니다. 준비해주세요.")
                .studyGroupId(session.getStudyGroup().getId())
                .studySessionId(session.getId())
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .priority(NotificationDto.NotificationPriority.NORMAL)
                .build();

        sendGroupNotification(session.getStudyGroup().getId(), notification);
    }

    /**
     * 일반 알림 전송
     */
    public void sendGeneralNotification(Long groupId, String title, String message, NotificationDto.NotificationPriority priority) {
        NotificationDto.NotificationMessage notification = NotificationDto.NotificationMessage.builder()
                .id(UUID.randomUUID().toString())
                .type(NotificationDto.NotificationType.GENERAL)
                .title(title)
                .message(message)
                .studyGroupId(groupId)
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .priority(priority)
                .build();

        sendGroupNotification(groupId, notification);
    }
} 