package com.example.bankcards.service.auth;

import com.example.bankcards.entity.RefreshTokenEntity;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.exception.card.UserAlreadyExistsException;
import com.example.bankcards.exception.security.*;
import com.example.bankcards.repository.RefreshTokenRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.JwtUtils;
import com.example.bankcards.service.model.LoginRequest;
import com.example.bankcards.service.model.LoginResponse;
import com.example.bankcards.service.model.RefreshResponse;
import com.example.bankcards.service.model.RegisterRequest;
import com.example.bankcards.service.model.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthServiceImpl testing.")
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    @Captor
    private ArgumentCaptor<UserEntity> userEntityCaptor;

    @BeforeEach
    void setUp() {
        Long refreshExpirationMs = 600000L;
        ReflectionTestUtils.setField(authService, "refreshExpirationMs", refreshExpirationMs);
    }

    @Nested
    @DisplayName("Registration testing.")
    class RegisterTests {
        private RegisterRequest request;

        @BeforeEach
        void init() {
            request = new RegisterRequest("user", "email@test.com", "pass");
        }

        @Test
        @DisplayName("Successful registration.")
        void success() {

            when(userRepository.existsByUserName(request.getUserName())).thenReturn(false);
            when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
            when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPass");


            assertDoesNotThrow(() -> authService.registerNewUser(request));


            verify(userRepository, times(1)).save(userEntityCaptor.capture());
            UserEntity savedUser = userEntityCaptor.getValue();


            assertNotNull(savedUser);
            assertEquals("user", savedUser.getUserName());
            assertEquals("encodedPass", savedUser.getPassword());
            assertEquals(UserRole.USER, savedUser.getRole());
            assertEquals("email@test.com", savedUser.getEmail());
            assertTrue(savedUser.isEnabled());
        }

        @Test
        @DisplayName("User already exists.")
        void throwsUserAlreadyExistsException() {
            when(userRepository.existsByUserName(request.getUserName())).thenReturn(true);

            assertThrows(UserAlreadyExistsException.class, () -> authService.registerNewUser(request));
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Email is already used.")
        void throwsEmailAlreadyUsedException() {
            when(userRepository.existsByUserName(request.getUserName())).thenReturn(false);
            when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

            assertThrows(EmailAlreadyUsedException.class, () -> authService.registerNewUser(request));
            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Login testing.")
    class LoginTests {
        private LoginRequest request;
        private UserEntity activeUser;

        @Captor
        private ArgumentCaptor<RefreshTokenEntity> refreshTokenCaptor;

        @BeforeEach
        void init() {
            request = new LoginRequest("user", "pass");
            activeUser = UserEntity.builder()
                .id(1L)
                .uuid(UUID.randomUUID())
                .userName("user")
                .password("encodedPass")
                .role(UserRole.USER)
                .enabled(true)
                .build();
        }

        @Test
        @DisplayName("Successful login and access token generation.")
        void success() {

            when(userRepository.findByUserName(request.getUserName())).thenReturn(Optional.of(activeUser));
            when(passwordEncoder.matches(request.getPassword(), activeUser.getPassword())).thenReturn(true);
            when(jwtUtils.generateAccessToken(activeUser.getUuid(), activeUser.getRole())).thenReturn("access-token");
            when(jwtUtils.generateRefreshToken()).thenReturn("refresh-token");


            Instant timeBeforeLogin = Instant.now();


            LoginResponse response = authService.login(request);


            assertNotNull(response);
            assertEquals("access-token", response.getAccessToken());
            assertEquals("refresh-token", response.getRefreshToken());
            assertEquals("user", response.getUserName());


            verify(refreshTokenRepository, times(1)).deleteByUserEntity_Id(activeUser.getId());


            verify(refreshTokenRepository, times(1)).save(refreshTokenCaptor.capture());
            RefreshTokenEntity savedToken = refreshTokenCaptor.getValue();


            assertNotNull(savedToken);
            assertEquals("refresh-token", savedToken.getToken());
            assertEquals(activeUser, savedToken.getUserEntity());
            Instant expectedExpiryDate = timeBeforeLogin.plusMillis(600000L);
            assertTrue(savedToken.getExpiryDate().isAfter(timeBeforeLogin));
            assertTrue(savedToken.getExpiryDate().isBefore(expectedExpiryDate.plusSeconds(5)));
        }

        @Test
        @DisplayName("User is not found.")
        void throwsBadCredentialsWhenUserNotFound() {
            when(userRepository.findByUserName(request.getUserName())).thenReturn(Optional.empty());

            assertThrows(BadCredentialsException.class, () -> authService.login(request));
        }

        @Test
        @DisplayName("User is not enabled.")
        void throwsUserIsNotEnabledException() {
            UserEntity disabledUser = mock(UserEntity.class);
            when(disabledUser.isNotEnabled()).thenReturn(true);
            when(userRepository.findByUserName(request.getUserName())).thenReturn(Optional.of(disabledUser));

            assertThrows(UserIsNotEnabledException.class, () -> authService.login(request));
        }

        @Test
        @DisplayName("Wrong password.")
        void throwsBadCredentialsWhenPasswordWrong() {
            when(userRepository.findByUserName(request.getUserName())).thenReturn(Optional.of(activeUser));
            when(passwordEncoder.matches(request.getPassword(), activeUser.getPassword())).thenReturn(false);

            assertThrows(BadCredentialsException.class, () -> authService.login(request));
        }
    }

    @Nested
    @DisplayName("Logout testing.")
    class LogoutTests {

        @Test
        @DisplayName("Successful logout and token cleaning.")
        void success() {
            UserEntity user = UserEntity.builder().id(5L).userName("user").build();
            when(userRepository.findByUserName("user")).thenReturn(Optional.of(user));

            authService.logout("user");

            verify(refreshTokenRepository, times(1)).deleteByUserEntity_Id(user.getId());
        }

        @Test
        @DisplayName("User is not found.")
        void userNotFoundDoesNothing() {
            when(userRepository.findByUserName("unknown")).thenReturn(Optional.empty());

            authService.logout("unknown");

            verify(refreshTokenRepository, never()).deleteByUserEntity_Id(anyLong());
        }
    }

    @Nested
    @DisplayName("Token updating testing.")
    class RefreshTokenTests {
        @Test
        @DisplayName("Successful updating of a pair of tokens.")
        void success() {
            UserEntity user = UserEntity.builder().id(1L).uuid(UUID.randomUUID()).userName("user").role(UserRole.USER).build();
            RefreshTokenEntity oldToken = mock(RefreshTokenEntity.class);

            when(oldToken.isExpired()).thenReturn(false);
            when(oldToken.getUserEntity()).thenReturn(user);
            when(refreshTokenRepository.findByToken("old-token")).thenReturn(Optional.of(oldToken));
            when(jwtUtils.generateAccessToken(user.getUuid(), user.getRole())).thenReturn("new-access");
            when(jwtUtils.generateRefreshToken()).thenReturn("new-refresh");

            RefreshResponse response = authService.useRefreshToken("old-token");

            assertNotNull(response);
            assertEquals("new-access", response.getAccessToken());
            assertEquals("new-refresh", response.getRefreshToken());
        }

        @Test
        @DisplayName("Refresh token is not found.")
        void throwsRefreshTokenNotFoundException() {
            when(refreshTokenRepository.findByToken("invalid")).thenReturn(Optional.empty());

            assertThrows(RefreshTokenNotFoundException.class, () -> authService.useRefreshToken("invalid"));
        }

        @Test
        @DisplayName("Refresh time is expired.")
        void throwsRefreshTokenExpiredException() {
            UserEntity user = UserEntity.builder().id(1L).build();
            RefreshTokenEntity oldToken = mock(RefreshTokenEntity.class);

            when(oldToken.isExpired()).thenReturn(true);
            when(oldToken.getUserEntity()).thenReturn(user);
            when(refreshTokenRepository.findByToken("expired")).thenReturn(Optional.of(oldToken));

            assertThrows(RefreshTokenExpiredException.class, () -> authService.useRefreshToken("expired"));
            verify(refreshTokenRepository, times(1)).deleteByUserEntity_Id(user.getId());
        }
    }
}