# DMT API - curl 명령어 모음

## 🚀 **사용자 관리 API**

### **사용자 등록**
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "nickname": "테스트유저",
    "password": "password123",
    "phoneNumber": "010-1234-5678"
  }'
```

### **사용자 조회 (ID로)**
```bash
curl -X GET http://localhost:8080/api/users/1
```

### **사용자 조회 (이메일로)**
```bash
curl -X GET http://localhost:8080/api/users/email/test@example.com
```

### **전체 사용자 조회**
```bash
curl -X GET http://localhost:8080/api/users
```

### **사용자 정보 수정**
```bash
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "nickname": "수정된닉네임",
    "phoneNumber": "010-1111-2222"
  }'
```

### **사용자 삭제**
```bash
curl -X DELETE http://localhost:8080/api/users/1
```

## 🏠 **스터디 그룹 관리 API**

### **스터디 그룹 생성**
```bash
curl -X POST "http://localhost:8080/api/study-groups?userId=1" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "테스트 스터디 그룹",
    "description": "DMT 앱 테스트용 그룹입니다.",
    "maxMembers": 5
  }'
```

### **스터디 그룹 조회 (ID로)**
```bash
curl -X GET http://localhost:8080/api/study-groups/1
```

### **전체 스터디 그룹 조회**
```bash
curl -X GET http://localhost:8080/api/study-groups
```

### **활성 스터디 그룹 조회**
```bash
curl -X GET http://localhost:8080/api/study-groups/active
```

### **사용자별 스터디 그룹 조회**
```bash
curl -X GET http://localhost:8080/api/study-groups/user/1
```

### **스터디 그룹 수정**
```bash
curl -X PUT "http://localhost:8080/api/study-groups/1?userId=1" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "수정된 그룹명",
    "description": "수정된 설명",
    "maxMembers": 10
  }'
```

### **스터디 그룹 삭제**
```bash
curl -X DELETE "http://localhost:8080/api/study-groups/1?userId=1"
```

### **스터디 그룹 참여**
```bash
curl -X POST "http://localhost:8080/api/study-groups/1/join?userId=2"
```

### **스터디 그룹 탈퇴**
```bash
curl -X POST "http://localhost:8080/api/study-groups/1/leave?userId=2"
```

## 📅 **스터디 세션 관리 API**

### **스터디 세션 생성**
```bash
curl -X POST "http://localhost:8080/api/study-sessions?userId=1" \
  -H "Content-Type: application/json" \
  -d '{
    "studyGroupId": 1,
    "sessionName": "첫 번째 스터디 세션",
    "startTime": "2024-08-13T14:00:00",
    "endTime": "2024-08-13T16:00:00"
  }'
```

### **스터디 세션 조회 (ID로)**
```bash
curl -X GET http://localhost:8080/api/study-sessions/1
```

### **그룹별 스터디 세션 조회**
```bash
curl -X GET http://localhost:8080/api/study-sessions/study-group/1
```

### **예정된 스터디 세션 조회**
```bash
curl -X GET http://localhost:8080/api/study-sessions/study-group/1/upcoming
```

### **현재 진행 중인 세션 조회**
```bash
curl -X GET http://localhost:8080/api/study-sessions/current
```

### **스터디 세션 수정**
```bash
curl -X PUT "http://localhost:8080/api/study-sessions/1?userId=1" \
  -H "Content-Type: application/json" \
  -d '{
    "sessionName": "수정된 세션명",
    "startTime": "2024-08-13T15:00:00",
    "endTime": "2024-08-13T17:00:00"
  }'
```

### **스터디 세션 상태 변경**
```bash
curl -X PUT "http://localhost:8080/api/study-sessions/1/status?userId=1" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "IN_PROGRESS"
  }'
```

### **스터디 세션 삭제**
```bash
curl -X DELETE "http://localhost:8080/api/study-sessions/1?userId=1"
```

## ✅ **출석 관리 API**

### **출석 기록 생성**
```bash
curl -X POST http://localhost:8080/api/attendances \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "studySessionId": 1,
    "status": "PRESENT",
    "note": "정상 출석"
  }'
```

### **출석 기록 조회 (ID로)**
```bash
curl -X GET http://localhost:8080/api/attendances/1
```

### **사용자별 출석 기록 조회**
```bash
curl -X GET http://localhost:8080/api/attendances/user/1
```

### **스터디 세션별 출석 기록 조회**
```bash
curl -X GET http://localhost:8080/api/attendances/study-session/1
```

### **그룹별 출석 기록 조회**
```bash
curl -X GET http://localhost:8080/api/attendances/study-group/1
```

### **출석 기록 수정**
```bash
curl -X PUT "http://localhost:8080/api/attendances/1?userId=1" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "LATE",
    "note": "10분 지각"
  }'
