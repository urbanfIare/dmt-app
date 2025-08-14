# 📋 DMT API 응답 예시

## 🎯 **목적**

이 문서는 DMT 프로젝트의 표준화된 API 응답 형식에 대한 구체적인 예시를 제공합니다.

## 📊 **응답 형식 개요**

### **성공 응답 구조**
```json
{
  "success": true,
  "data": {...},
  "message": "요청이 성공적으로 처리되었습니다.",
  "timestamp": "2024-01-01 12:00:00"
}
```

### **실패 응답 구조**
```json
{
  "success": false,
  "errorCode": "2001",
  "message": "이미 존재하는 사용자입니다.",
  "errorDetail": "이메일: test@example.com",
  "timestamp": "2024-01-01 12:00:00"
}
```

## 👤 **사용자 관리 API 예시**

### **1. 사용자 등록 성공**

**요청:**
```http
POST /api/users/register
Content-Type: application/json

{
  "email": "newuser@example.com",
  "nickname": "새사용자",
  "password": "password123",
  "phoneNumber": "010-1234-5678"
}
```

**응답:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "email": "newuser@example.com",
    "nickname": "새사용자",
    "phoneNumber": "010-1234-5678",
    "role": "USER",
    "createdAt": "2024-01-01 12:00:00",
    "updatedAt": "2024-01-01 12:00:00"
  },
  "message": "사용자가 성공적으로 등록되었습니다.",
  "timestamp": "2024-01-01 12:00:00"
}
```

### **2. 사용자 등록 실패 - 이메일 중복**

**요청:**
```http
POST /api/users/register
Content-Type: application/json

{
  "email": "existing@example.com",
  "nickname": "중복사용자",
  "password": "password123",
  "phoneNumber": "010-1234-5678"
}
```

**응답:**
```json
{
  "success": false,
  "errorCode": "2002",
  "message": "이미 사용 중인 이메일입니다.",
  "errorDetail": "이메일: existing@example.com",
  "timestamp": "2024-01-01 12:00:00"
}
```

### **3. 사용자 조회 성공**

**요청:**
```http
GET /api/users/1
```

**응답:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "email": "user@example.com",
    "nickname": "테스트유저",
    "phoneNumber": "010-1234-5678",
    "role": "USER",
    "createdAt": "2024-01-01 12:00:00",
    "updatedAt": "2024-01-01 12:00:00"
  },
  "message": "요청이 성공적으로 처리되었습니다.",
  "timestamp": "2024-01-01 12:00:00"
}
```

### **4. 사용자 조회 실패 - 사용자 없음**

**요청:**
```http
GET /api/users/999
```

**응답:**
```json
{
  "success": false,
  "errorCode": "2000",
  "message": "사용자를 찾을 수 없습니다.",
  "errorDetail": "사용자 ID: 999",
  "timestamp": "2024-01-01 12:00:00"
}
```

## 🏠 **스터디 그룹 관리 API 예시**

### **1. 스터디 그룹 생성 성공**

**요청:**
```http
POST /api/study-groups?userId=1
Content-Type: application/json

{
  "name": "알고리즘 스터디",
  "description": "알고리즘 문제 풀이 스터디 그룹입니다.",
  "maxMembers": 8
}
```

**응답:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "알고리즘 스터디",
    "description": "알고리즘 문제 풀이 스터디 그룹입니다.",
    "maxMembers": 8,
    "minMembers": 2,
    "status": "ACTIVE",
    "createdAt": "2024-01-01 12:00:00",
    "updatedAt": "2024-01-01 12:00:00"
  },
  "message": "스터디 그룹이 성공적으로 생성되었습니다.",
  "timestamp": "2024-01-01 12:00:00"
}
```

### **2. 스터디 그룹 수정 실패 - 권한 없음**

**요청:**
```http
PUT /api/study-groups/1?userId=2
Content-Type: application/json

{
  "name": "수정된 그룹명",
  "description": "수정된 설명",
  "maxMembers": 10
}
```

**응답:**
```json
{
  "success": false,
  "errorCode": "3003",
  "message": "그룹 리더만 수정할 수 있습니다.",
  "errorDetail": "사용자 2는 그룹 1의 리더가 아닙니다.",
  "timestamp": "2024-01-01 12:00:00"
}
```

### **3. 스터디 그룹 참여 성공**

**요청:**
```http
POST /api/study-groups/1/join?userId=3
```

**응답:**
```json
{
  "success": true,
  "message": "스터디 그룹에 성공적으로 참여했습니다.",
  "timestamp": "2024-01-01 12:00:00"
}
```

## 🔐 **인증 API 예시**

### **1. 로그인 성공**

**요청:**
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

**응답:**
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "email": "user@example.com",
    "nickname": "테스트유저",
    "role": "USER",
    "message": "로그인이 성공했습니다."
  },
  "message": "요청이 성공적으로 처리되었습니다.",
  "timestamp": "2024-01-01 12:00:00"
}
```

### **2. 로그인 실패 - 잘못된 비밀번호**

**요청:**
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "wrongpassword"
}
```

**응답:**
```json
{
  "success": false,
  "errorCode": "2004",
  "message": "비밀번호가 올바르지 않습니다.",
  "timestamp": "2024-01-01 12:00:00"
}
```

## 📚 **스터디 세션 API 예시**

### **1. 세션 생성 성공**

**요청:**
```http
POST /api/study-sessions
Content-Type: application/json

