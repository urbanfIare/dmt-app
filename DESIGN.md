# DMT (Don't Touch My Time) - 앱 설계서

## 📱 **앱 개요**

**앱 이름**: don't touch my time (DMT)  
**핵심 기능**: 스터디 그룹을 만들어서 정해진 시간에 스터디를 진행하며, 스터디 시간 동안 폰을 못보게 하는 디지털 디톡스 서비스

## 🎯 **핵심 기능**

### 1. **스터디 그룹 관리**
- 그룹 생성/수정/삭제
- 멤버 초대/추방/권한 관리
- 최소 2명 이상 구성 (최대 인원은 설정 가능)

### 2. **폰 사용 제한 (핵심 기능)**
- 스터디 시간 동안 폰 사용 차단
- 스터디장의 예외 승인 시스템
- 긴급 상황 시 비상 연락망 제공

### 3. **출결 관리**
- 스터디 참석/불참 기록
- 출석률 통계 및 리포트
- 지각/조퇴 관리

## 🏗️ **기술 스택**

- **Backend**: Spring Boot 3.5.4
- **Language**: Java 21
- **Build Tool**: Maven
- **Database**: H2 (개발용), 추후 MySQL/PostgreSQL로 변경 가능
- **ORM**: Spring Data JPA + Hibernate
- **Validation**: Spring Boot Validation
- **Security**: Spring Security + JWT
- **Real-time**: WebSocket + STOMP
- **Documentation**: Swagger/OpenAPI (SpringDoc)

## 📊 **데이터베이스 설계**

### **엔티티 관계도**

```
User (사용자)
├── StudyGroupMember (그룹 멤버십)
│   └── StudyGroup (스터디 그룹)
│       ├── StudySession (스터디 세션)
│       │   ├── Attendance (출석 기록)
│       │   └── PhoneRestrictionException (폰 사용 제한 예외)
│       └── StudyGroupMember (그룹 멤버)
```

### **1. User (사용자)**
```java
- id: Long (PK)
- email: String (unique, not null)
- nickname: String (not null)
- password: String (not null)
- role: UserRole (ADMIN, USER)
- phoneNumber: String
- createdAt: LocalDateTime
- updatedAt: LocalDateTime
```

**관계**:
- `StudyGroupMember`와 1:N (그룹 멤버십)
- `Attendance`와 1:N (출석 기록)
- `PhoneRestrictionException`과 1:N (폰 사용 제한 예외)

### **2. StudyGroup (스터디 그룹)**
```java
- id: Long (PK)
- name: String (not null)
- description: String (TEXT)
- maxMembers: Integer
- minMembers: Integer (not null, default: 2)
- status: StudyGroupStatus (ACTIVE, INACTIVE, DELETED)
- createdAt: LocalDateTime
- updatedAt: LocalDateTime
```

**관계**:
- `StudyGroupMember`와 1:N (그룹 멤버)
- `StudySession`과 1:N (스터디 세션)

### **3. StudyGroupMember (그룹 멤버십)**
```java
- id: Long (PK)
- user: User (FK, not null)
- studyGroup: StudyGroup (FK, not null)
- role: MemberRole (LEADER, MEMBER)
- joinedAt: LocalDateTime
- isActive: Boolean (default: true)
```

**역할**:
- **LEADER**: 그룹 관리, 폰 사용 제한 예외 승인
- **MEMBER**: 일반 멤버

### **4. StudySession (스터디 세션)**
```java
- id: Long (PK)
- studyGroup: StudyGroup (FK, not null)
- sessionName: String (not null)
- startTime: LocalDateTime (not null)
- endTime: LocalDateTime (not null)
- durationMinutes: Integer
- status: SessionStatus (SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED)
- createdAt: LocalDateTime
- updatedAt: LocalDateTime
```

**상태**:
- **SCHEDULED**: 예정된 세션
- **IN_PROGRESS**: 진행 중인 세션
- **COMPLETED**: 완료된 세션
- **CANCELLED**: 취소된 세션

### **5. Attendance (출석 기록)**
```java
- id: Long (PK)
- user: User (FK, not null)
- studySession: StudySession (FK, not null)
- status: AttendanceStatus (PRESENT, ABSENT, LATE, EARLY_LEAVE, EXCUSED)
- arrivalTime: LocalDateTime
- departureTime: LocalDateTime
- lateMinutes: Integer
- earlyLeaveMinutes: Integer
- note: String (TEXT)
- createdAt: LocalDateTime
- updatedAt: LocalDateTime
```

**출석 상태**:
- **PRESENT**: 정상 출석
- **ABSENT**: 결석
- **LATE**: 지각
- **EARLY_LEAVE**: 조퇴
- **EXCUSED**: 사유 결석

