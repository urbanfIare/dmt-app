package com.dmt.app.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("DMT API - Don't Touch My Time")
                        .description("""
                                ## 📱 DMT (Don't Touch My Time) API
                                
                                ### 🎯 핵심 기능
                                - **스터디 그룹 관리**: 그룹 생성, 참여, 관리
                                - **스터디 세션 관리**: 세션 생성, 진행, 모니터링
                                - **폰 사용 제한**: 스터디 시간 중 자동 폰 사용 제한
                                - **예외 관리**: 긴급 상황 시 폰 사용 예외 신청/승인
                                - **출석 관리**: 세션별 출석 기록 및 통계
                                
                                ### 🔐 인증
                                - JWT 기반 인증 시스템
                                - Spring Security 적용
                                - Role 기반 접근 제어
                                
                                ### 📊 주요 엔드포인트
                                - **인증**: `/api/auth/**` - 로그인, 회원가입
                                - **사용자**: `/api/users/**` - 사용자 관리
                                - **스터디 그룹**: `/api/study-groups/**` - 그룹 관리
                                - **스터디 세션**: `/api/study-sessions/**` - 세션 관리
                                - **출석**: `/api/attendances/**` - 출석 관리
                                - **폰 사용 제한**: `/api/phone-exceptions/**` - 예외 관리
                                
                                ### 🚀 사용 방법
                                1. **회원가입**: `/api/auth/register`로 계정 생성
                                2. **로그인**: `/api/auth/login`으로 JWT 토큰 획득
                                3. **API 호출**: Authorization 헤더에 `Bearer {JWT}` 포함
                                
                                ### 🔧 개발 환경
                                - **Base URL**: `http://localhost:8080`
                                - **데이터베이스**: H2 (In-Memory)
                                - **H2 콘솔**: `http://localhost:8080/h2-console`
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("DMT Development Team")
                                .email("dev@dmt.com")
                                .url("https://github.com/dmt-app"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("로컬 개발 환경"),
                        new Server().url("https://api.dmt.com").description("운영 환경")
                ))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT 토큰을 입력하세요. 'Bearer ' 접두사는 자동으로 추가됩니다.")));
    }
} 