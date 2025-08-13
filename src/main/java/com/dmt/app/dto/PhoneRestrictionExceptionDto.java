package com.dmt.app.dto;

import com.dmt.app.entity.PhoneRestrictionException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class PhoneRestrictionExceptionDto {
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        private Long userId;
        private Long studySessionId;
        private String reason;
        private LocalDateTime exceptionStartTime;
        private LocalDateTime exceptionEndTime;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private String reason;
        private LocalDateTime exceptionStartTime;
        private LocalDateTime exceptionEndTime;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private Long userId;
        private String userNickname;
        private Long studySessionId;
        private String sessionName;
        private Long approvedById;
        private String approvedByNickname;
        private PhoneRestrictionException.ExceptionStatus status;
        private String reason;
        private LocalDateTime approvedAt;
        private LocalDateTime exceptionStartTime;
        private LocalDateTime exceptionEndTime;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        public static Response from(PhoneRestrictionException exception) {
            return Response.builder()
                    .id(exception.getId())
                    .userId(exception.getUser().getId())
                    .userNickname(exception.getUser().getNickname())
                    .studySessionId(exception.getStudySession().getId())
                    .sessionName(exception.getStudySession().getSessionName())
                    .approvedById(exception.getApprovedBy() != null ? exception.getApprovedBy().getId() : null)
                    .approvedByNickname(exception.getApprovedBy() != null ? exception.getApprovedBy().getNickname() : null)
                    .status(exception.getStatus())
                    .reason(exception.getReason())
                    .approvedAt(exception.getApprovedAt())
                    .exceptionStartTime(exception.getExceptionStartTime())
                    .exceptionEndTime(exception.getExceptionEndTime())
                    .createdAt(exception.getCreatedAt())
                    .updatedAt(exception.getUpdatedAt())
                    .build();
        }
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApprovalRequest {
        private PhoneRestrictionException.ExceptionStatus status;
        private String note;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SessionRestrictionSummary {
        private Long sessionId;
        private String sessionName;
        private int totalExceptions;
        private int pendingExceptions;
        private int approvedExceptions;
        private int rejectedExceptions;
        private boolean isSessionActive;
        private LocalDateTime summaryAt;
        
        public static SessionRestrictionSummary create(Long sessionId, String sessionName, 
                                                     int total, int pending, int approved, int rejected, 
                                                     boolean isActive) {
            return SessionRestrictionSummary.builder()
                    .sessionId(sessionId)
                    .sessionName(sessionName)
                    .totalExceptions(total)
                    .pendingExceptions(pending)
                    .approvedExceptions(approved)
                    .rejectedExceptions(rejected)
                    .isSessionActive(isActive)
                    .summaryAt(LocalDateTime.now())
                    .build();
        }
    }
} 