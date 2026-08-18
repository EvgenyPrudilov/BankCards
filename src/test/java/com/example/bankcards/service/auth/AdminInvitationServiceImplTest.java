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

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminInvitationServiceImpl testing.")
class AdminInvitationServiceImplTest {

    private static final Long EXPIRY_MS = 900000L;
    @Mock
    private AdminInvitationRepository invitationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private AdminInvitationServiceImpl adminInvitationService;

    @BeforeEach
    void setUp() {


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

            when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());


            AdminInvitationEntity oldInvitation = mock(AdminInvitationEntity.class);
            when(invitationRepository.findByEmail(testEmail)).thenReturn(Optional.of(oldInvitation));


            AdminInvitationResponseDto response = adminInvitationService.createInvitation(testEmail);


            assertNotNull(response);
            assertEquals(testEmail, response.getEmail());
            assertNotNull(response.getToken());
            assertNotNull(response.getExpiryDate());


            verify(invitationRepository, times(1)).delete(oldInvitation);


            verify(invitationRepository, times(1)).save(invitationCaptor.capture());
            AdminInvitationEntity savedInvitation = invitationCaptor.getValue();

            assertEquals(testEmail, savedInvitation.getEmail());
            assertEquals(response.getToken(), savedInvitation.getToken());
        }

        @Test
        @DisplayName("Email is already used.")
        void throwsUserAlreadyExistsException() {

            when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(new UserEntity()));


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

            AdminInvitationEntity invitation = new AdminInvitationEntity("admin@test.com", EXPIRY_MS);


            ReflectionTestUtils.setField(invitation, "token", testToken);
            ReflectionTestUtils.setField(invitation, "used", false);


            when(invitationRepository.findByToken(testToken)).thenReturn(Optional.of(invitation));
            when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedAdminPass");


            assertDoesNotThrow(() -> adminInvitationService.registerAdminByToken(registerRequest));


            verify(userRepository, times(1)).save(userCaptor.capture());
            UserEntity savedAdmin = userCaptor.getValue();

            assertNotNull(savedAdmin);
            assertEquals("newAdmin", savedAdmin.getUserName());
            assertEquals("encodedAdminPass", savedAdmin.getPassword());
            assertEquals("admin@test.com", savedAdmin.getEmail());
            assertEquals(UserRole.ADMIN, savedAdmin.getRole());
            assertTrue(savedAdmin.isEnabled());
            assertTrue(invitation.isUsed());
            verify(invitationRepository, times(1)).save(invitation);
        }


        @Test
        @DisplayName("Ошибка: Токен инвайта не найден в базе данных")
        void throwsInvitationNotFoundException() {

            when(invitationRepository.findByToken(testToken)).thenReturn(Optional.empty());


            assertThrows(InvitationNotFoundException.class, () -> adminInvitationService.registerAdminByToken(registerRequest));
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Ошибка: Инвайт уже был использован ранее")
        void throwsInvitationAlreadyUsedException() {

            AdminInvitationEntity mockInvitation = mock(AdminInvitationEntity.class);
            when(mockInvitation.isUsed()).thenReturn(true);
            when(invitationRepository.findByToken(testToken)).thenReturn(Optional.of(mockInvitation));


            assertThrows(InvitationAlreadyUsedException.class, () -> adminInvitationService.registerAdminByToken(registerRequest));
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Ошибка: Срок действия инвайта истек")
        void throwsInvitationExpiredException() {

            AdminInvitationEntity mockInvitation = mock(AdminInvitationEntity.class);
            when(mockInvitation.isUsed()).thenReturn(false);
            when(mockInvitation.isExpired()).thenReturn(true);
            when(invitationRepository.findByToken(testToken)).thenReturn(Optional.of(mockInvitation));


            assertThrows(InvitationExpiredException.class, () -> adminInvitationService.registerAdminByToken(registerRequest));
            verify(userRepository, never()).save(any());
        }
    }
}
