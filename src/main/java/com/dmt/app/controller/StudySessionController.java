package com.dmt.app.controller;

import com.dmt.app.dto.ApiResponse;
import com.dmt.app.dto.StudySessionDto;
import com.dmt.app.service.StudySessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/study-sessions")
@RequiredArgsConstructor
@Slf4j
public class StudySessionController {
    
    private final StudySessionService studySessionService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<StudySessionDto.Response>> createStudySession(@RequestParam Long userId,
                                                                     @Valid @RequestBody StudySessionDto.CreateRequest request) {
        log.info("스터디 세션 생성 요청: {} (사용자: {})", request.getSessionName(), userId);
        StudySessionDto.Response response = studySessionService.createStudySession(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "스터디 세션이 성공적으로 생성되었습니다."));
    }
    
    @GetMapping("/{sessionId}")
    public ResponseEntity<ApiResponse<StudySessionDto.Response>> getStudySessionById(@PathVariable Long sessionId) {
        log.info("스터디 세션 조회 요청: {}", sessionId);
        StudySessionDto.Response response = studySessionService.getStudySessionById(sessionId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/study-group/{groupId}")
    public ResponseEntity<ApiResponse<List<StudySessionDto.Response>>> getStudySessionsByGroupId(@PathVariable Long groupId) {
        log.info("스터디 그룹별 세션 조회 요청: {}", groupId);
        List<StudySessionDto.Response> response = studySessionService.getStudySessionsByGroupId(groupId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/study-group/{groupId}/upcoming")
    public ResponseEntity<ApiResponse<List<StudySessionDto.Response>>> getUpcomingSessionsByGroupId(@PathVariable Long groupId) {
        log.info("스터디 그룹별 예정 세션 조회 요청: {}", groupId);
        List<StudySessionDto.Response> response = studySessionService.getUpcomingSessionsByGroupId(groupId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/current")
    public ResponseEntity<ApiResponse<List<StudySessionDto.Response>>> getCurrentSessions() {
        log.info("현재 진행 중인 세션 조회 요청");
        List<StudySessionDto.Response> response = studySessionService.getCurrentSessions();
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PutMapping("/{sessionId}")
    public ResponseEntity<ApiResponse<StudySessionDto.Response>> updateStudySession(@PathVariable Long sessionId,
                                                                     @RequestParam Long userId,
                                                                     @Valid @RequestBody StudySessionDto.UpdateRequest request) {
        log.info("스터디 세션 수정 요청: {} (사용자: {})", sessionId, userId);
        StudySessionDto.Response response = studySessionService.updateStudySession(sessionId, userId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "스터디 세션이 성공적으로 수정되었습니다."));
    }
    
    @PutMapping("/{sessionId}/status")
    public ResponseEntity<ApiResponse<StudySessionDto.Response>> updateSessionStatus(@PathVariable Long sessionId,
                                                                      @RequestParam Long userId,
                                                                      @Valid @RequestBody StudySessionDto.StatusUpdateRequest request) {
        log.info("스터디 세션 상태 변경 요청: {} -> {} (사용자: {})", sessionId, request.getStatus(), userId);
        StudySessionDto.Response response = studySessionService.updateSessionStatus(sessionId, userId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "스터디 세션 상태가 성공적으로 변경되었습니다."));
    }
    
    @DeleteMapping("/{sessionId}")
    public ResponseEntity<ApiResponse<Void>> deleteStudySession(@PathVariable Long sessionId, @RequestParam Long userId) {
        log.info("스터디 세션 삭제 요청: {} (사용자: {})", sessionId, userId);
        studySessionService.deleteStudySession(sessionId, userId);
        return ResponseEntity.ok(ApiResponse.success("스터디 세션이 성공적으로 삭제되었습니다."));
    }
    
    // 폰 사용 제한 관련 엔드포인트
    
    @GetMapping("/{sessionId}/phone-restriction-status")
    public ResponseEntity<ApiResponse<StudySessionDto.PhoneRestrictionStatus>> getPhoneRestrictionStatus(@PathVariable Long sessionId) {
        log.info("폰 사용 제한 상태 확인 요청: {}", sessionId);
        StudySessionDto.PhoneRestrictionStatus response = studySessionService.getPhoneRestrictionStatus(sessionId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/user/{userId}/phone-restriction")
    public ResponseEntity<ApiResponse<Boolean>> isUserPhoneRestricted(@PathVariable Long userId) {
        log.info("사용자 폰 사용 제한 상태 확인 요청: {}", userId);
        boolean isRestricted = studySessionService.isPhoneRestricted(userId);
        return ResponseEntity.ok(ApiResponse.success(isRestricted));
    }
} 