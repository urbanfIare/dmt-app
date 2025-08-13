package com.dmt.app.dto;

import com.dmt.app.entity.StudySession;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class StudySessionDto {
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        private Long studyGroupId;
        private String sessionName;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Integer durationMinutes;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private String sessionName;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Integer durationMinutes;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private Long studyGroupId;
        private String sessionName;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Integer durationMinutes;
        private StudySession.SessionStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        public static Response from(StudySession studySession) {
            return Response.builder()
                    .id(studySession.getId())
                    .studyGroupId(studySession.getStudyGroup().getId())
                    .sessionName(studySession.getSessionName())
                    .startTime(studySession.getStartTime())
                    .endTime(studySession.getEndTime())
                    .durationMinutes(studySession.getDurationMinutes())
                    .status(studySession.getStatus())
                    .createdAt(studySession.getCreatedAt())
                    .updatedAt(studySession.getUpdatedAt())
                    .build();
        }
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusUpdateRequest {
        private StudySession.SessionStatus status;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PhoneRestrictionStatus {
        private Long sessionId;
        private boolean isRestricted;
        private String message;
        private LocalDateTime checkedAt;
        
        public static PhoneRestrictionStatus create(Long sessionId, boolean isRestricted, String message) {
            return PhoneRestrictionStatus.builder()
                    .sessionId(sessionId)
                    .isRestricted(isRestricted)
                    .message(message)
                    .checkedAt(LocalDateTime.now())
                    .build();
        }
    }
} 