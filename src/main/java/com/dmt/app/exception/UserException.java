package com.dmt.app.exception;

/**
 * 사용자 관련 예외 클래스
 */
public class UserException {

    public static class UserNotFoundException extends BusinessException {
        public UserNotFoundException() {
            super(ErrorCode.USER_NOT_FOUND);
        }

        public UserNotFoundException(String message) {
            super(ErrorCode.USER_NOT_FOUND, message);
        }
    }

    public static class UserAlreadyExistsException extends BusinessException {
        public UserAlreadyExistsException() {
            super(ErrorCode.USER_ALREADY_EXISTS);
        }

        public UserAlreadyExistsException(String message) {
            super(ErrorCode.USER_ALREADY_EXISTS, message);
        }
    }

    public static class DuplicateEmailException extends BusinessException {
        public DuplicateEmailException() {
            super(ErrorCode.DUPLICATE_EMAIL);
        }

        public DuplicateEmailException(String email) {
            super(ErrorCode.DUPLICATE_EMAIL, "이미 사용 중인 이메일입니다: " + email);
        }
    }

    public static class DuplicateNicknameException extends BusinessException {
        public DuplicateNicknameException() {
            super(ErrorCode.DUPLICATE_NICKNAME);
        }

        public DuplicateNicknameException(String nickname) {
            super(ErrorCode.DUPLICATE_NICKNAME, "이미 사용 중인 닉네임입니다: " + nickname);
        }
    }

    public static class InvalidPasswordException extends BusinessException {
        public InvalidPasswordException() {
            super(ErrorCode.INVALID_PASSWORD);
        }

        public InvalidPasswordException(String message) {
            super(ErrorCode.INVALID_PASSWORD, message);
        }
    }
} 