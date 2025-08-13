package com.dmt.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "로그인 요청")
    public static class LoginRequest {
        @Schema(description = "사용자 이메일", example = "user@example.com", required = true)
        private String email;
        @Schema(description = "사용자 비밀번호", example = "password123", required = true)
        private String password;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "로그인 응답")
    public static class LoginResponse {
        @Schema(description = "JWT 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        private String token;
        @Schema(description = "사용자 이메일", example = "user@example.com")
        private String email;
        @Schema(description = "사용자 닉네임", example = "테스트유저")
        private String nickname;
        @Schema(description = "사용자 역할", example = "USER")
        private String role;
        @Schema(description = "응답 메시지", example = "로그인이 성공했습니다.")
        private String message;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "회원가입 요청")
    public static class RegisterRequest {
        @Schema(description = "사용자 이메일", example = "user@example.com", required = true)
        private String email;
        @Schema(description = "사용자 닉네임", example = "테스트유저", required = true)
        private String nickname;
        @Schema(description = "사용자 비밀번호", example = "password123", required = true)
        private String password;
        @Schema(description = "전화번호", example = "010-1234-5678")
        private String phoneNumber;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "회원가입 응답")
    public static class RegisterResponse {
        @Schema(description = "응답 메시지", example = "회원가입이 완료되었습니다.")
        private String message;
        @Schema(description = "사용자 이메일", example = "user@example.com")
        private String email;
        @Schema(description = "사용자 닉네임", example = "테스트유저")
        private String nickname;
    }
} 