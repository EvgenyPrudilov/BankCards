package com.example.bankcards.controller;

import com.example.bankcards.controller.docs.PrivateControllerDocs;
import com.example.bankcards.dto.auth.AdminInvitationResponseDto;
import com.example.bankcards.dto.auth.AdminInviteRequestDto;
import com.example.bankcards.service.ServicesGate;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/private/auth")
public class AuthPrivateController implements PrivateControllerDocs {
    private final ServicesGate servicesGate;

    @Value("${app.refresh-token.path}")
    private String refreshPath;

    @Override
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
        @Parameter(hidden = true) @AuthenticationPrincipal String userName
    ) {
        servicesGate.logout(userName);
        ResponseCookie clearRefreshCookie = ResponseCookie.from("refreshToken", "")
            .httpOnly(true)
            .secure(true)
            .path(refreshPath)
            .maxAge(0)
            .sameSite("Strict")
            .build();
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, clearRefreshCookie.toString())
            .build();
    }

    @Override
    @PostMapping("/admin/invitations")
    public ResponseEntity<AdminInvitationResponseDto> inviteNewAdmin(
        @Valid @RequestBody AdminInviteRequestDto requestDto
    ) {
        AdminInvitationResponseDto responseDto = servicesGate.createInvitation(requestDto.getEmail());
        return ResponseEntity.ok(responseDto);
    }
}
