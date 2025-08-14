package com.dmt.app.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * API 에러 코드 정의
 * 일관된 에러 코드와 메시지를 제공합니다.
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 공통 에러 (1000번대)
    INVALID_INPUT_VALUE(1000, "잘못된 입력값입니다.", HttpStatus.BAD_REQUEST),
    METHOD_NOT_ALLOWED(1001, "지원하지 않는 HTTP 메서드입니다.", HttpStatus.METHOD_NOT_ALLOWED),
    ENTITY_NOT_FOUND(1002, "요청한 리소스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INTERNAL_SERVER_ERROR(1003, "서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_TYPE_VALUE(1004, "잘못된 타입의 값입니다.", HttpStatus.BAD_REQUEST),
    HANDLE_ACCESS_DENIED(1005, "접근이 거부되었습니다.", HttpStatus.FORBIDDEN),
    UNAUTHORIZED(1006, "인증이 필요합니다.", HttpStatus.UNAUTHORIZED),

    // 사용자 관련 에러 (2000번대)
    USER_NOT_FOUND(2000, "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS(2001, "이미 존재하는 사용자입니다.", HttpStatus.CONFLICT),
    DUPLICATE_EMAIL(2002, "이미 사용 중인 이메일입니다.", HttpStatus.CONFLICT),
    DUPLICATE_NICKNAME(2003, "이미 사용 중인 닉네임입니다.", HttpStatus.CONFLICT),
    INVALID_PASSWORD(2004, "비밀번호가 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    PASSWORD_MISMATCH(2005, "비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),

    // 스터디 그룹 관련 에러 (3000번대)
    STUDY_GROUP_NOT_FOUND(3000, "스터디 그룹을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    STUDY_GROUP_ALREADY_EXISTS(3001, "이미 존재하는 스터디 그룹입니다.", HttpStatus.CONFLICT),
    NOT_GROUP_MEMBER(3002, "그룹 멤버가 아닙니다.", HttpStatus.FORBIDDEN),
    NOT_GROUP_LEADER(3003, "그룹 리더가 아닙니다.", HttpStatus.FORBIDDEN),
    GROUP_FULL(3004, "그룹 인원이 가득 찼습니다.", HttpStatus.BAD_REQUEST),
    GROUP_MIN_MEMBERS_NOT_MET(3005, "최소 인원 요구사항을 충족하지 못합니다.", HttpStatus.BAD_REQUEST),

    // 스터디 세션 관련 에러 (4000번대)
    STUDY_SESSION_NOT_FOUND(4000, "스터디 세션을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_SESSION_TIME(4001, "잘못된 세션 시간입니다.", HttpStatus.BAD_REQUEST),
    SESSION_ALREADY_STARTED(4002, "이미 시작된 세션입니다.", HttpStatus.BAD_REQUEST),
    SESSION_ALREADY_ENDED(4003, "이미 종료된 세션입니다.", HttpStatus.BAD_REQUEST),
    SESSION_TIME_CONFLICT(4004, "세션 시간이 겹칩니다.", HttpStatus.BAD_REQUEST),

    // 출석 관련 에러 (5000번대)
    ATTENDANCE_NOT_FOUND(5000, "출석 기록을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ATTENDANCE_ALREADY_EXISTS(5001, "이미 출석 기록이 존재합니다.", HttpStatus.CONFLICT),
    INVALID_ATTENDANCE_STATUS(5002, "잘못된 출석 상태입니다.", HttpStatus.BAD_REQUEST),

    // 폰 사용 제한 관련 에러 (6000번대)
    PHONE_RESTRICTION_EXCEPTION_NOT_FOUND(6000, "폰 사용 제한 예외를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    EXCEPTION_ALREADY_APPROVED(6001, "이미 승인된 예외입니다.", HttpStatus.CONFLICT),
    EXCEPTION_ALREADY_REJECTED(6002, "이미 거부된 예외입니다.", HttpStatus.CONFLICT),
    EXCEPTION_EXPIRED(6003, "만료된 예외입니다.", HttpStatus.BAD_REQUEST),

    // 알림 관련 에러 (7000번대)
    NOTIFICATION_NOT_FOUND(7000, "알림을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    NOTIFICATION_SEND_FAILED(7001, "알림 전송에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    // 파일 관련 에러 (8000번대)
    FILE_NOT_FOUND(8000, "파일을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    FILE_UPLOAD_FAILED(8001, "파일 업로드에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_FILE_TYPE(8002, "지원하지 않는 파일 형식입니다.", HttpStatus.BAD_REQUEST),
    FILE_TOO_LARGE(8003, "파일 크기가 너무 큽니다.", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    /**
     * 에러 코드로 ErrorCode 찾기
     */
    public static ErrorCode findByCode(int code) {
        for (ErrorCode errorCode : ErrorCode.values()) {
            if (errorCode.getCode() == code) {
                return errorCode;
            }
        }
        throw new IllegalArgumentException("Unknown error code: " + code);
    }
} 