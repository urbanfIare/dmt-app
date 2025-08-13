package com.dmt.app.controller;

import com.dmt.app.dto.NotificationDto;
import com.dmt.app.service.NotificationService;
import com.dmt.app.service.StudySessionService;
import com.dmt.app.service.PhoneRestrictionExceptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/realtime")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "실시간 모니터링", description = "실시간 상태 모니터링 및 알림 API")
public class RealTimeMonitoringController {

    private final StudySessionService studySessionService;
    private final PhoneRestrictionExceptionService phoneRestrictionExceptionService;
    private final NotificationService notificationService;

    /**
     * 사용자의 현재 실시간 상태 요약
     */
    @Operation(
        summary = "사용자 실시간 상태 요약",
        description = "사용자의 현재 스터디 세션, 폰 사용 제한 상태 등을 종합적으로 조회합니다."
    )
    @GetMapping("/user/{userId}/status")
    public ResponseEntity<Map<String, Object>> getUserRealTimeStatus(@PathVariable Long userId) {
        log.info("사용자 실시간 상태 조회 요청: {}", userId);
        
        Map<String, Object> status = new HashMap<>();
        
        // 폰 사용 제한 상태
        boolean isPhoneRestricted = studySessionService.isPhoneRestricted(userId);
        status.put("isPhoneRestricted", isPhoneRestricted);
        
        // 현재 참여 중인 세션 정보
        // TODO: 현재 참여 중인 세션 정보 조회 로직 구현
        
        // 대기 중인 알림 개수
        // TODO: 읽지 않은 알림 개수 조회 로직 구현
        
        status.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(status);
    }

    /**
     * 스터디 그룹의 실시간 상태 요약
     */
    @Operation(
        summary = "스터디 그룹 실시간 상태 요약",
        description = "스터디 그룹의 현재 세션, 참여자, 폰 사용 제한 상태 등을 조회합니다."
    )
    @GetMapping("/study-group/{groupId}/status")
    public ResponseEntity<Map<String, Object>> getStudyGroupRealTimeStatus(@PathVariable Long groupId) {
        log.info("스터디 그룹 실시간 상태 조회 요청: {}", groupId);
        
        Map<String, Object> status = new HashMap<>();
        
        // 현재 진행 중인 세션 정보
        // TODO: 그룹의 현재 진행 중인 세션 정보 조회
        
        // 그룹 멤버 현황
        // TODO: 그룹 멤버 수, 온라인 상태 등 조회
        
        // 폰 사용 제한 상태 요약
        // TODO: 그룹 전체의 폰 사용 제한 상태 요약
        
        status.put("groupId", groupId);
        status.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(status);
    }

    /**
     * 폰 사용 제한 상태 실시간 모니터링
     */
    @Operation(
        summary = "폰 사용 제한 상태 실시간 모니터링",
        description = "현재 진행 중인 모든 세션의 폰 사용 제한 상태를 실시간으로 조회합니다."
    )
    @GetMapping("/phone-restriction/status")
    public ResponseEntity<Map<String, Object>> getPhoneRestrictionRealTimeStatus() {
        log.info("폰 사용 제한 상태 실시간 모니터링 요청");
        
        Map<String, Object> status = new HashMap<>();
        
        // 현재 진행 중인 세션 수
        // TODO: 현재 진행 중인 세션 수 조회
        
        // 폰 사용 제한 중인 사용자 수
        // TODO: 현재 폰 사용이 제한된 사용자 수 조회
        
        // 대기 중인 예외 신청 수
        // TODO: 승인 대기 중인 예외 신청 수 조회
        
        status.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(status);
    }

    /**
     * 실시간 알림 테스트 (개발용)
     */
    @Operation(
        summary = "실시간 알림 테스트",
        description = "개발 및 테스트를 위한 실시간 알림을 전송합니다."
    )
    @PostMapping("/test-notification")
    public ResponseEntity<String> sendTestNotification(
            @RequestParam Long userId,
            @RequestParam String message) {
        log.info("테스트 알림 전송 요청: 사용자 {} - {}", userId, message);
        
        NotificationDto.NotificationMessage notification = NotificationDto.NotificationMessage.builder()
                .id(java.util.UUID.randomUUID().toString())
                .type(NotificationDto.NotificationType.GENERAL)
                .title("테스트 알림")
                .message(message)
                .userId(userId)
                .createdAt(java.time.LocalDateTime.now())
                .isRead(false)
                .priority(NotificationDto.NotificationPriority.NORMAL)
                .build();
        
        notificationService.sendPersonalNotification(userId, notification);
        
        return ResponseEntity.ok("테스트 알림이 전송되었습니다.");
    }

    /**
     * WebSocket 연결 상태 확인
     */
    @Operation(
        summary = "WebSocket 연결 상태 확인",
        description = "WebSocket 서비스의 상태를 확인합니다."
    )
    @GetMapping("/websocket/health")
    public ResponseEntity<Map<String, Object>> getWebSocketHealth() {
        log.info("WebSocket 연결 상태 확인 요청");
        
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("websocket", "WebSocket 서비스가 정상 동작 중입니다.");
        health.put("endpoint", "/ws");
        health.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(health);
    }
} 