{
  "studyGroupId": 1,
  "sessionName": "알고리즘 문제 풀이",
  "startTime": "2024-01-01T14:00:00",
  "endTime": "2024-01-01T16:00:00"
}
```

**응답:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "studyGroupId": 1,
    "sessionName": "알고리즘 문제 풀이",
    "startTime": "2024-01-01T14:00:00",
    "endTime": "2024-01-01T16:00:00",
    "durationMinutes": 120,
    "status": "SCHEDULED",
    "createdAt": "2024-01-01 12:00:00",
    "updatedAt": "2024-01-01 12:00:00"
  },
  "message": "요청이 성공적으로 처리되었습니다.",
  "timestamp": "2024-01-01 12:00:00"
}
```

### **2. 세션 생성 실패 - 잘못된 시간**

**요청:**
```http
POST /api/study-sessions
Content-Type: application/json

{
  "studyGroupId": 1,
  "sessionName": "잘못된 세션",
  "startTime": "2024-01-01T16:00:00",
  "endTime": "2024-01-01T14:00:00"
}
```

**응답:**
```json
{
  "success": false,
  "errorCode": "4001",
  "message": "시작 시간은 종료 시간보다 빨라야 합니다.",
  "timestamp": "2024-01-01 12:00:00"
}
```

## 📊 **페이지네이션 응답 예시**

### **사용자 목록 조회 (페이지네이션)**

**요청:**
```http
GET /api/users?page=0&size=10
```

**응답:**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "email": "user1@example.com",
        "nickname": "사용자1",
        "phoneNumber": "010-1111-1111",
        "role": "USER",
        "createdAt": "2024-01-01 12:00:00",
        "updatedAt": "2024-01-01 12:00:00"
      },
      {
        "id": 2,
        "email": "user2@example.com",
        "nickname": "사용자2",
        "phoneNumber": "010-2222-2222",
        "role": "USER",
        "createdAt": "2024-01-01 12:00:00",
        "updatedAt": "2024-01-01 12:00:00"
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 25,
    "totalPages": 3,
    "hasNext": true,
    "hasPrevious": false
  },
  "message": "요청이 성공적으로 처리되었습니다.",
  "timestamp": "2024-01-01 12:00:00"
}
```

## 🚨 **공통 에러 응답 예시**

### **1. 입력값 검증 실패**

**응답:**
```json
{
  "success": false,
  "errorCode": "1000",
  "message": "입력값 검증에 실패했습니다.",
  "errorDetail": "이메일 형식이 올바르지 않습니다, 닉네임은 2자 이상이어야 합니다",
  "timestamp": "2024-01-01 12:00:00"
}
```

### **2. 인증 필요**

**응답:**
```json
{
  "success": false,
  "errorCode": "1006",
  "message": "인증이 필요합니다.",
  "timestamp": "2024-01-01 12:00:00"
}
```

### **3. 접근 거부**

**응답:**
```json
{
  "success": false,
  "errorCode": "1005",
  "message": "접근이 거부되었습니다.",
  "timestamp": "2024-01-01 12:00:00"
}
```

### **4. 리소스 없음**

**응답:**
```json
{
  "success": false,
  "errorCode": "1002",
  "message": "요청한 리소스를 찾을 수 없습니다.",
  "timestamp": "2024-01-01 12:00:00"
}
```

### **5. 서버 내부 오류**

**응답:**
```json
{
  "success": false,
  "errorCode": "1003",
  "message": "서버 내부 오류가 발생했습니다.",
  "timestamp": "2024-01-01 12:00:00"
}
```

## 🔧 **테스트 방법**

### **Postman에서 테스트**

1. **환경 변수 설정**
   - `baseUrl`: `http://localhost:8080`
   - `authToken`: JWT 토큰 저장

2. **Pre-request Script (인증이 필요한 API)**
   ```javascript
   pm.request.headers.add({
       key: 'Authorization',
       value: 'Bearer ' + pm.environment.get('authToken')
   });
   ```

3. **Tests (응답 검증)**
   ```javascript
   pm.test("응답 형식 검증", function () {
       const response = pm.response.json();
       
       pm.expect(response).to.have.property('success');
       pm.expect(response).to.have.property('timestamp');
       
       if (response.success) {
           pm.expect(response).to.have.property('data');
           pm.expect(response).to.have.property('message');
       } else {
           pm.expect(response).to.have.property('errorCode');
           pm.expect(response).to.have.property('message');
       }
   });
   ```

### **curl로 테스트**

```bash
# 사용자 등록
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "nickname": "테스트유저",
    "password": "password123",
    "phoneNumber": "010-1234-5678"
  }'

# 로그인
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'

# 사용자 조회 (JWT 토큰 필요)
curl -X GET http://localhost:8080/api/users/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 📋 **체크리스트**

API 응답 테스트 시 다음 항목들을 확인하세요:

- [ ] `success` 필드가 올바르게 설정되었는가?
- [ ] 성공 시 `data` 필드가 포함되었는가?
- [ ] 실패 시 `errorCode`와 `message`가 포함되었는가?
- [ ] `timestamp` 필드가 모든 응답에 포함되었는가?
- [ ] 에러 코드가 문서와 일치하는가?
- [ ] 응답 메시지가 사용자 친화적인가?
- [ ] HTTP 상태 코드가 에러 코드와 일치하는가?

---

**버전**: 1.0  
**최종 업데이트**: 2024년 1월  
**작성자**: DMT 개발팀 