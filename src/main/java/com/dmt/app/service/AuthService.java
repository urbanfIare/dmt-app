package com.dmt.app.service;

import com.dmt.app.dto.AuthDto;
import com.dmt.app.entity.User;
import com.dmt.app.repository.UserRepository;
import com.dmt.app.security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthDto.RegisterResponse register(AuthDto.RegisterRequest request) {
        // 이메일 중복 확인
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        // 닉네임 중복 확인
        if (userRepository.findByNickname(request.getNickname()).isPresent()) {
            throw new RuntimeException("이미 존재하는 닉네임입니다.");
        }

        // 사용자 생성
        User user = User.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .role(User.UserRole.USER)
                .build();

        userRepository.save(user);
        log.info("새 사용자 등록: {}", user.getEmail());

        return AuthDto.RegisterResponse.builder()
                .message("회원가입이 완료되었습니다.")
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
    }

    public AuthDto.LoginResponse login(AuthDto.LoginRequest request) {
        try {
            // 인증 처리
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));

            // JWT 토큰 생성
            String token = jwtTokenUtil.generateToken(userDetails.getUsername(), user.getRole().name());

            log.info("사용자 로그인 성공: {}", user.getEmail());

            return AuthDto.LoginResponse.builder()
                    .token(token)
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .role(user.getRole().name())
                    .message("로그인이 성공했습니다.")
                    .build();

        } catch (Exception e) {
            log.error("로그인 실패: {}", e.getMessage());
            throw new RuntimeException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }
    }
} 