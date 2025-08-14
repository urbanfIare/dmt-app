package com.dmt.app.controller;

import com.dmt.app.dto.ApiResponse;
import com.dmt.app.dto.AuthDto;
import com.dmt.app.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "인증 관리", description = "사용자 로그인, 회원가입 API")
public class AuthController {

    private final AuthService authService;

    @Operation(
        summary = "사용자 회원가입",
        description = "새로운 사용자를 등록합니다. 이메일과 닉네임은 중복될 수 없습니다."
    )
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthDto.RegisterResponse>> register(
            @Valid @RequestBody AuthDto.RegisterRequest request) {
        log.info("회원가입 요청: {}", request.getEmail());
        AuthDto.RegisterResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success(response, "회원가입이 성공적으로 완료되었습니다."));
    }

    @Operation(
        summary = "사용자 로그인",
        description = "이메일과 비밀번호로 로그인하여 JWT 토큰을 발급받습니다."
    )
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthDto.LoginResponse>> login(
            @Valid @RequestBody AuthDto.LoginRequest request) {
        log.info("로그인 요청: {}", request.getEmail());
        AuthDto.LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "로그인이 성공했습니다."));
    }

    @Operation(
        summary = "서비스 상태 확인",
        description = "인증 서비스의 상태를 확인합니다."
    )
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("Auth Service is running"));
    }
} 