package com.dmt.app.controller;

import com.dmt.app.dto.ApiResponse;
import com.dmt.app.dto.UserDto;
import com.dmt.app.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDto.Response>> registerUser(@Valid @RequestBody UserDto.CreateRequest request) {
        log.info("사용자 등록 요청: {}", request.getEmail());
        UserDto.Response response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "사용자가 성공적으로 등록되었습니다."));
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserDto.Response>> getUserById(@PathVariable Long userId) {
        log.info("사용자 조회 요청: {}", userId);
        UserDto.Response response = userService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<UserDto.Response>> getUserByEmail(@PathVariable String email) {
        log.info("이메일로 사용자 조회 요청: {}", email);
        UserDto.Response response = userService.getUserByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserDto.Response>> updateUser(@PathVariable Long userId, 
                                                     @Valid @RequestBody UserDto.UpdateRequest request) {
        log.info("사용자 정보 수정 요청: {}", userId);
        UserDto.Response response = userService.updateUser(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "사용자 정보가 성공적으로 수정되었습니다."));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDto.Response>>> getAllUsers() {
        log.info("전체 사용자 조회 요청");
        List<UserDto.Response> response = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/study-group/{groupId}")
    public ResponseEntity<ApiResponse<List<UserDto.Response>>> getUsersByStudyGroupId(@PathVariable Long groupId) {
        log.info("스터디 그룹 사용자 조회 요청: {}", groupId);
        List<UserDto.Response> response = userService.getUsersByStudyGroupId(groupId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/study-group/{groupId}/leader")
    public ResponseEntity<ApiResponse<UserDto.Response>> getLeaderByStudyGroupId(@PathVariable Long groupId) {
        log.info("스터디 그룹 리더 조회 요청: {}", groupId);
        UserDto.Response response = userService.getLeaderByStudyGroupId(groupId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long userId) {
        log.info("사용자 삭제 요청: {}", userId);
        userService.deleteUser(userId);
        return ResponseEntity.ok(ApiResponse.success("사용자가 성공적으로 삭제되었습니다."));
    }
} 