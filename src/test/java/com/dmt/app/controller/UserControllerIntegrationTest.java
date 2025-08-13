package com.dmt.app.controller;

import com.dmt.app.dto.UserDto;
import com.dmt.app.entity.User;
import com.dmt.app.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserControllerIntegrationTest {
    
    @Autowired
    private WebApplicationContext webApplicationContext;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private MockMvc mockMvc;
    
    private User testUser;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        // 테스트 사용자 생성
        testUser = User.builder()
                .email("test@example.com")
                .nickname("테스트유저")
                .password("password123")
                .phoneNumber("010-1234-5678")
                .role(User.UserRole.USER)
                .build();
        
        testUser = userRepository.save(testUser);
    }
    
    @Test
    @DisplayName("사용자 등록 API 테스트 - 성공")
    void registerUser_Success() throws Exception {
        // given
        UserDto.CreateRequest request = UserDto.CreateRequest.builder()
                .email("new@example.com")
                .nickname("새유저")
                .password("password123")
                .phoneNumber("010-9876-5432")
                .build();
        
        // when & then
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("new@example.com"))
                .andExpect(jsonPath("$.nickname").value("새유저"));
    }
    
    @Test
    @DisplayName("사용자 등록 API 테스트 - 이메일 중복 실패")
    void registerUser_Fail_DuplicateEmail() throws Exception {
        // given
        UserDto.CreateRequest request = UserDto.CreateRequest.builder()
                .email("test@example.com") // 이미 존재하는 이메일
                .nickname("새유저")
                .password("password123")
                .phoneNumber("010-9876-5432")
                .build();
        
        // when & then
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이미 존재하는 이메일입니다."));
    }
    
    @Test
    @DisplayName("사용자 조회 API 테스트 - 성공")
    void getUserById_Success() throws Exception {
        // when & then
        mockMvc.perform(get("/api/users/{userId}", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUser.getId()))
                .andExpect(jsonPath("$.email").value(testUser.getEmail()))
                .andExpect(jsonPath("$.nickname").value(testUser.getNickname()));
    }
    
    @Test
    @DisplayName("사용자 조회 API 테스트 - 존재하지 않는 사용자")
    void getUserById_Fail_UserNotFound() throws Exception {
        // when & then
        mockMvc.perform(get("/api/users/{userId}", 999L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("사용자를 찾을 수 없습니다."));
    }
    
    @Test
    @DisplayName("사용자 정보 수정 API 테스트 - 성공")
    void updateUser_Success() throws Exception {
        // given
        UserDto.UpdateRequest request = UserDto.UpdateRequest.builder()
                .nickname("수정된닉네임")
                .phoneNumber("010-1111-2222")
                .build();
        
        // when & then
        mockMvc.perform(put("/api/users/{userId}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("수정된닉네임"))
                .andExpect(jsonPath("$.phoneNumber").value("010-1111-2222"));
    }
    
    @Test
    @DisplayName("전체 사용자 조회 API 테스트 - 성공")
    void getAllUsers_Success() throws Exception {
        // when & then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].email").value(testUser.getEmail()));
    }
    
    @Test
    @DisplayName("사용자 삭제 API 테스트 - 성공")
    void deleteUser_Success() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/users/{userId}", testUser.getId()))
                .andExpect(status().isNoContent());
        
        // 삭제 확인
        assert userRepository.findById(testUser.getId()).isEmpty();
    }
} 