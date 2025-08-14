package com.dmt.app.config;

import com.dmt.app.dto.ApiResponse;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI 설정
 * 표준화된 API 응답 형식을 반영한 문서화 설정
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", createAPIKeyScheme())
                        .addResponses("StandardSuccess", createSuccessResponse())
                        .addResponses("StandardError", createErrorResponse())
                        .addResponses("StandardValidationError", createValidationErrorResponse())
                        .addResponses("StandardUnauthorized", createUnauthorizedResponse())
                        .addResponses("StandardForbidden", createForbiddenResponse())
                        .addResponses("StandardNotFound", createNotFoundResponse())
                        .addResponses("StandardConflict", createConflictResponse())
                        .addResponses("StandardInternalServerError", createInternalServerErrorResponse()));
    }

    private Info apiInfo() {
        return new Info()
                .title("DMT (Don't Touch My Time) API")
                .description("""
                    스터디 그룹을 만들어서 정해진 시간에 스터디를 진행하며, 
                    스터디 시간 동안 폰을 못보게 하는 디지털 디톡스 서비스의 백엔드 API입니다.
                    
                    ## 📋 API 응답 형식
                    모든 API는 일관된 응답 형식을 제공합니다:
                    
                    ### 성공 응답
                    ```json
                    {
                      "success": true,
                      "data": {...},
                      "message": "요청이 성공적으로 처리되었습니다.",
                      "timestamp": "2024-01-01 12:00:00"
                    }
                    ```
                    
                    ### 실패 응답
                    ```json
                    {
                      "success": false,
                      "errorCode": "2001",
                      "message": "이미 존재하는 사용자입니다.",
                      "errorDetail": "이메일: test@example.com",
                      "timestamp": "2024-01-01 12:00:00"
                    }
                    ```
                    
                    ## 🔢 에러 코드 체계
                    - **1000번대**: 공통 에러 (잘못된 입력값, 인증 실패 등)
                    - **2000번대**: 사용자 관련 에러 (사용자 없음, 중복 등)
                    - **3000번대**: 스터디 그룹 관련 에러 (그룹 없음, 권한 없음 등)
                    - **4000번대**: 스터디 세션 관련 에러 (세션 없음, 시간 충돌 등)
                    - **5000번대**: 출석 관련 에러
                    - **6000번대**: 폰 사용 제한 관련 에러
                    - **7000번대**: 알림 관련 에러
                    - **8000번대**: 파일 관련 에러
                    """)
                .version("2.0.0")
                .contact(new Contact()
                        .name("DMT 개발팀")
                        .email("dev@dmt.com")
                        .url("https://github.com/dmt-backend"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"));
    }

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer")
                .description("JWT 토큰을 입력하세요. Bearer 접두사는 자동으로 추가됩니다.");
    }

    private ApiResponse createSuccessResponse() {
        return new ApiResponse()
                .description("요청이 성공적으로 처리되었습니다.")
                .content(new Content()
                        .addMediaType("application/json", new MediaType()
                                .schema(new Schema<ApiResponse>()
                                        .example("""
                                            {
                                              "success": true,
                                              "data": {...},
                                              "message": "요청이 성공적으로 처리되었습니다.",
                                              "timestamp": "2024-01-01 12:00:00"
                                            }
                                            """))));
    }

    private ApiResponse createErrorResponse() {
        return new ApiResponse()
                .description("요청 처리 중 오류가 발생했습니다.")
                .content(new Content()
                        .addMediaType("application/json", new MediaType()
                                .schema(new Schema<ApiResponse>()
                                        .example("""
                                            {
                                              "success": false,
                                              "errorCode": "2001",
                                              "message": "이미 존재하는 사용자입니다.",
                                              "errorDetail": "이메일: test@example.com",
                                              "timestamp": "2024-01-01 12:00:00"
                                            }
                                            """))));
    }

    private ApiResponse createValidationErrorResponse() {
        return new ApiResponse()
                .description("입력값 검증에 실패했습니다.")
                .content(new Content()
                        .addMediaType("application/json", new MediaType()
                                .schema(new Schema<ApiResponse>()
                                        .example("""
                                            {
                                              "success": false,
                                              "errorCode": "1000",
                                              "message": "입력값 검증에 실패했습니다.",
                                              "errorDetail": "이메일 형식이 올바르지 않습니다, 닉네임은 2자 이상이어야 합니다",
                                              "timestamp": "2024-01-01 12:00:00"
                                            }
                                            """))));
    }

    private ApiResponse createUnauthorizedResponse() {
        return new ApiResponse()
                .description("인증이 필요합니다.")
                .content(new Content()
                        .addMediaType("application/json", new MediaType()
                                .schema(new Schema<ApiResponse>()
                                        .example("""
                                            {
                                              "success": false,
                                              "errorCode": "1006",
                                              "message": "인증이 필요합니다.",
                                              "timestamp": "2024-01-01 12:00:00"
                                            }
                                            """))));
    }

    private ApiResponse createForbiddenResponse() {
        return new ApiResponse()
                .description("접근이 거부되었습니다.")
                .content(new Content()
                        .addMediaType("application/json", new MediaType()
                                .schema(new Schema<ApiResponse>()
                                        .example("""
                                            {
                                              "success": false,
                                              "errorCode": "1005",
                                              "message": "접근이 거부되었습니다.",
                                              "timestamp": "2024-01-01 12:00:00"
                                            }
                                            """))));
    }

    private ApiResponse createNotFoundResponse() {
        return new ApiResponse()
                .description("요청한 리소스를 찾을 수 없습니다.")
                .content(new Content()
                        .addMediaType("application/json", new MediaType()
                                .schema(new Schema<ApiResponse>()
                                        .example("""
                                            {
                                              "success": false,
                                              "errorCode": "1002",
                                              "message": "요청한 리소스를 찾을 수 없습니다.",
                                              "timestamp": "2024-01-01 12:00:00"
                                            }
                                            """))));
    }

    private ApiResponse createConflictResponse() {
        return new ApiResponse()
                .description("리소스 충돌이 발생했습니다.")
                .content(new Content()
                        .addMediaType("application/json", new MediaType()
                                .schema(new Schema<ApiResponse>()
                                        .example("""
                                            {
                                              "success": false,
                                              "errorCode": "2002",
                                              "message": "이미 사용 중인 이메일입니다.",
                                              "errorDetail": "이메일: test@example.com",
                                              "timestamp": "2024-01-01 12:00:00"
                                            }
                                            """))));
    }

    private ApiResponse createInternalServerErrorResponse() {
        return new ApiResponse()
                .description("서버 내부 오류가 발생했습니다.")
                .content(new Content()
                        .addMediaType("application/json", new MediaType()
                                .schema(new Schema<ApiResponse>()
                                        .example("""
                                            {
                                              "success": false,
                                              "errorCode": "1003",
                                              "message": "서버 내부 오류가 발생했습니다.",
                                              "timestamp": "2024-01-01 12:00:00"
                                            }
                                            """))));
    }
} 