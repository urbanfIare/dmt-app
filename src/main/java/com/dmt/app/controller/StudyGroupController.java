package com.dmt.app.controller;

import com.dmt.app.dto.ApiResponse;
import com.dmt.app.dto.StudyGroupDto;
import com.dmt.app.service.StudyGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/study-groups")
@RequiredArgsConstructor
@Slf4j
public class StudyGroupController {
    
    private final StudyGroupService studyGroupService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<StudyGroupDto.Response>> createStudyGroup(@RequestParam Long userId,
                                                                 @Valid @RequestBody StudyGroupDto.CreateRequest request) {
        log.info("스터디 그룹 생성 요청: {} (사용자: {})", request.getName(), userId);
        StudyGroupDto.Response response = studyGroupService.createStudyGroup(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "스터디 그룹이 성공적으로 생성되었습니다."));
    }
    
    @GetMapping("/{groupId}")
    public ResponseEntity<ApiResponse<StudyGroupDto.Response>> getStudyGroupById(@PathVariable Long groupId) {
        log.info("스터디 그룹 조회 요청: {}", groupId);
        StudyGroupDto.Response response = studyGroupService.getStudyGroupById(groupId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<StudyGroupDto.Response>>> getAllStudyGroups() {
        log.info("전체 스터디 그룹 조회 요청");
        List<StudyGroupDto.Response> response = studyGroupService.getAllStudyGroups();
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<StudyGroupDto.Response>>> getActiveStudyGroups() {
        log.info("활성 스터디 그룹 조회 요청");
        List<StudyGroupDto.Response> response = studyGroupService.getActiveStudyGroups();
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<StudyGroupDto.Response>>> getStudyGroupsByUserId(@PathVariable Long userId) {
        log.info("사용자별 스터디 그룹 조회 요청: {}", userId);
        List<StudyGroupDto.Response> response = studyGroupService.getStudyGroupsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PutMapping("/{groupId}")
    public ResponseEntity<ApiResponse<StudyGroupDto.Response>> updateStudyGroup(@PathVariable Long groupId,
                                                                 @RequestParam Long userId,
                                                                 @Valid @RequestBody StudyGroupDto.UpdateRequest request) {
        log.info("스터디 그룹 수정 요청: {} (사용자: {})", groupId, userId);
        StudyGroupDto.Response response = studyGroupService.updateStudyGroup(groupId, userId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "스터디 그룹이 성공적으로 수정되었습니다."));
    }
    
    @DeleteMapping("/{groupId}")
    public ResponseEntity<ApiResponse<Void>> deleteStudyGroup(@PathVariable Long groupId, @RequestParam Long userId) {
        log.info("스터디 그룹 삭제 요청: {} (사용자: {})", groupId, userId);
        studyGroupService.deleteStudyGroup(groupId, userId);
        return ResponseEntity.ok(ApiResponse.success("스터디 그룹이 성공적으로 삭제되었습니다."));
    }
    
    @PostMapping("/{groupId}/join")
    public ResponseEntity<ApiResponse<Void>> joinStudyGroup(@PathVariable Long groupId, @RequestParam Long userId) {
        log.info("스터디 그룹 참여 요청: {} (사용자: {})", groupId, userId);
        studyGroupService.joinStudyGroup(groupId, userId);
        return ResponseEntity.ok(ApiResponse.success("스터디 그룹에 성공적으로 참여했습니다."));
    }
    
    @PostMapping("/{groupId}/leave")
    public ResponseEntity<ApiResponse<Void>> leaveStudyGroup(@PathVariable Long groupId, @RequestParam Long userId) {
        log.info("스터디 그룹 탈퇴 요청: {} (사용자: {})", groupId, userId);
        studyGroupService.leaveStudyGroup(groupId, userId);
        return ResponseEntity.ok(ApiResponse.success("스터디 그룹에서 성공적으로 탈퇴했습니다."));
    }
} 