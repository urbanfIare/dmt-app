# 📱 DMT (Don't Touch My Time) - Backend API

## 🎯 **프로젝트 소개**

**DMT**는 스터디 그룹을 만들어서 정해진 시간에 스터디를 진행하며, 스터디 시간 동안 폰을 못보게 하는 **디지털 디톡스 서비스**입니다.

### **핵심 기능**
- 🏠 **스터디 그룹 관리**: 그룹 생성, 멤버 관리, 권한 제어
- 📱 **폰 사용 제한**: 스터디 시간 중 자동 폰 사용 제한
- 🔐 **예외 관리**: 긴급 상황 시 폰 사용 예외 신청/승인
- ✅ **출석 관리**: 세션별 출석 기록 및 통계
- 🌐 **실시간 알림**: WebSocket 기반 실시간 상태 업데이트

---

## 🏗️ **기술 스택**

- **Backend**: Spring Boot 3.5.4
- **Language**: Java 21
- **Build Tool**: Maven
- **Database**: H2 (개발용), 추후 MySQL/PostgreSQL
- **ORM**: Spring Data JPA + Hibernate
- **Security**: Spring Security + JWT
- **Real-time**: WebSocket + STOMP
- **Documentation**: Swagger/OpenAPI (SpringDoc)
- **Validation**: Spring Boot Validation

---

## 🚀 **빠른 시작**

### **필수 요구사항**
- Java 21 이상
- Maven 3.6 이상

### **1. 프로젝트 클론**
```bash
git clone https://github.com/your-username/dmt-backend.git
cd dmt-backend
```

### **2. 애플리케이션 실행**
```bash
# Maven Wrapper 사용
./mvnw spring-boot:run

# 또는 Maven 직접 사용
mvn spring-boot:run
```

### **3. 접속 확인**
- **애플리케이션**: `http://localhost:8080`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **H2 콘솔**: `http://localhost:8080/h2-console`
- **WebSocket 테스트**: `http://localhost:8080/websocket-test.html`

---

## 📚 **API 문서**

### **Swagger UI**
- **URL**: `http://localhost:8080/swagger-ui.html`
- **기능**: API 테스트, 문서 확인, JWT 인증 설정, 표준 응답 형식 예시

### **API 응답 형식**
모든 API는 일관된 응답 형식을 제공합니다:

**성공 응답:**
```json
{
  "success": true,
  "data": {...},
  "message": "요청이 성공적으로 처리되었습니다.",
  "timestamp": "2024-01-01 12:00:00"
}
```

**실패 응답:**
```json
{
  "success": false,
  "errorCode": "2001",
  "message": "이미 존재하는 사용자입니다.",
  "errorDetail": "이메일: test@example.com",
  "timestamp": "2024-01-01 12:00:00"
}
```

### **에러 코드 체계**
- **1000번대**: 공통 에러 (입력값 검증, 인증, 권한 등)
- **2000번대**: 사용자 관련 에러 (사용자 없음, 중복 등)
- **3000번대**: 스터디 그룹 관련 에러 (그룹 없음, 권한 없음 등)
- **4000번대**: 스터디 세션 관련 에러 (세션 없음, 시간 충돌 등)
- **5000번대**: 출석 관련 에러
- **6000번대**: 폰 사용 제한 관련 에러
- **7000번대**: 알림 관련 에러
- **8000번대**: 파일 관련 에러

### **API 엔드포인트**
- **인증**: `/api/auth/**` - 로그인, 회원가입
- **사용자**: `/api/users/**` - 사용자 관리
- **스터디 그룹**: `/api/study-groups/**` - 그룹 관리
- **스터디 세션**: `/api/study-sessions/**` - 세션 관리
- **출석**: `/api/attendances/**` - 출석 관리
- **폰 사용 제한**: `/api/phone-exceptions/**` - 예외 관리
- **실시간 모니터링**: `/api/realtime/**` - 실시간 상태

### **추가 문서**
- **API 응답 가이드**: [API_RESPONSE_GUIDE.md](./API_RESPONSE_GUIDE.md)
- **API 응답 예시**: [API_EXAMPLES.md](./API_EXAMPLES.md)

---

## 🔐 **인증 및 보안**

### **JWT 토큰 기반 인증**
1. **회원가입**: `POST /api/auth/register`
2. **로그인**: `POST /api/auth/login`
3. **API 호출**: `Authorization: Bearer {JWT_TOKEN}` 헤더 포함

### **보안 설정**
- **인증 필요 없는 엔드포인트**: `/api/auth/**`, `/h2-console/**`
- **인증 필요한 엔드포인트**: 나머지 모든 API
- **권한 관리**: Role 기반 접근 제어

---

## 🌐 **실시간 기능**

### **WebSocket 연결**
- **엔드포인트**: `/ws`
- **프로토콜**: STOMP over SockJS
- **메시지 브로커**: `/topic`, `/queue`

