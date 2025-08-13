package com.dmt.app.scheduler;

import com.dmt.app.service.StudySessionService;
import com.dmt.app.service.AttendanceService;
import com.dmt.app.service.PhoneRestrictionExceptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StudySessionScheduler {
    
    private final StudySessionService studySessionService;
    private final AttendanceService attendanceService;
    private final PhoneRestrictionExceptionService phoneRestrictionExceptionService;
    
    /**
     * 매분마다 실행 - 예정된 세션을 진행 상태로 변경
     */
    @Scheduled(fixedRate = 60000) // 1분 = 60,000ms
    public void startScheduledSessions() {
        try {
            log.info("스케줄러: 예정된 세션 시작 처리 시작");
            studySessionService.startScheduledSessions();
            log.info("스케줄러: 예정된 세션 시작 처리 완료");
        } catch (Exception e) {
            log.error("스케줄러: 예정된 세션 시작 처리 중 오류 발생", e);
        }
    }
    
    /**
     * 매분마다 실행 - 현재 진행 중인 세션에 대한 자동 출석 체크
     */
    @Scheduled(fixedRate = 60000) // 1분 = 60,000ms
    public void checkAttendanceForCurrentSessions() {
        try {
            log.info("스케줄러: 현재 세션 출석 체크 시작");
            attendanceService.checkAttendanceForCurrentSessions();
            log.info("스케줄러: 현재 세션 출석 체크 완료");
        } catch (Exception e) {
            log.error("스케줄러: 현재 세션 출석 체크 중 오류 발생", e);
        }
    }
    
    /**
     * 매시간마다 실행 - 만료된 폰 사용 제한 예외 처리
     */
    @Scheduled(fixedRate = 3600000) // 1시간 = 3,600,000ms
    public void processExpiredPhoneExceptions() {
        try {
            log.info("스케줄러: 만료된 폰 사용 제한 예외 처리 시작");
            phoneRestrictionExceptionService.processExpiredExceptions();
            log.info("스케줄러: 만료된 폰 사용 제한 예외 처리 완료");
        } catch (Exception e) {
            log.error("스케줄러: 만료된 폰 사용 제한 예외 처리 중 오류 발생", e);
        }
    }
    
    /**
     * 매 5분마다 실행 - 폰 사용 제한 상태 모니터링 및 알림
     */
    @Scheduled(fixedRate = 300000) // 5분 = 300,000ms
    public void monitorPhoneRestrictionStatus() {
        try {
            log.info("스케줄러: 폰 사용 제한 상태 모니터링 시작");
            // TODO: 현재 진행 중인 세션들의 폰 사용 제한 상태 확인
            // TODO: 제한 상태 변경 시 알림 발송
            log.info("스케줄러: 폰 사용 제한 상태 모니터링 완료");
        } catch (Exception e) {
            log.error("스케줄러: 폰 사용 제한 상태 모니터링 중 오류 발생", e);
        }
    }
    
    /**
     * 매 30분마다 실행 - 세션 상태 동기화 및 폰 사용 제한 적용
     */
    @Scheduled(fixedRate = 1800000) // 30분 = 1,800,000ms
    public void syncSessionStatusAndPhoneRestriction() {
        try {
            log.info("스케줄러: 세션 상태 동기화 및 폰 사용 제한 적용 시작");
            // TODO: 세션 상태와 폰 사용 제한 상태 동기화
            // TODO: 시간이 지난 세션 자동 종료
            log.info("스케줄러: 세션 상태 동기화 및 폰 사용 제한 적용 완료");
        } catch (Exception e) {
            log.error("스케줄러: 세션 상태 동기화 및 폰 사용 제한 적용 중 오류 발생", e);
        }
    }
    
    /**
     * 매일 자정에 실행 - 전날 세션 정리 및 통계 생성
     */
    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정
    public void dailySessionCleanup() {
        try {
            log.info("스케줄러: 일일 세션 정리 시작");
            // TODO: 전날 완료된 세션들의 통계 생성 및 정리 작업
            log.info("스케줄러: 일일 세션 정리 완료");
        } catch (Exception e) {
            log.error("스케줄러: 일일 세션 정리 중 오류 발생", e);
        }
    }
} 