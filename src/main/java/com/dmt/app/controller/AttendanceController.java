package com.dmt.app.controller;

import com.dmt.app.dto.AttendanceDto;
import com.dmt.app.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/attendances")
@RequiredArgsConstructor
@Slf4j
public class AttendanceController {
    
    private final AttendanceService attendanceService;
    
    @PostMapping
    public ResponseEntity<AttendanceDto.Response> createAttendance(@Valid @RequestBody AttendanceDto.CreateRequest request) {
        log.info("출석 기록 생성 요청: {} - {}", request.getUserId(), request.getStudySessionId());
        AttendanceDto.Response response = attendanceService.createAttendance(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{attendanceId}")
    public ResponseEntity<AttendanceDto.Response> getAttendanceById(@PathVariable Long attendanceId) {
        log.info("출석 기록 조회 요청: {}", attendanceId);
        AttendanceDto.Response response = attendanceService.getAttendanceById(attendanceId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AttendanceDto.Response>> getAttendancesByUserId(@PathVariable Long userId) {
        log.info("사용자별 출석 기록 조회 요청: {}", userId);
        List<AttendanceDto.Response> response = attendanceService.getAttendancesByUserId(userId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/study-session/{sessionId}")
    public ResponseEntity<List<AttendanceDto.Response>> getAttendancesByStudySessionId(@PathVariable Long sessionId) {
        log.info("스터디 세션별 출석 기록 조회 요청: {}", sessionId);
        List<AttendanceDto.Response> response = attendanceService.getAttendancesByStudySessionId(sessionId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/study-group/{groupId}")
    public ResponseEntity<List<AttendanceDto.Response>> getAttendancesByStudyGroupId(@PathVariable Long groupId) {
        log.info("스터디 그룹별 출석 기록 조회 요청: {}", groupId);
        List<AttendanceDto.Response> response = attendanceService.getAttendancesByStudyGroupId(groupId);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{attendanceId}")
    public ResponseEntity<AttendanceDto.Response> updateAttendance(@PathVariable Long attendanceId,
                                                                 @RequestParam Long userId,
                                                                 @Valid @RequestBody AttendanceDto.UpdateRequest request) {
        log.info("출석 기록 수정 요청: {} (사용자: {})", attendanceId, userId);
        AttendanceDto.Response response = attendanceService.updateAttendance(attendanceId, userId, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{attendanceId}")
    public ResponseEntity<Void> deleteAttendance(@PathVariable Long attendanceId, @RequestParam Long userId) {
        log.info("출석 기록 삭제 요청: {} (사용자: {})", attendanceId, userId);
        attendanceService.deleteAttendance(attendanceId, userId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/summary/user/{userId}/group/{groupId}")
    public ResponseEntity<AttendanceDto.AttendanceSummary> getAttendanceSummary(@PathVariable Long userId,
                                                                              @PathVariable Long groupId) {
        log.info("출석률 요약 조회 요청: {} (그룹: {})", userId, groupId);
        AttendanceDto.AttendanceSummary response = attendanceService.getAttendanceSummary(userId, groupId);
        return ResponseEntity.ok(response);
    }
} 