### **알림 구독**
- **개인 알림**: `/user/{userId}/queue/notifications`
- **그룹 알림**: `/topic/study-group/{groupId}`
- **공개 알림**: `/topic/public`

---

## 🧪 **테스트 및 개발**

### **테스트 실행**
```bash
# 전체 테스트 실행
./mvnw test

# 테스트 커버리지 포함 실행
./mvnw clean test jacoco:report

# Windows에서 실행
run-tests.bat
```

### **테스트 구조**
- **단위 테스트**: `src/test/java/com/dmt/app/service/`
- **컨트롤러 테스트**: `src/test/java/com/dmt/app/controller/`
- **통합 테스트**: `src/test/java/com/dmt/app/integration/`
- **테스트 설정**: `src/test/resources/application-test.yml`

### **테스트 커버리지**
- JaCoCo 플러그인으로 코드 커버리지 측정
- 리포트 위치: `target/site/jacoco/index.html`

### **개발 도구**
- **Postman 테스트 컬렉션**: `DMT-API-Test-Collection.postman_collection.json`
- **Postman 환경 설정**: `DMT-API-Environment.postman_environment.json`
- **API 테스트 실행**: `run-api-tests.bat`
- **curl 명령어**: `curl-commands.md`
- **WebSocket 테스트**: `websocket-test.html`

### **테스트 시나리오**
1. **기본 플로우**: 회원가입 → 로그인 → 그룹 생성 → 세션 생성
2. **핵심 기능**: 폰 사용 제한 예외 신청 → 리더 승인
3. **실시간 기능**: WebSocket 연결 → 알림 구독 → 실시간 알림

### **API 테스트 자동화**
- **Postman 컬렉션**: 표준화된 응답 형식 검증
- **자동 테스트**: 응답 형식, 상태 코드, 에러 처리 검증
- **환경 변수**: 테스트 데이터 자동 관리
- **테스트 실행**: `run-api-tests.bat`로 간편 실행

---

## 📁 **프로젝트 구조**

```
src/
├── main/java/com/dmt/app/
│   ├── config/                    # 설정 클래스
│   ├── controller/                # REST API 컨트롤러
│   ├── service/                   # 비즈니스 로직 서비스
│   ├── repository/                # 데이터 접근 계층
│   ├── entity/                    # 데이터베이스 엔티티
│   ├── dto/                       # 데이터 전송 객체
│   ├── security/                  # 보안 관련 클래스
│   ├── exception/                 # 예외 처리
│   └── scheduler/                 # 스케줄러
├── main/resources/
│   ├── application.yml            # 애플리케이션 설정
│   └── static/                    # 정적 리소스
└── test/                          # 테스트 코드
```

---

## ⚙️ **설정**

### **application.yml 주요 설정**
```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
  
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true

jwt:
  secret: your-secret-key
  expiration: 86400000  # 24시간
```

---

## 🔄 **자동화 기능**

### **스케줄러**
- **세션 자동 시작**: 매분마다 예정된 세션 진행 상태로 변경
- **출석 자동 체크**: 매분마다 현재 세션 출석 상태 확인
- **예외 만료 처리**: 매시간마다 만료된 예외 상태 변경
- **폰 사용 제한 모니터링**: 매 5분마다 제한 상태 확인

---

## 📊 **데이터베이스**

### **H2 인메모리 데이터베이스 (개발용)**
- **URL**: `jdbc:h2:mem:testdb`
- **사용자명**: `sa`
- **비밀번호**: (없음)
- **콘솔**: `http://localhost:8080/h2-console`

### **주요 테이블**
- `users`: 사용자 정보
- `study_groups`: 스터디 그룹
- `study_sessions`: 스터디 세션
- `attendances`: 출석 기록
- `phone_restriction_exceptions`: 폰 사용 제한 예외

---

## 🚨 **문제 해결**

### **일반적인 문제들**
1. **포트 충돌**: 8080 포트가 사용 중인 경우 `application.yml`에서 포트 변경
2. **Java 버전**: Java 21 이상 필요
3. **Maven 의존성**: `mvn clean install`로 의존성 재다운로드

### **로그 확인**
- **애플리케이션 로그**: 콘솔 출력
- **Hibernate SQL**: `application.yml`에서 `show-sql: true` 설정

---

## 🤝 **기여하기**

### **개발 환경 설정**
1. 프로젝트 포크
2. 로컬에 클론
3. 기능 브랜치 생성
4. 개발 및 테스트
5. Pull Request 생성

### **코드 컨벤션**
- Java 코드 스타일 가이드 준수
- 적절한 주석 및 문서화
- 단위 테스트 작성

---

## 📄 **라이선스**

이 프로젝트는 MIT 라이선스 하에 배포됩니다.

---

## 📞 **연락처**

- **프로젝트**: [GitHub Issues](https://github.com/your-username/dmt-backend/issues)
- **개발팀**: dev@dmt.com

---

**버전**: 2.0  
**최종 업데이트**: 2024년 8월  
**작성자**: DMT 개발팀 