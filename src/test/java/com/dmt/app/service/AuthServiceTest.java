package com.dmt.app.service;

import com.dmt.app.dto.AuthDto;
import com.dmt.app.entity.User;
import com.dmt.app.repository.UserRepository;
import com.dmt.app.security.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private AuthDto.RegisterRequest registerRequest;
    private AuthDto.LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .nickname("테스트유저")
                .password("encodedPassword123")
                .phoneNumber("010-1234-5678")
                .role(User.UserRole.USER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        registerRequest = AuthDto.RegisterRequest.builder()
                .email("new@example.com")
                .nickname("새유저")
                .password("password123")
                .phoneNumber("010-9876-5432")
                .build();

        loginRequest = AuthDto.LoginRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();
    }

    @Test
    @DisplayName("회원가입 성공")
    void register_Success() {
        // given
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(userRepository.findByNickname(registerRequest.getNickname())).thenReturn(null);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // when
        AuthDto.RegisterResponse response = authService.register(registerRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo("회원가입이 완료되었습니다.");
        assertThat(response.getEmail()).isEqualTo(registerRequest.getEmail());
        assertThat(response.getNickname()).isEqualTo(registerRequest.getNickname());

        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(userRepository).findByNickname(registerRequest.getNickname());
        verify(passwordEncoder).encode(registerRequest.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void register_Fail_DuplicateEmail() {
        // given
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("이미 존재하는 이메일입니다.");

        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 닉네임 중복")
    void register_Fail_DuplicateNickname() {
        // given
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(userRepository.findByNickname(registerRequest.getNickname())).thenReturn(Optional.of(testUser));

        // when & then
        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("이미 존재하는 닉네임입니다.");

        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(userRepository).findByNickname(registerRequest.getNickname());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("로그인 성공")
    void login_Success() {
        // given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(testUser.getEmail());
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(jwtTokenUtil.generateToken(anyString(), anyString())).thenReturn("jwtToken123");

        // when
        AuthDto.LoginResponse response = authService.login(loginRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwtToken123");
        assertThat(response.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(response.getNickname()).isEqualTo(testUser.getNickname());
        assertThat(response.getRole()).isEqualTo(testUser.getRole().name());
        assertThat(response.getMessage()).isEqualTo("로그인이 성공했습니다.");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail(testUser.getEmail());
        verify(jwtTokenUtil).generateToken(testUser.getEmail(), testUser.getRole().name());
    }

    @Test
    @DisplayName("로그인 실패 - 인증 실패")
    void login_Fail_AuthenticationFailed() {
        // given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("인증 실패"));

        // when & then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("이메일 또는 비밀번호가 올바르지 않습니다.");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    @DisplayName("로그인 실패 - 사용자 정보를 찾을 수 없음")
    void login_Fail_UserNotFound() {
        // given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(testUser.getEmail());
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("사용자 정보를 찾을 수 없습니다.");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail(testUser.getEmail());
    }
} 