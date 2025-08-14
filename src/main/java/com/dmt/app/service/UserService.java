package com.dmt.app.service;

import com.dmt.app.dto.UserDto;
import com.dmt.app.entity.User;
import com.dmt.app.exception.UserException;
import com.dmt.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional
    public UserDto.Response createUser(UserDto.CreateRequest request) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserException.DuplicateEmailException(request.getEmail());
        }
        
        // 닉네임 중복 체크
        if (userRepository.findByNickname(request.getNickname()).isPresent()) {
            throw new UserException.DuplicateNicknameException(request.getNickname());
        }
        
        // 사용자 생성 (BCrypt로 비밀번호 인코딩)
        User user = User.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .role(User.UserRole.USER)
                .build();
        
        User savedUser = userRepository.save(user);
        log.info("새로운 사용자가 생성되었습니다: {}", savedUser.getEmail());
        
        return UserDto.Response.from(savedUser);
    }
    
    public UserDto.Response getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException.UserNotFoundException("사용자 ID: " + userId));
        
        return UserDto.Response.from(user);
    }
    
    public UserDto.Response getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException.UserNotFoundException("이메일: " + email));
        
        return UserDto.Response.from(user);
    }
    
    @Transactional
    public UserDto.Response updateUser(Long userId, UserDto.UpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException.UserNotFoundException("사용자 ID: " + userId));
        
        // 닉네임 변경 시 중복 체크
        if (!user.getNickname().equals(request.getNickname()) && 
            userRepository.findByNickname(request.getNickname()).isPresent()) {
            throw new UserException.DuplicateNicknameException(request.getNickname());
        }
        
        user.setNickname(request.getNickname());
        user.setPhoneNumber(request.getPhoneNumber());
        
        User updatedUser = userRepository.save(user);
        log.info("사용자 정보가 업데이트되었습니다: {}", updatedUser.getEmail());
        
        return UserDto.Response.from(updatedUser);
    }
    
    public List<UserDto.Response> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDto.Response::from)
                .collect(Collectors.toList());
    }
    
    public List<UserDto.Response> getUsersByStudyGroupId(Long groupId) {
        return userRepository.findUsersByStudyGroupId(groupId).stream()
                .map(UserDto.Response::from)
                .collect(Collectors.toList());
    }
    
    public UserDto.Response getLeaderByStudyGroupId(Long groupId) {
        User leader = userRepository.findLeaderByStudyGroupId(groupId)
                .orElseThrow(() -> new UserException.UserNotFoundException("그룹 ID: " + groupId + "의 리더"));
        
        return UserDto.Response.from(leader);
    }
    
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException.UserNotFoundException("사용자 ID: " + userId));
        
        userRepository.delete(user);
        log.info("사용자가 삭제되었습니다: {}", user.getEmail());
    }
    
    // 이 메서드는 더 이상 사용되지 않음 (PasswordEncoder 사용)
    // private String encodePassword(String password) {
    //     return passwordEncoder.encode(password);
    // }
} 