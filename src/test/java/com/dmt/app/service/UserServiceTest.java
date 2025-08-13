package com.dmt.app.service;

import com.dmt.app.dto.UserDto;
import com.dmt.app.entity.User;
import com.dmt.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserService userService;
    
    private User testUser;
    private UserDto.CreateRequest createRequest;
    private UserDto.UpdateRequest updateRequest;
    
    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .nickname("테스트유저")
                .password("password123")
                .phoneNumber("010-1234-5678")
                .role(User.UserRole.USER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        createRequest = UserDto.CreateRequest.builder()
                .email("new@example.com")
                .nickname("새유저")
                .password("password123")
                .phoneNumber("010-9876-5432")
                .build();
        
        updateRequest = UserDto.UpdateRequest.builder()
                .nickname("수정된닉네임")
                .phoneNumber("010-1111-2222")
                .build();
    }
    
    @Test
    @DisplayName("사용자 생성 성공")
    void createUser_Success() {
        // given
        when(userRepository.existsByEmail(createRequest.getEmail())).thenReturn(false);
        when(userRepository.findByNickname(createRequest.getNickname())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // when
        UserDto.Response response = userService.createUser(createRequest);
        
        // then
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(response.getNickname()).isEqualTo(testUser.getNickname());
        
        verify(userRepository).existsByEmail(createRequest.getEmail());
        verify(userRepository).findByNickname(createRequest.getNickname());
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    @DisplayName("사용자 생성 실패 - 이메일 중복")
    void createUser_Fail_DuplicateEmail() {
        // given
        when(userRepository.existsByEmail(createRequest.getEmail())).thenReturn(true);
        
        // when & then
        assertThatThrownBy(() -> userService.createUser(createRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 이메일입니다.");
        
        verify(userRepository).existsByEmail(createRequest.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    @DisplayName("사용자 생성 실패 - 닉네임 중복")
    void createUser_Fail_DuplicateNickname() {
        // given
        when(userRepository.existsByEmail(createRequest.getEmail())).thenReturn(false);
        when(userRepository.findByNickname(createRequest.getNickname())).thenReturn(Optional.of(testUser));
        
        // when & then
        assertThatThrownBy(() -> userService.createUser(createRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 닉네임입니다.");
        
        verify(userRepository).existsByEmail(createRequest.getEmail());
        verify(userRepository).findByNickname(createRequest.getNickname());
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    @DisplayName("사용자 조회 성공")
    void getUserById_Success() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        
        // when
        UserDto.Response response = userService.getUserById(1L);
        
        // then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(testUser.getId());
        assertThat(response.getEmail()).isEqualTo(testUser.getEmail());
        
        verify(userRepository).findById(1L);
    }
    
    @Test
    @DisplayName("사용자 조회 실패 - 존재하지 않는 사용자")
    void getUserById_Fail_UserNotFound() {
        // given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        
        // when & then
        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
        
        verify(userRepository).findById(999L);
    }
    
    @Test
    @DisplayName("사용자 정보 수정 성공")
    void updateUser_Success() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByNickname(updateRequest.getNickname())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // when
        UserDto.Response response = userService.updateUser(1L, updateRequest);
        
        // then
        assertThat(response).isNotNull();
        verify(userRepository).findById(1L);
        verify(userRepository).findByNickname(updateRequest.getNickname());
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    @DisplayName("전체 사용자 조회 성공")
    void getAllUsers_Success() {
        // given
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);
        
        // when
        List<UserDto.Response> responses = userService.getAllUsers();
        
        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getEmail()).isEqualTo(testUser.getEmail());
        
        verify(userRepository).findAll();
    }
    
    @Test
    @DisplayName("사용자 삭제 성공")
    void deleteUser_Success() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        
        // when
        userService.deleteUser(1L);
        
        // then
        verify(userRepository).findById(1L);
        verify(userRepository).delete(testUser);
    }
} 