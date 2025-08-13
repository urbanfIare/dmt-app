package com.dmt.app.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    
    // 사용자 관련 에러
    USER_NOT_FOUND("U001", "사용자를 찾을 수 없습니다."),
    USER_ALREADY_EXISTS("U002", "이미 존재하는 사용자입니다."),
    EMAIL_ALREADY_EXISTS("U003", "이미 존재하는 이메일입니다."),
    NICKNAME_ALREADY_EXISTS("U004", "이미 존재하는 닉네임입니다."),
    
    // 스터디 그룹 관련 에러
    STUDY_GROUP_NOT_FOUND("G001", "스터디 그룹을 찾을 수 없습니다."),
    STUDY_GROUP_FULL("G002", "그룹 인원이 가득 찼습니다."),
    STUDY_GROUP_INACTIVE("G003", "비활성화된 그룹입니다."),
    NOT_GROUP_MEMBER("G004", "그룹 멤버가 아닙니다."),
    NOT_GROUP_LEADER("G005", "그룹 리더가 아닙니다."),
    
    // 스터디 세션 관련 에러
    STUDY_SESSION_NOT_FOUND("S001", "스터디 세션을 찾을 수 없습니다."),
    SESSION_ALREADY_STARTED("S002", "이미 시작된 세션입니다."),
    SESSION_ALREADY_COMPLETED("S003", "이미 완료된 세션입니다."),
    INVALID_SESSION_TIME("S004", "유효하지 않은 세션 시간입니다."),
    
    // 출석 관련 에러
    ATTENDANCE_ALREADY_EXISTS("A001", "이미 출석 기록이 존재합니다."),
    ATTENDANCE_NOT_FOUND("A002", "출석 기록을 찾을 수 없습니다."),
    INVALID_ATTENDANCE_STATUS("A003", "유효하지 않은 출석 상태입니다."),
    
    // 폰 사용 제한 예외 관련 에러
    PHONE_EXCEPTION_ALREADY_EXISTS("P001", "이미 폰 사용 제한 예외를 신청했습니다."),
    PHONE_EXCEPTION_NOT_FOUND("P002", "폰 사용 제한 예외를 찾을 수 없습니다."),
    PHONE_EXCEPTION_ALREADY_PROCESSED("P003", "이미 처리된 예외입니다."),
    INVALID_EXCEPTION_TIME("P004", "유효하지 않은 예외 시간입니다."),
    
    // 권한 관련 에러
    UNAUTHORIZED("AUTH001", "인증이 필요합니다."),
    FORBIDDEN("AUTH002", "접근 권한이 없습니다."),
    
    // 일반 에러
    INVALID_INPUT("C001", "유효하지 않은 입력입니다."),
    RESOURCE_NOT_FOUND("C002", "리소스를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR("C003", "서버 내부 오류가 발생했습니다.");
    
    private final String code;
    private final String message;
} 