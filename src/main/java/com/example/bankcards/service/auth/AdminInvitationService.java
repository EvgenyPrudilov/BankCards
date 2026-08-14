package com.example.bankcards.service.auth;

import com.example.bankcards.dto.auth.AdminInvitationResponseDto;
import com.example.bankcards.service.model.AdminRegister;

public interface AdminInvitationService {
    AdminInvitationResponseDto createInvitation(String email);

    void registerAdminByToken(AdminRegister adminRegister);
}

