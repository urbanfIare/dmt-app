package com.dmt.app.controller;

import com.dmt.app.dto.UserDto;
import com.dmt.app.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "사용자 관리", description = "사용자 CRUD 및 조회 API")
public class UserController {
    
    private final UserService userService;
    
    @Operation(
        summary = "사용자 등록",
        description = "새로운 사용자를 등록합니다. 이메일과 닉네임은 중복될 수 없습니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "사용자 생성 성공",
            content = @Content(schema = @Schema(implementation = UserDto.Response.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "409", description = "이미 존재하는 이메일 또는 닉네임")
    })
    @PostMapping("/register")
    public ResponseEntity<UserDto.Response> registerUser(@Valid @RequestBody UserDto.CreateRequest request) {
        log.info("사용자 등록 요청: {}", request.getEmail());
        UserDto.Response response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto.Response> getUserById(@PathVariable Long userId) {
        log.info("사용자 조회 요청: {}", userId);
        UserDto.Response response = userService.getUserById(userId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDto.Response> getUserByEmail(@PathVariable String email) {
        log.info("이메일로 사용자 조회 요청: {}", email);
        UserDto.Response response = userService.getUserByEmail(email);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{userId}")
    public ResponseEntity<UserDto.Response> updateUser(@PathVariable Long userId, 
                                                     @Valid @RequestBody UserDto.UpdateRequest request) {
        log.info("사용자 정보 수정 요청: {}", userId);
        UserDto.Response response = userService.updateUser(userId, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<UserDto.Response>> getAllUsers() {
        log.info("전체 사용자 조회 요청");
        List<UserDto.Response> response = userService.getAllUsers();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/study-group/{groupId}")
    public ResponseEntity<List<UserDto.Response>> getUsersByStudyGroupId(@PathVariable Long groupId) {
        log.info("스터디 그룹 사용자 조회 요청: {}", groupId);
        List<UserDto.Response> response = userService.getUsersByStudyGroupId(groupId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/study-group/{groupId}/leader")
    public ResponseEntity<UserDto.Response> getLeaderByStudyGroupId(@PathVariable Long groupId) {
        log.info("스터디 그룹 리더 조회 요청: {}", groupId);
        UserDto.Response response = userService.getLeaderByStudyGroupId(groupId);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        log.info("사용자 삭제 요청: {}", userId);
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
} 