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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminInvitationServiceImpl implements AdminInvitationService {

    private final AdminInvitationRepository invitationRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.invite.expiration-ms}")
    private Long invitationExpiryMs;

    @Transactional
    public AdminInvitationResponseDto createInvitation(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException();
        }

        invitationRepository.findByEmail(email).ifPresent(invitationRepository::delete);
        AdminInvitationEntity invitation = new AdminInvitationEntity(email, invitationExpiryMs);
        invitationRepository.save(invitation);
        UUID token = invitation.getToken();

        return AdminInvitationResponseDto.builder()
            .email(invitation.getEmail())
            .token(token)
            .expiryDate(invitation.getExpiryDate())
            .build();
    }

    @Transactional
    public void registerAdminByToken(AdminRegister adminRegister) {
        AdminInvitationEntity invitation = invitationRepository.findByToken(adminRegister.getToken())
            .orElseThrow(InvitationNotFoundException::new);

        if (invitation.isUsed()) {
            throw new InvitationAlreadyUsedException();
        }

        if (invitation.isExpired()) {
            throw new InvitationExpiredException();
        }

        UserEntity admin = UserEntity.builder()
            .userName(adminRegister.getUserName())
            .password(passwordEncoder.encode(adminRegister.getPassword()))
            .email(invitation.getEmail())
            .enabled(true)
            .role(UserRole.ADMIN)
            .build();

        userRepository.save(admin);

        invitation.setUsed(true);
        invitationRepository.save(invitation);
    }
}
