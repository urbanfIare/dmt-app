package com.dmt.app.exception;

/**
 * 스터디 세션 관련 예외 클래스
 */
public class StudySessionException {

    public static class StudySessionNotFoundException extends BusinessException {
        public StudySessionNotFoundException() {
            super(ErrorCode.STUDY_SESSION_NOT_FOUND);
        }

        public StudySessionNotFoundException(Long sessionId) {
            super(ErrorCode.STUDY_SESSION_NOT_FOUND, "스터디 세션을 찾을 수 없습니다: " + sessionId);
        }
    }

    public static class InvalidSessionTimeException extends BusinessException {
        public InvalidSessionTimeException() {
            super(ErrorCode.INVALID_SESSION_TIME);
        }

        public InvalidSessionTimeException(String message) {
            super(ErrorCode.INVALID_SESSION_TIME, message);
        }
    }

    public static class SessionAlreadyStartedException extends BusinessException {
        public SessionAlreadyStartedException() {
            super(ErrorCode.SESSION_ALREADY_STARTED);
        }

        public SessionAlreadyStartedException(String sessionName) {
            super(ErrorCode.SESSION_ALREADY_STARTED, "이미 시작된 세션입니다: " + sessionName);
        }
    }

    public static class SessionAlreadyEndedException extends BusinessException {
        public SessionAlreadyEndedException() {
            super(ErrorCode.SESSION_ALREADY_ENDED);
        }

        public SessionAlreadyEndedException(String sessionName) {
            super(ErrorCode.SESSION_ALREADY_ENDED, "이미 종료된 세션입니다: " + sessionName);
        }
    }

    public static class SessionTimeConflictException extends BusinessException {
        public SessionTimeConflictException() {
            super(ErrorCode.SESSION_TIME_CONFLICT);
        }

        public SessionTimeConflictException(String message) {
            super(ErrorCode.SESSION_TIME_CONFLICT, message);
        }
    }
} 