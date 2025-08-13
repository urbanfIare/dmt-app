package com.dmt.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class NotificationDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "실시간 알림 메시지")
    public static class NotificationMessage {
        @Schema(description = "알림 ID", example = "1")
        private String id;
        
        @Schema(description = "알림 타입", example = "SESSION_START", required = true)
        private NotificationType type;
        
        @Schema(description = "알림 제목", example = "스터디 세션이 시작되었습니다", required = true)
        private String title;
        
        @Schema(description = "알림 내용", example = "오후 2시 스터디 세션이 시작되었습니다", required = true)
        private String message;
        
        @Schema(description = "관련 사용자 ID", example = "1")
        private Long userId;
        
        @Schema(description = "관련 스터디 그룹 ID", example = "1")
        private Long studyGroupId;
        
        @Schema(description = "관련 스터디 세션 ID", example = "1")
        private Long studySessionId;
        
        @Schema(description = "알림 생성 시간")
        private LocalDateTime createdAt;
        
        @Schema(description = "읽음 여부", example = "false")
        private boolean isRead;
        
        @Schema(description = "알림 우선순위", example = "HIGH")
        private NotificationPriority priority;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "폰 사용 제한 상태 변경 알림")
    public static class PhoneRestrictionStatusChange {
        @Schema(description = "사용자 ID", example = "1", required = true)
        private Long userId;
        
        @Schema(description = "스터디 세션 ID", example = "1", required = true)
        private Long studySessionId;
        
        @Schema(description = "변경된 상태", example = "RESTRICTED", required = true)
        private String status;
        
        @Schema(description = "상태 변경 시간")
        private LocalDateTime changedAt;
        
        @Schema(description = "알림 메시지", example = "폰 사용이 제한되었습니다")
        private String message;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "스터디 세션 상태 변경 알림")
    public static class SessionStatusChange {
        @Schema(description = "스터디 세션 ID", example = "1", required = true)
        private Long studySessionId;
        
        @Schema(description = "세션 이름", example = "오후 스터디", required = true)
        private String sessionName;
        
        @Schema(description = "이전 상태", example = "SCHEDULED")
        private String previousStatus;
        
        @Schema(description = "새로운 상태", example = "IN_PROGRESS", required = true)
        private String newStatus;
        
        @Schema(description = "상태 변경 시간")
        private LocalDateTime changedAt;
        
        @Schema(description = "알림 메시지", example = "스터디 세션이 시작되었습니다")
        private String message;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "폰 사용 제한 예외 처리 알림")
    public static class ExceptionProcessed {
        @Schema(description = "사용자 ID", example = "1", required = true)
        private Long userId;
        
        @Schema(description = "사용자 닉네임", example = "테스트유저", required = true)
        private String userNickname;
        
        @Schema(description = "스터디 세션 ID", example = "1", required = true)
        private Long studySessionId;
        
        @Schema(description = "처리 결과", example = "APPROVED", required = true)
        private String result;
        
        @Schema(description = "처리 시간")
        private LocalDateTime processedAt;
        
        @Schema(description = "알림 메시지", example = "폰 사용 제한 예외가 승인되었습니다")
        private String message;
    }

    // 알림 타입 열거형
    public enum NotificationType {
        SESSION_START,           // 세션 시작
        SESSION_END,             // 세션 종료
        SESSION_CANCELLED,       // 세션 취소
        PHONE_RESTRICTION_ON,    // 폰 사용 제한 시작
        PHONE_RESTRICTION_OFF,   // 폰 사용 제한 해제
        EXCEPTION_REQUESTED,     // 예외 신청
        EXCEPTION_APPROVED,      // 예외 승인
        EXCEPTION_REJECTED,      // 예외 거절
        EXCEPTION_EXPIRED,       // 예외 만료
        ATTENDANCE_REMINDER,     // 출석 알림
        GENERAL                  // 일반 알림
    }

    // 알림 우선순위 열거형
    public enum NotificationPriority {
        LOW,        // 낮음
        NORMAL,     // 보통
        HIGH,       // 높음
        URGENT      // 긴급
    }
} 