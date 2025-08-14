package com.dmt.app.controller;

import com.dmt.app.dto.UserDto;
import com.dmt.app.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto.CreateRequest createRequest;
    private UserDto.UpdateRequest updateRequest;
    private UserDto.Response userResponse;
    private List<UserDto.Response> userList;

    @BeforeEach
    void setUp() {
        createRequest = UserDto.CreateRequest.builder()
                .email("test@example.com")
                .nickname("테스트유저")
                .password("password123")
                .phoneNumber("010-1234-5678")
                .build();

        updateRequest = UserDto.UpdateRequest.builder()
                .nickname("수정된닉네임")
                .phoneNumber("010-1111-2222")
                .build();

        userResponse = UserDto.Response.builder()
                .id(1L)
                .email("test@example.com")
                .nickname("테스트유저")
                .phoneNumber("010-1234-5678")
                .role("USER")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userList = Arrays.asList(userResponse);
    }

    @Test
    @DisplayName("사용자 등록 성공")
    void registerUser_Success() throws Exception {
        // given
        when(userService.createUser(any(UserDto.CreateRequest.class))).thenReturn(userResponse);

        // when & then
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userResponse.getId()))
                .andExpect(jsonPath("$.email").value(userResponse.getEmail()))
                .andExpect(jsonPath("$.nickname").value(userResponse.getNickname()));

        verify(userService).createUser(any(UserDto.CreateRequest.class));
    }

    @Test
    @DisplayName("사용자 등록 실패 - 잘못된 요청 데이터")
    void registerUser_Fail_InvalidRequest() throws Exception {
        // given
        UserDto.CreateRequest invalidRequest = UserDto.CreateRequest.builder()
                .email("invalid-email")
                .nickname("")
                .password("")
                .build();

        // when & then
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(UserDto.CreateRequest.class));
    }

    @Test
    @DisplayName("사용자 조회 성공")
    void getUserById_Success() throws Exception {
        // given
        when(userService.getUserById(1L)).thenReturn(userResponse);

        // when & then
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userResponse.getId()))
                .andExpect(jsonPath("$.email").value(userResponse.getEmail()));

        verify(userService).getUserById(1L);
    }

    @Test
    @DisplayName("이메일로 사용자 조회 성공")
    void getUserByEmail_Success() throws Exception {
        // given
        when(userService.getUserByEmail("test@example.com")).thenReturn(userResponse);

        // when & then
        mockMvc.perform(get("/api/users/email/test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(userResponse.getEmail()));

        verify(userService).getUserByEmail("test@example.com");
    }

    @Test
    @DisplayName("사용자 정보 수정 성공")
    void updateUser_Success() throws Exception {
        // given
        when(userService.updateUser(eq(1L), any(UserDto.UpdateRequest.class))).thenReturn(userResponse);

        // when & then
        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userResponse.getId()));

        verify(userService).updateUser(eq(1L), any(UserDto.UpdateRequest.class));
    }

    @Test
    @DisplayName("사용자 정보 수정 실패 - 잘못된 요청 데이터")
    void updateUser_Fail_InvalidRequest() throws Exception {
        // given
        UserDto.UpdateRequest invalidRequest = UserDto.UpdateRequest.builder()
                .nickname("")
                .phoneNumber("invalid-phone")
                .build();

        // when & then
        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).updateUser(any(), any(UserDto.UpdateRequest.class));
    }

    @Test
    @DisplayName("전체 사용자 조회 성공")
    void getAllUsers_Success() throws Exception {
        // given
        when(userService.getAllUsers()).thenReturn(userList);

        // when & then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(userResponse.getId()));

        verify(userService).getAllUsers();
    }

    @Test
    @DisplayName("스터디 그룹별 사용자 조회 성공")
    void getUsersByStudyGroupId_Success() throws Exception {
        // given
        when(userService.getUsersByStudyGroupId(1L)).thenReturn(userList);

        // when & then
        mockMvc.perform(get("/api/users/study-group/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(userResponse.getId()));

        verify(userService).getUsersByStudyGroupId(1L);
    }

    @Test
    @DisplayName("스터디 그룹 리더 조회 성공")
    void getLeaderByStudyGroupId_Success() throws Exception {
        // given
        when(userService.getLeaderByStudyGroupId(1L)).thenReturn(userResponse);

        // when & then
        mockMvc.perform(get("/api/users/study-group/1/leader"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userResponse.getId()));

        verify(userService).getLeaderByStudyGroupId(1L);
    }

    @Test
    @DisplayName("사용자 삭제 성공")
    void deleteUser_Success() throws Exception {
        // given
        doNothing().when(userService).deleteUser(1L);

        // when & then
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(1L);
    }
} 