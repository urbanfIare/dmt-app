package com.dmt.app.exception;

/**
 * 스터디 그룹 관련 예외 클래스
 */
public class StudyGroupException {

    public static class StudyGroupNotFoundException extends BusinessException {
        public StudyGroupNotFoundException() {
            super(ErrorCode.STUDY_GROUP_NOT_FOUND);
        }

        public StudyGroupNotFoundException(Long groupId) {
            super(ErrorCode.STUDY_GROUP_NOT_FOUND, "스터디 그룹을 찾을 수 없습니다: " + groupId);
        }
    }

    public static class NotGroupMemberException extends BusinessException {
        public NotGroupMemberException() {
            super(ErrorCode.NOT_GROUP_MEMBER);
        }

        public NotGroupMemberException(Long userId, Long groupId) {
            super(ErrorCode.NOT_GROUP_MEMBER, 
                  String.format("사용자 %d는 그룹 %d의 멤버가 아닙니다.", userId, groupId));
        }
    }

    public static class NotGroupLeaderException extends BusinessException {
        public NotGroupLeaderException() {
            super(ErrorCode.NOT_GROUP_LEADER);
        }

        public NotGroupLeaderException(Long userId, Long groupId) {
            super(ErrorCode.NOT_GROUP_LEADER, 
                  String.format("사용자 %d는 그룹 %d의 리더가 아닙니다.", userId, groupId));
        }
    }

    public static class GroupFullException extends BusinessException {
        public GroupFullException() {
            super(ErrorCode.GROUP_FULL);
        }

        public GroupFullException(Long groupId, int currentMembers, int maxMembers) {
            super(ErrorCode.GROUP_FULL, 
                  String.format("그룹 %d의 인원이 가득 찼습니다. (현재: %d, 최대: %d)", 
                               groupId, currentMembers, maxMembers));
        }
    }

    public static class GroupMinMembersNotMetException extends BusinessException {
        public GroupMinMembersNotMetException() {
            super(ErrorCode.GROUP_MIN_MEMBERS_NOT_MET);
        }

        public GroupMinMembersNotMetException(int currentMembers, int minMembers) {
            super(ErrorCode.GROUP_MIN_MEMBERS_NOT_MET, 
                  String.format("최소 인원 요구사항을 충족하지 못합니다. (현재: %d, 최소: %d)", 
                               currentMembers, minMembers));
        }
    }
} 