```

### **출석 기록 삭제**
```bash
curl -X DELETE "http://localhost:8080/api/attendances/1?userId=1"
```

### **출석률 요약 조회**
```bash
curl -X GET http://localhost:8080/api/attendances/summary/user/1/group/1
```

## 📱 **폰 사용 제한 예외 API**

### **폰 사용 제한 예외 신청**
```bash
curl -X POST http://localhost:8080/api/phone-exceptions \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "studySessionId": 1,
    "reason": "긴급한 연락이 필요합니다",
    "exceptionStartTime": "2024-08-13T14:30:00",
    "exceptionEndTime": "2024-08-13T15:00:00"
  }'
```

### **폰 사용 제한 예외 조회 (ID로)**
```bash
curl -X GET http://localhost:8080/api/phone-exceptions/1
```

### **사용자별 폰 사용 제한 예외 조회**
```bash
curl -X GET http://localhost:8080/api/phone-exceptions/user/1
```

### **스터디 세션별 폰 사용 제한 예외 조회**
```bash
curl -X GET http://localhost:8080/api/phone-exceptions/study-session/1
```

### **그룹별 대기 중인 예외 조회**
```bash
curl -X GET http://localhost:8080/api/phone-exceptions/study-group/1/pending
```

### **리더별 대기 중인 예외 조회**
```bash
curl -X GET http://localhost:8080/api/phone-exceptions/leader/1/pending
```

### **폰 사용 제한 예외 승인/거절**
```bash
curl -X PUT "http://localhost:8080/api/phone-exceptions/1/approve?leaderId=1" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "APPROVED",
    "note": "승인합니다"
  }'
```

### **폰 사용 제한 예외 수정**
```bash
curl -X PUT "http://localhost:8080/api/phone-exceptions/1?userId=1" \
  -H "Content-Type: application/json" \
  -d '{
    "reason": "수정된 사유",
    "exceptionStartTime": "2024-08-13T14:45:00",
    "exceptionEndTime": "2024-08-13T15:15:00"
  }'
```

### **폰 사용 제한 예외 삭제**
```bash
curl -X DELETE "http://localhost:8080/api/phone-exceptions/1?userId=1"
```

### **현재 활성화된 예외 조회**
```bash
curl -X GET http://localhost:8080/api/phone-exceptions/active
```

## 🔧 **테스트용 데이터 생성 순서**

### **1단계: 사용자 생성**
```bash
# 관리자 사용자 생성
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@dmt.com",
    "nickname": "관리자",
    "password": "admin123",
    "phoneNumber": "010-0000-0000"
  }'

# 일반 사용자 생성
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user1@dmt.com",
    "nickname": "테스트유저1",
    "password": "user123",
    "phoneNumber": "010-1111-1111"
  }'
```

### **2단계: 스터디 그룹 생성**
```bash
curl -X POST "http://localhost:8080/api/study-groups?userId=1" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "테스트 스터디 그룹",
    "description": "DMT 앱 테스트를 위한 스터디 그룹입니다.",
    "maxMembers": 5
  }'
```

### **3단계: 스터디 세션 생성**
```bash
curl -X POST "http://localhost:8080/api/study-sessions?userId=1" \
  -H "Content-Type: application/json" \
  -d '{
    "studyGroupId": 1,
    "sessionName": "첫 번째 스터디 세션",
    "startTime": "2024-08-13T14:00:00",
    "endTime": "2024-08-13T16:00:00"
  }'
```

## 📝 **사용 팁**

1. **Windows에서 사용할 때**: `\` 대신 `^` 사용
2. **JSON 데이터**: 작은따옴표(`'`) 대신 큰따옴표(`"`) 사용 권장
3. **응답 확인**: `-v` 옵션 추가하여 상세 정보 확인
4. **파일로 저장**: `-o` 옵션으로 응답을 파일로 저장

## 🚨 **주의사항**

- 모든 API는 `localhost:8080`에서 실행 중
- 사용자 ID는 실제 생성된 사용자 ID로 변경 필요
- 그룹 ID, 세션 ID 등도 실제 생성된 값으로 변경 필요
- 테스트 전에 H2 데이터베이스에 테스트 데이터가 있는지 확인 