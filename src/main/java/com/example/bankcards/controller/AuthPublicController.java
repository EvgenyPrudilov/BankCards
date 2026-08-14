package com.example.bankcards.controller;

import com.example.bankcards.controller.docs.PublicControllerDocs;
import com.example.bankcards.dto.auth.*;
import com.example.bankcards.service.ServicesGate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public/auth")
public class AuthPublicController implements PublicControllerDocs {
    private final ServicesGate servicesGate;

    @Value("${app.refresh-token.path}")
    private String refreshPath;
    @Value("${app.refresh-token.max-age-s}")
    private Long refreshMaxAge;

    @Override
    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(
        @Valid @RequestBody RegisterRequestDto request
    ) {
        servicesGate.registerNewUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(
        @Valid @RequestBody LoginRequestDto requestDto
    ) {
        LoginResponseDto loginResponseDto = servicesGate.login(requestDto);
        ResponseCookie refreshCookie = createRefreshCookie(loginResponseDto.getRefreshToken());
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
            .body(new TokenResponseDto(
                loginResponseDto.getAccessToken(),
                loginResponseDto.getUsername())
            );
    }

    @Override
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refresh(
        @CookieValue(name = "refreshToken") String refreshToken
    ) {
        RefreshResponseDto refreshResponseDto = servicesGate.useRefreshToken(refreshToken);
        ResponseCookie refreshCookie = createRefreshCookie(refreshResponseDto.getRefreshToken());
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
            .body(new TokenResponseDto(
                refreshResponseDto.getAccessToken(),
                refreshResponseDto.getUsername())
            );
    }

    private ResponseCookie createRefreshCookie(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
            .httpOnly(true)
            .secure(true)
            .path(refreshPath)
            .maxAge(refreshMaxAge)
            .sameSite("Strict")
            .build();
    }

    @Override
    @PostMapping("/admin/register")
    public ResponseEntity<Void> completeAdminRegistration(
        @Valid @RequestBody AdminRegisterDto registerDto
    ) {
        servicesGate.registerAdminByToken(registerDto);
        return ResponseEntity.ok().build();
    }
}
