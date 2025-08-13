package com.dmt.app.controller;

import com.dmt.app.dto.PhoneRestrictionExceptionDto;
import com.dmt.app.service.PhoneRestrictionExceptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/phone-exceptions")
@RequiredArgsConstructor
@Slf4j
public class PhoneRestrictionExceptionController {
    
    private final PhoneRestrictionExceptionService phoneRestrictionExceptionService;
    
    @PostMapping
    public ResponseEntity<PhoneRestrictionExceptionDto.Response> createException(@Valid @RequestBody PhoneRestrictionExceptionDto.CreateRequest request) {
        log.info("폰 사용 제한 예외 신청: {} - {}", request.getUserId(), request.getStudySessionId());
        PhoneRestrictionExceptionDto.Response response = phoneRestrictionExceptionService.createException(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{exceptionId}")
    public ResponseEntity<PhoneRestrictionExceptionDto.Response> getExceptionById(@PathVariable Long exceptionId) {
        log.info("폰 사용 제한 예외 조회 요청: {}", exceptionId);
        PhoneRestrictionExceptionDto.Response response = phoneRestrictionExceptionService.getExceptionById(exceptionId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PhoneRestrictionExceptionDto.Response>> getExceptionsByUserId(@PathVariable Long userId) {
        log.info("사용자별 폰 사용 제한 예외 조회 요청: {}", userId);
        List<PhoneRestrictionExceptionDto.Response> response = phoneRestrictionExceptionService.getExceptionsByUserId(userId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/study-session/{sessionId}")
    public ResponseEntity<List<PhoneRestrictionExceptionDto.Response>> getExceptionsByStudySessionId(@PathVariable Long sessionId) {
        log.info("스터디 세션별 폰 사용 제한 예외 조회 요청: {}", sessionId);
        List<PhoneRestrictionExceptionDto.Response> response = phoneRestrictionExceptionService.getExceptionsByStudySessionId(sessionId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/study-group/{groupId}/pending")
    public ResponseEntity<List<PhoneRestrictionExceptionDto.Response>> getPendingExceptionsByGroupId(@PathVariable Long groupId) {
        log.info("스터디 그룹별 대기 중인 예외 조회 요청: {}", groupId);
        List<PhoneRestrictionExceptionDto.Response> response = phoneRestrictionExceptionService.getPendingExceptionsByGroupId(groupId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/leader/{leaderId}/pending")
    public ResponseEntity<List<PhoneRestrictionExceptionDto.Response>> getPendingExceptionsByLeaderId(@PathVariable Long leaderId) {
        log.info("리더별 대기 중인 예외 조회 요청: {}", leaderId);
        List<PhoneRestrictionExceptionDto.Response> response = phoneRestrictionExceptionService.getPendingExceptionsByLeaderId(leaderId);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{exceptionId}/approve")
    public ResponseEntity<PhoneRestrictionExceptionDto.Response> approveException(@PathVariable Long exceptionId,
                                                                               @RequestParam Long leaderId,
                                                                               @Valid @RequestBody PhoneRestrictionExceptionDto.ApprovalRequest request) {
        log.info("폰 사용 제한 예외 승인/거절 요청: {} -> {} (리더: {})", exceptionId, request.getStatus(), leaderId);
        PhoneRestrictionExceptionDto.Response response = phoneRestrictionExceptionService.approveException(exceptionId, leaderId, request);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{exceptionId}")
    public ResponseEntity<PhoneRestrictionExceptionDto.Response> updateException(@PathVariable Long exceptionId,
                                                                              @RequestParam Long userId,
                                                                              @Valid @RequestBody PhoneRestrictionExceptionDto.UpdateRequest request) {
        log.info("폰 사용 제한 예외 수정 요청: {} (사용자: {})", exceptionId, userId);
        PhoneRestrictionExceptionDto.Response response = phoneRestrictionExceptionService.updateException(exceptionId, userId, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{exceptionId}")
    public ResponseEntity<Void> deleteException(@PathVariable Long exceptionId, @RequestParam Long userId) {
        log.info("폰 사용 제한 예외 삭제 요청: {} (사용자: {})", exceptionId, userId);
        phoneRestrictionExceptionService.deleteException(exceptionId, userId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<PhoneRestrictionExceptionDto.Response>> getActiveExceptions() {
        log.info("현재 활성화된 폰 사용 제한 예외 조회 요청");
        List<PhoneRestrictionExceptionDto.Response> response = phoneRestrictionExceptionService.getActiveExceptions();
        return ResponseEntity.ok(response);
    }
    
    // 폰 사용 제한 상태 확인 엔드포인트들
    
    @GetMapping("/user/{userId}/restriction-status")
    public ResponseEntity<Boolean> isUserCurrentlyPhoneRestricted(@PathVariable Long userId) {
        log.info("사용자 현재 폰 사용 제한 상태 확인 요청: {}", userId);
        boolean isRestricted = phoneRestrictionExceptionService.isUserCurrentlyPhoneRestricted(userId);
        return ResponseEntity.ok(isRestricted);
    }
    
    @GetMapping("/study-session/{sessionId}/user/{userId}/restriction-status")
    public ResponseEntity<Boolean> isUserPhoneRestrictedInSession(@PathVariable Long sessionId, @PathVariable Long userId) {
        log.info("사용자 세션별 폰 사용 제한 상태 확인 요청: {} - {}", sessionId, userId);
        boolean isRestricted = phoneRestrictionExceptionService.isUserPhoneRestrictedInSession(userId, sessionId);
        return ResponseEntity.ok(isRestricted);
    }
    
    @GetMapping("/study-session/{sessionId}/restriction-summary")
    public ResponseEntity<PhoneRestrictionExceptionDto.SessionRestrictionSummary> getSessionRestrictionSummary(@PathVariable Long sessionId) {
        log.info("세션별 폰 사용 제한 상태 요약 요청: {}", sessionId);
        PhoneRestrictionExceptionDto.SessionRestrictionSummary response = phoneRestrictionExceptionService.getSessionRestrictionSummary(sessionId);
        return ResponseEntity.ok(response);
    }
} 