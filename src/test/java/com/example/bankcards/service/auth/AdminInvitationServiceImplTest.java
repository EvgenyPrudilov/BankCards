package com.example.bankcards.service.auth;

import com.example.bankcards.dto.auth.AdminInvitationResponseDto;
import com.example.bankcards.entity.AdminInvitationEntity;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.exception.card.UserAlreadyExistsException;
import com.example.bankcards.exception.security.InvitationAlreadyUsedException;
import com.example.bankcards.exception.security.InvitationExpiredException;
import com.example.bankcards.exception.security.InvitationNotFoundException;
import com.example.bankcards.repository.AdminInvitationRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.model.AdminRegister;
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
@DisplayName("AdminInvitationServiceImpl testing.")
class AdminInvitationServiceImplTest {

    @Mock
    private AdminInvitationRepository invitationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminInvitationServiceImpl adminInvitationService;

//    private static final String BASE_URL = "http://localhost:80/api/v1/public/auth/admin/register";
    private static final Long EXPIRY_MS = 900000L;

    @BeforeEach
    void setUp() {
        // Внедряем конфигурационные значения @Value
//        ReflectionTestUtils.setField(adminInvitationService, "baseUrl", BASE_URL);
        ReflectionTestUtils.setField(adminInvitationService, "invitationExpiryMs", EXPIRY_MS);
    }

    @Nested
    @DisplayName("Invitation creating testing.")
    class CreateInvitationTests {
        private final String testEmail = "admin@test.com";

        @Captor
        private ArgumentCaptor<AdminInvitationEntity> invitationCaptor;

        @Test
        @DisplayName("Successful invite creation.")
        void success_WithExistingOldInvitationDeleted() {
            // Given
            when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());

            // Симулируем, что старый инвайт существовал и должен быть удален
            AdminInvitationEntity oldInvitation = mock(AdminInvitationEntity.class);
            when(invitationRepository.findByEmail(testEmail)).thenReturn(Optional.of(oldInvitation));

            // When
            AdminInvitationResponseDto response = adminInvitationService.createInvitation(testEmail);

            // Then
            assertNotNull(response);
            assertEquals(testEmail, response.getEmail());
            assertNotNull(response.getToken());
            assertNotNull(response.getExpiryDate());

            // Проверяем, что старый инвайт был удален перед сохранением нового
            verify(invitationRepository, times(1)).delete(oldInvitation);

            // Строгая проверка сохраняемого инвайта через ArgumentCaptor
            verify(invitationRepository, times(1)).save(invitationCaptor.capture());
            AdminInvitationEntity savedInvitation = invitationCaptor.getValue();

            assertEquals(testEmail, savedInvitation.getEmail());
            assertEquals(response.getToken(), savedInvitation.getToken());
        }

        @Test
        @DisplayName("Email is already used.")
        void throwsUserAlreadyExistsException() {
            // Given
            when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(new UserEntity()));

            // When & Then
            assertThrows(UserAlreadyExistsException.class, () -> adminInvitationService.createInvitation(testEmail));

            verify(invitationRepository, never()).delete(any());
            verify(invitationRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Invitation using.")
    class RegisterAdminByTokenTests {
        private AdminRegister registerRequest;
        private UUID testToken;

        @Captor
        private ArgumentCaptor<UserEntity> userCaptor;

        @BeforeEach
        void init() {
            testToken = UUID.randomUUID();
            registerRequest = new AdminRegister("newAdmin", "adminPass", testToken);
        }

        @Test
        @DisplayName("Successful new admin registration without spy")
        void success() {
            // 1. Given (Предусловия)
            // Создаем реальный объект инвайта.
            // Передаем большой EXPIRY_MS (например, 24 часа), чтобы expiryDate гарантированно была в будущем,
            // и метод invitation.isExpired() вернул false сам по себе.
            AdminInvitationEntity invitation = new AdminInvitationEntity("admin@test.com", EXPIRY_MS);

            // Проставляем токен и убеждаемся, что статус used = false
            ReflectionTestUtils.setField(invitation, "token", testToken);
            ReflectionTestUtils.setField(invitation, "used", false);

            // Задаем стандартное поведение заглушек
            when(invitationRepository.findByToken(testToken)).thenReturn(Optional.of(invitation));
            when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedAdminPass");

            // 2. When (Действие)
            assertDoesNotThrow(() -> adminInvitationService.registerAdminByToken(registerRequest));

            // 3. Then (Проверки пользователя)
            verify(userRepository, times(1)).save(userCaptor.capture());
            UserEntity savedAdmin = userCaptor.getValue();

            assertNotNull(savedAdmin);
            assertEquals("newAdmin", savedAdmin.getUsername());
            assertEquals("encodedAdminPass", savedAdmin.getPassword());
            assertEquals("admin@test.com", savedAdmin.getEmail());
            assertEquals(UserRole.ROLE_ADMIN, savedAdmin.getRole());
            assertTrue(savedAdmin.isEnabled());

            // 4. Then (Проверки изменения статуса инвайта)
            // Проверяем, что оригинальный объект действительно изменил свое состояние
            assertTrue(invitation.isUsed());
            verify(invitationRepository, times(1)).save(invitation);
        }


        @Test
        @DisplayName("Ошибка: Токен инвайта не найден в базе данных")
        void throwsInvitationNotFoundException() {
            // Given
            when(invitationRepository.findByToken(testToken)).thenReturn(Optional.empty());

            // When & Then
            assertThrows(InvitationNotFoundException.class, () -> adminInvitationService.registerAdminByToken(registerRequest));
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Ошибка: Инвайт уже был использован ранее")
        void throwsInvitationAlreadyUsedException() {
            // Given
            AdminInvitationEntity mockInvitation = mock(AdminInvitationEntity.class);
            when(mockInvitation.isUsed()).thenReturn(true);
            when(invitationRepository.findByToken(testToken)).thenReturn(Optional.of(mockInvitation));

            // When & Then
            assertThrows(InvitationAlreadyUsedException.class, () -> adminInvitationService.registerAdminByToken(registerRequest));
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Ошибка: Срок действия инвайта истек")
        void throwsInvitationExpiredException() {
            // Given
            AdminInvitationEntity mockInvitation = mock(AdminInvitationEntity.class);
            when(mockInvitation.isUsed()).thenReturn(false);
            when(mockInvitation.isExpired()).thenReturn(true);
            when(invitationRepository.findByToken(testToken)).thenReturn(Optional.of(mockInvitation));

            // When & Then
            assertThrows(InvitationExpiredException.class, () -> adminInvitationService.registerAdminByToken(registerRequest));
            verify(userRepository, never()).save(any());
        }
    }
}