### **6. PhoneRestrictionException (폰 사용 제한 예외)**
```java
- id: Long (PK)
- user: User (FK, not null)
- studySession: StudySession (FK, not null)
- approvedBy: User (FK, 승인자)
- status: ExceptionStatus (PENDING, APPROVED, REJECTED, EXPIRED)
- reason: String (TEXT, not null)
- approvedAt: LocalDateTime
- exceptionStartTime: LocalDateTime
- exceptionEndTime: LocalDateTime
- createdAt: LocalDateTime
- updatedAt: LocalDateTime
```

**예외 상태**:
- **PENDING**: 승인 대기
- **APPROVED**: 승인됨
- **REJECTED**: 거절됨
- **EXPIRED**: 만료됨

## 🔄 **주요 비즈니스 플로우**

### **1. 스터디 그룹 생성**
1. 사용자가 그룹 생성 요청
2. 그룹 정보 입력 (이름, 설명, 최대 인원 등)
3. 생성자가 자동으로 LEADER 역할 부여
4. 그룹 상태를 ACTIVE로 설정

### **2. 스터디 세션 생성**
1. 그룹 LEADER가 세션 생성
2. 세션 정보 입력 (이름, 시작/종료 시간)
3. 세션 상태를 SCHEDULED로 설정
4. 그룹 멤버들에게 알림 발송

### **3. 폰 사용 제한 예외 신청**
1. 멤버가 폰 사용이 필요한 사유 제출
2. 예외 상태를 PENDING으로 설정
3. LEADER에게 승인 요청
4. LEADER 검토 후 승인/거절 결정

### **4. 출석 관리**
1. 세션 시작 시 자동 출석 체크
2. 지각/조퇴 시간 자동 계산
3. 사유 결석 시 LEADER 승인 필요
4. 출석률 통계 자동 생성

## 🚀 **개발 단계**

### **Phase 1: 기본 구조 (완료)**
- [x] 프로젝트 설정 (Spring Boot + JPA)
- [x] 데이터베이스 설계 및 엔티티 생성
- [x] Repository 계층 구현
- [x] Service 계층 구현
- [x] Controller 계층 구현
- [x] DTO 클래스 구현
- [x] 예외 처리 시스템
- [x] 유효성 검증 (Bean Validation)

### **Phase 2: 핵심 기능 (완료)**
- [x] 사용자 인증/권한 관리 (Spring Security + JWT)
- [x] 스터디 그룹 CRUD
- [x] 스터디 세션 관리
- [x] 출석 관리 시스템
- [x] 폰 사용 제한 예외 신청/승인 시스템

### **Phase 3: 고급 기능 (완료)**
- [x] 폰 사용 제한 로직 구현
- [x] 실시간 알림 시스템 (WebSocket + STOMP)
- [x] 자동화된 스케줄러
- [x] API 문서화 (Swagger/OpenAPI)

### **Phase 4: 실시간 기능 (완료)**
- [x] WebSocket 설정 및 연결 관리
- [x] 실시간 알림 서비스
- [x] 폰 사용 제한 상태 실시간 모니터링
- [x] 테스트용 WebSocket 페이지

## 💡 **향후 확장 가능한 기능**

### **학습 효과 증진**
- 집중도 측정 (앱 사용 시간 기록)
- 성취도 시스템 (연속 출석, 목표 달성 시 뱃지/포인트)
- 스터디 로그 (매일의 학습 내용, 느낀점 기록)

### **커뮤니티 기능**
- 스터디 후기 (각 세션 후 간단한 피드백)
- 멤버 간 소통 (스터디 전후 간단한 메시지)
- 성장 그래프 (개인/팀별 학습 진도 시각화)

### **편의 기능**
- 알림 시스템 (스터디 시작 전 리마인더)
- 날씨 연동 (날씨에 따른 스터디 장소 추천)
- 음악 추천 (집중력 향상에 도움되는 BGM)

## 🔧 **기술적 고려사항**

### **보안**
- 사용자 인증 및 권한 관리
- API 보안 (JWT 토큰 기반)
- 데이터 암호화

### **성능**
- 데이터베이스 인덱싱
- 캐싱 전략
- API 응답 시간 최적화

### **확장성**
- 마이크로서비스 아키텍처 고려
- 데이터베이스 샤딩 전략
- 로드 밸런싱

## 📝 **API 설계 예시**

### **사용자 관리**
```
POST   /api/users/register     # 사용자 등록
POST   /api/users/login        # 로그인
GET    /api/users/profile      # 프로필 조회
PUT    /api/users/profile      # 프로필 수정
```

