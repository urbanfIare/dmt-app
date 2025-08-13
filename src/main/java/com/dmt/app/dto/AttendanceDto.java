package com.dmt.app.dto;

import com.dmt.app.entity.Attendance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class AttendanceDto {
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        private Long userId;
        private Long studySessionId;
        private Attendance.AttendanceStatus status;
        private String note;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private Attendance.AttendanceStatus status;
        private LocalDateTime arrivalTime;
        private LocalDateTime departureTime;
        private String note;
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
        private Attendance.AttendanceStatus status;
        private LocalDateTime arrivalTime;
        private LocalDateTime departureTime;
        private Integer lateMinutes;
        private Integer earlyLeaveMinutes;
        private String note;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        public static Response from(Attendance attendance) {
            return Response.builder()
                    .id(attendance.getId())
                    .userId(attendance.getUser().getId())
                    .userNickname(attendance.getUser().getNickname())
                    .studySessionId(attendance.getStudySession().getId())
                    .sessionName(attendance.getStudySession().getSessionName())
                    .status(attendance.getStatus())
                    .arrivalTime(attendance.getArrivalTime())
                    .departureTime(attendance.getDepartureTime())
                    .lateMinutes(attendance.getLateMinutes())
                    .earlyLeaveMinutes(attendance.getEarlyLeaveMinutes())
                    .note(attendance.getNote())
                    .createdAt(attendance.getCreatedAt())
                    .updatedAt(attendance.getUpdatedAt())
                    .build();
        }
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttendanceSummary {
        private Long userId;
        private String userNickname;
        private Long totalSessions;
        private Long presentCount;
        private Long absentCount;
        private Long lateCount;
        private Long earlyLeaveCount;
        private Long excusedCount;
        private Double attendanceRate;
    }
} 