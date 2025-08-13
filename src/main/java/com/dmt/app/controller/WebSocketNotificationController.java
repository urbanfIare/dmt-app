package com.dmt.app.controller;

import com.dmt.app.dto.NotificationDto;
import com.dmt.app.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
@Tag(name = "WebSocket 알림", description = "실시간 WebSocket 알림 API")
public class WebSocketNotificationController {

    private final NotificationService notificationService;

    /**
     * 클라이언트로부터 받은 메시지를 모든 구독자에게 브로드캐스트
     */
    @MessageMapping("/broadcast")
    @SendTo("/topic/public")
    public NotificationDto.NotificationMessage broadcastMessage(@Payload NotificationDto.NotificationMessage message) {
        log.info("브로드캐스트 메시지 수신: {}", message.getTitle());
        return message;
    }

    /**
     * 사용자별 개인 메시지 처리
     */
    @MessageMapping("/private-message")
    public void handlePrivateMessage(@Payload NotificationDto.NotificationMessage message) {
        log.info("개인 메시지 수신: 사용자 {} - {}", message.getUserId(), message.getTitle());
        // 개인 메시지 처리 로직
    }

    /**
     * 그룹별 메시지 처리
     */
    @MessageMapping("/group-message")
    public void handleGroupMessage(@Payload NotificationDto.NotificationMessage message) {
        log.info("그룹 메시지 수신: 그룹 {} - {}", message.getStudyGroupId(), message.getTitle());
        // 그룹 메시지 처리 로직
    }
} 