### **스터디 그룹 관리**
```
POST   /api/study-groups       # 그룹 생성
GET    /api/study-groups       # 그룹 목록 조회
GET    /api/study-groups/{id}  # 그룹 상세 조회
PUT    /api/study-groups/{id}  # 그룹 수정
DELETE /api/study-groups/{id}  # 그룹 삭제
```

### **스터디 세션 관리**
```
POST   /api/study-sessions     # 세션 생성
GET    /api/study-sessions     # 세션 목록 조회
PUT    /api/study-sessions/{id}/status  # 세션 상태 변경
```

### **출석 관리**
```
POST   /api/attendances        # 출석 기록 생성
GET    /api/attendances        # 출석 기록 조회
PUT    /api/attendances/{id}   # 출석 기록 수정
```

### **폰 사용 제한 예외**
```
POST   /api/phone-exceptions   # 예외 신청
GET    /api/phone-exceptions   # 예외 목록 조회
PUT    /api/phone-exceptions/{id}/approve  # 예외 승인
```

---

## 🚀 **구현 완료된 기능들 상세**

### **✅ 보안 시스템 구현 완료**

#### **Spring Security + JWT**
- **JWT 토큰 기반 인증**: 24시간 유효기간
- **비밀번호 암호화**: BCrypt 사용
- **권한 기반 접근 제어**: Role 기반 API 보안
- **세션리스 방식**: JWT 토큰으로 상태 관리

#### **보안 설정**
- **인증 필요 없는 엔드포인트**: `/api/auth/**`, `/h2-console/**`, `/api/users/register`
- **인증 필요한 엔드포인트**: 나머지 모든 API
- **CSRF 보호**: 비활성화 (JWT 사용으로 인해)

### **✅ 실시간 기능 구현 완료**

#### **WebSocket + STOMP**
- **연결 엔드포인트**: `/ws`
- **메시지 브로커**: `/topic`, `/queue`
- **개인 메시지**: `/user/{userId}/queue/notifications`
- **그룹 메시지**: `/topic/study-group/{groupId}`

#### **실시간 알림 타입**
- **세션 관리**: 시작/종료/취소 알림
- **폰 사용 제한**: 제한 시작/해제 알림
- **예외 처리**: 신청/승인/거절 알림
- **출석 관리**: 출석 알림 및 리마인더

#### **알림 우선순위**
- **LOW**: 낮음 (일반 정보)
- **NORMAL**: 보통 (기본 알림)
- **HIGH**: 높음 (중요 알림)
- **URGENT**: 긴급 (긴급 상황)

### **✅ API 문서화 완료**

#### **Swagger/OpenAPI**
- **접속 URL**: `http://localhost:8080/swagger-ui.html`
- **API 문서**: `http://localhost:8080/api-docs`
- **자동 문서 생성**: 코드 변경 시 자동 업데이트
- **테스트 가능**: 브라우저에서 직접 API 테스트

#### **문서 구성**
- **API 태그별 분류**: 인증, 사용자, 그룹, 세션, 출석, 예외
- **상세한 설명**: 각 API의 목적, 파라미터, 응답
- **예시 데이터**: 요청/응답 예시 포함
- **JWT 인증 지원**: Authorize 버튼으로 토큰 설정

### **✅ 자동화 시스템 구현 완료**

#### **스케줄러 기능**
- **세션 자동 시작**: 매분마다 예정된 세션 진행 상태로 변경
- **출석 자동 체크**: 매분마다 현재 세션 출석 상태 확인
- **예외 만료 처리**: 매시간마다 만료된 예외 상태 변경
- **폰 사용 제한 모니터링**: 매 5분마다 제한 상태 확인
- **세션 상태 동기화**: 매 30분마다 상태 동기화

### **✅ 테스트 환경 구축 완료**

#### **개발 도구**
- **Postman 컬렉션**: `DMT-API-Collection.postman_collection.json`
- **curl 명령어 모음**: `curl-commands.md`
- **WebSocket 테스트 페이지**: `http://localhost:8080/websocket-test.html`
- **H2 데이터베이스 콘솔**: `http://localhost:8080/h2-console`

#### **테스트 시나리오**
- **기본 플로우**: 회원가입 → 로그인 → 그룹 생성 → 세션 생성 → 출석 기록
- **핵심 기능**: 폰 사용 제한 예외 신청 → 리더 승인 → 제한 해제
- **실시간 기능**: WebSocket 연결 → 알림 구독 → 실시간 알림 수신

---

## 📁 **프로젝트 파일 구조**

