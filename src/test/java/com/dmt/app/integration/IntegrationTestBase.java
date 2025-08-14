package com.dmt.app.integration;

import com.dmt.app.entity.User;
import com.dmt.app.repository.UserRepository;
import com.dmt.app.security.JwtTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@Transactional
public abstract class IntegrationTestBase {

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected JwtTokenUtil jwtTokenUtil;

    @Autowired
    protected ObjectMapper objectMapper;

    protected MockMvc mockMvc;
    protected User testUser;
    protected String testUserToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        setupTestData();
    }

    protected void setupTestData() {
        // 테스트 사용자 생성
        testUser = User.builder()
                .email("test@example.com")
                .nickname("테스트유저")
                .password("$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG") // "password"
                .role(User.UserRole.USER)
                .phoneNumber("010-1234-5678")
                .build();

        testUser = userRepository.save(testUser);

        // JWT 토큰 생성
        testUserToken = jwtTokenUtil.generateToken(testUser.getEmail(), testUser.getRole().name());
    }

    protected String createJsonRequest(Object request) throws Exception {
        return objectMapper.writeValueAsString(request);
    }

    protected MediaType getJsonMediaType() {
        return MediaType.APPLICATION_JSON;
    }
} 