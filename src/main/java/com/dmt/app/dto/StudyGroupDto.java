package com.dmt.app.dto;

import com.dmt.app.entity.StudyGroup;
import com.dmt.app.entity.StudyGroupMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class StudyGroupDto {
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        private String name;
        private String description;
        private Integer maxMembers;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private String name;
        private String description;
        private Integer maxMembers;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String name;
        private String description;
        private Integer maxMembers;
        private Integer minMembers;
        private StudyGroup.StudyGroupStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<MemberResponse> members;
        
        public static Response from(StudyGroup studyGroup) {
            return Response.builder()
                    .id(studyGroup.getId())
                    .name(studyGroup.getName())
                    .description(studyGroup.getDescription())
                    .maxMembers(studyGroup.getMaxMembers())
                    .minMembers(studyGroup.getMinMembers())
                    .status(studyGroup.getStatus())
                    .createdAt(studyGroup.getCreatedAt())
                    .updatedAt(studyGroup.getUpdatedAt())
                    .members(studyGroup.getMembers().stream()
                            .map(MemberResponse::from)
                            .collect(Collectors.toList()))
                    .build();
        }
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberResponse {
        private Long id;
        private UserDto.Response user;
        private StudyGroupMember.MemberRole role;
        private LocalDateTime joinedAt;
        private Boolean isActive;
        
        public static MemberResponse from(StudyGroupMember member) {
            return MemberResponse.builder()
                    .id(member.getId())
                    .user(UserDto.Response.from(member.getUser()))
                    .role(member.getRole())
                    .joinedAt(member.getJoinedAt())
                    .isActive(member.getIsActive())
                    .build();
        }
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JoinRequest {
        private Long groupId;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InviteRequest {
        private Long groupId;
        private String userEmail;
    }
} 