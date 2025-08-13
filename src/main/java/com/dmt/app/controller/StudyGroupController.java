package com.dmt.app.controller;

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
    public ResponseEntity<StudyGroupDto.Response> createStudyGroup(@RequestParam Long userId,
                                                                 @Valid @RequestBody StudyGroupDto.CreateRequest request) {
        log.info("스터디 그룹 생성 요청: {} (사용자: {})", request.getName(), userId);
        StudyGroupDto.Response response = studyGroupService.createStudyGroup(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{groupId}")
    public ResponseEntity<StudyGroupDto.Response> getStudyGroupById(@PathVariable Long groupId) {
        log.info("스터디 그룹 조회 요청: {}", groupId);
        StudyGroupDto.Response response = studyGroupService.getStudyGroupById(groupId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<StudyGroupDto.Response>> getAllStudyGroups() {
        log.info("전체 스터디 그룹 조회 요청");
        List<StudyGroupDto.Response> response = studyGroupService.getAllStudyGroups();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<StudyGroupDto.Response>> getActiveStudyGroups() {
        log.info("활성 스터디 그룹 조회 요청");
        List<StudyGroupDto.Response> response = studyGroupService.getActiveStudyGroups();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<StudyGroupDto.Response>> getStudyGroupsByUserId(@PathVariable Long userId) {
        log.info("사용자별 스터디 그룹 조회 요청: {}", userId);
        List<StudyGroupDto.Response> response = studyGroupService.getStudyGroupsByUserId(userId);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{groupId}")
    public ResponseEntity<StudyGroupDto.Response> updateStudyGroup(@PathVariable Long groupId,
                                                                 @RequestParam Long userId,
                                                                 @Valid @RequestBody StudyGroupDto.UpdateRequest request) {
        log.info("스터디 그룹 수정 요청: {} (사용자: {})", groupId, userId);
        StudyGroupDto.Response response = studyGroupService.updateStudyGroup(groupId, userId, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> deleteStudyGroup(@PathVariable Long groupId, @RequestParam Long userId) {
        log.info("스터디 그룹 삭제 요청: {} (사용자: {})", groupId, userId);
        studyGroupService.deleteStudyGroup(groupId, userId);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{groupId}/join")
    public ResponseEntity<Void> joinStudyGroup(@PathVariable Long groupId, @RequestParam Long userId) {
        log.info("스터디 그룹 참여 요청: {} (사용자: {})", groupId, userId);
        studyGroupService.joinStudyGroup(groupId, userId);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{groupId}/leave")
    public ResponseEntity<Void> leaveStudyGroup(@PathVariable Long groupId, @RequestParam Long userId) {
        log.info("스터디 그룹 탈퇴 요청: {} (사용자: {})", groupId, userId);
        studyGroupService.leaveStudyGroup(groupId, userId);
        return ResponseEntity.ok().build();
    }
} 