```
dmt-app/
├── src/main/java/com/dmt/app/
│   ├── config/                    # 설정 클래스
│   │   ├── SecurityConfig.java    # Spring Security 설정
│   │   ├── WebSocketConfig.java   # WebSocket 설정
│   │   └── OpenAPIConfig.java     # Swagger 설정
│   ├── controller/                # REST API 컨트롤러
│   │   ├── AuthController.java    # 인증 API
│   │   ├── UserController.java    # 사용자 관리 API
│   │   ├── StudyGroupController.java      # 스터디 그룹 API
│   │   ├── StudySessionController.java    # 스터디 세션 API
│   │   ├── AttendanceController.java      # 출석 관리 API
│   │   ├── PhoneRestrictionExceptionController.java  # 예외 관리 API
│   │   ├── WebSocketNotificationController.java      # WebSocket API
│   │   └── RealTimeMonitoringController.java        # 실시간 모니터링 API
│   ├── service/                   # 비즈니스 로직 서비스
│   │   ├── AuthService.java       # 인증 서비스
│   │   ├── UserService.java       # 사용자 관리 서비스
│   │   ├── StudyGroupService.java # 스터디 그룹 서비스
│   │   ├── StudySessionService.java       # 스터디 세션 서비스
│   │   ├── AttendanceService.java # 출석 관리 서비스
│   │   ├── PhoneRestrictionExceptionService.java    # 예외 관리 서비스
│   │   └── NotificationService.java       # 알림 서비스
│   ├── repository/                # 데이터 접근 계층
│   │   ├── UserRepository.java    # 사용자 데이터 접근
│   │   ├── StudyGroupRepository.java      # 스터디 그룹 데이터 접근
│   │   ├── StudyGroupMemberRepository.java # 그룹 멤버 데이터 접근
│   │   ├── StudySessionRepository.java    # 스터디 세션 데이터 접근
│   │   ├── AttendanceRepository.java      # 출석 데이터 접근
│   │   └── PhoneRestrictionExceptionRepository.java # 예외 데이터 접근
│   ├── entity/                    # 데이터베이스 엔티티
│   │   ├── User.java              # 사용자 엔티티
│   │   ├── StudyGroup.java        # 스터디 그룹 엔티티
│   │   ├── StudyGroupMember.java  # 그룹 멤버 엔티티
│   │   ├── StudySession.java      # 스터디 세션 엔티티
│   │   ├── Attendance.java        # 출석 엔티티
│   │   └── PhoneRestrictionException.java # 예외 엔티티
│   ├── dto/                       # 데이터 전송 객체
│   │   ├── AuthDto.java           # 인증 관련 DTO
│   │   ├── UserDto.java           # 사용자 관련 DTO
│   │   ├── StudyGroupDto.java     # 스터디 그룹 관련 DTO
│   │   ├── StudySessionDto.java   # 스터디 세션 관련 DTO
│   │   ├── AttendanceDto.java     # 출석 관련 DTO
│   │   ├── PhoneRestrictionExceptionDto.java # 예외 관련 DTO
│   │   └── NotificationDto.java   # 알림 관련 DTO
│   ├── security/                  # 보안 관련 클래스
│   │   ├── JwtTokenUtil.java      # JWT 토큰 유틸리티
│   │   └── JwtAuthenticationFilter.java # JWT 인증 필터
│   ├── exception/                 # 예외 처리
│   │   ├── GlobalExceptionHandler.java # 전역 예외 처리
│   │   ├── BusinessException.java # 비즈니스 예외
│   │   └── ErrorCode.java         # 에러 코드 정의
│   └── scheduler/                 # 스케줄러
│       └── StudySessionScheduler.java # 스터디 세션 스케줄러
├── src/main/resources/
│   ├── application.yml            # 애플리케이션 설정
│   └── static/
│       └── websocket-test.html    # WebSocket 테스트 페이지
├── src/test/                      # 테스트 코드
├── DESIGN.md                      # 프로젝트 설계서
├── SWAGGER_GUIDE.md              # Swagger 사용 가이드
├── curl-commands.md               # curl 명령어 모음
└── DMT-API-Collection.postman_collection.json # Postman 컬렉션
```

---

## 🎯 **다음 개발 단계**

### **A단계: 테스트 코드 강화**
- [ ] 통합 테스트 추가
- [ ] API 테스트 자동화
- [ ] WebSocket 테스트
- [ ] 테스트 커버리지 향상

### **C단계: 프론트엔드 개발**
- [ ] React/Vue.js 웹 UI 구현
- [ ] 모바일 앱 개발
- [ ] 실시간 알림 UI
- [ ] 폰 사용 제한 상태 시각화

### **운영 및 배포**
- [ ] Docker 컨테이너화
- [ ] CI/CD 파이프라인 구축
- [ ] 모니터링 및 로깅 시스템
- [ ] 성능 최적화

---

**작성일**: 2024년  
**버전**: 2.0 (구현 완료 업데이트)  
**작성자**: DMT 개발팀 