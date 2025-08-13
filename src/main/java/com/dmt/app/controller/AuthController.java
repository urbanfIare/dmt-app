package com.dmt.app.controller;

import com.dmt.app.dto.AuthDto;
import com.dmt.app.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "회원가입 성공",
            content = @Content(schema = @Schema(implementation = AuthDto.RegisterResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "409", description = "이미 존재하는 이메일 또는 닉네임")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthDto.RegisterResponse> register(
            @Valid @RequestBody AuthDto.RegisterRequest request) {
        log.info("회원가입 요청: {}", request.getEmail());
        AuthDto.RegisterResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "사용자 로그인",
        description = "이메일과 비밀번호로 로그인하여 JWT 토큰을 발급받습니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "로그인 성공",
            content = @Content(schema = @Schema(implementation = AuthDto.LoginResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthDto.LoginResponse> login(
            @Valid @RequestBody AuthDto.LoginRequest request) {
        log.info("로그인 요청: {}", request.getEmail());
        AuthDto.LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "서비스 상태 확인",
        description = "인증 서비스의 상태를 확인합니다."
    )
    @ApiResponse(responseCode = "200", description = "서비스 정상 동작")
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Auth Service is running");
    }
} 