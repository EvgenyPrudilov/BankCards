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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.jwt.users.refresh-expiration-ms}")
    private Long refreshExpirationMs;

    @Transactional
    public void registerNewUser(RegisterRequest request) {
        String userName = request.getUserName();
        String email = request.getEmail();

        if (userRepository.existsByUserName(userName)) {
            throw new UserAlreadyExistsException();
        }
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyUsedException();
        }

        UserEntity userEntity = UserEntity.builder()
            .userName(request.getUserName())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(UserRole.USER)
            .enabled(true)
            .email(request.getEmail())
            .build();
        userRepository.save(userEntity);
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        String userName = request.getUserName();

        UserEntity userEntity = userRepository.findByUserName(userName)
            .orElseThrow(BadCredentialsException::new);

        if (userEntity.isNotEnabled()) {
            throw new UserIsNotEnabledException();
        }

        if (!passwordEncoder.matches(request.getPassword(), userEntity.getPassword())) {
            throw new BadCredentialsException();
        }

        LoginResponse loginResponse = new LoginResponse(
            jwtUtils.generateAccessToken(userEntity.getUuid(), userEntity.getRole()),
            generateRefreshToken(userEntity),
            userName
        );

        return loginResponse;
    }


    @Transactional
    public void logout(String userName) {
        userRepository.findByUserName(userName).ifPresent(user ->
            refreshTokenRepository.deleteByUserEntity_Id(user.getId())
        );
    }

    @Transactional
    public String generateRefreshToken(UserEntity userEntity) {
        refreshTokenRepository.deleteByUserEntity_Id(userEntity.getId());
        refreshTokenRepository.flush();

        String tokenItself = jwtUtils.generateRefreshToken();
        refreshTokenRepository.save(
            new RefreshTokenEntity(
                tokenItself,
                Instant.now().plusMillis(refreshExpirationMs),
                userEntity
            )
        );

        return tokenItself;
    }

    @Transactional
    public RefreshResponse useRefreshToken(String token) {
        RefreshTokenEntity oldRefreshToken = refreshTokenRepository.findByToken(token)
            .orElseThrow(RefreshTokenNotFoundException::new);

        if (oldRefreshToken.isExpired()) {
            refreshTokenRepository.deleteByUserEntity_Id(oldRefreshToken.getUserEntity().getId());
            throw new RefreshTokenExpiredException();
        }

        UserEntity userEntity = oldRefreshToken.getUserEntity();
        RefreshResponse refreshResponse = new RefreshResponse(
            jwtUtils.generateAccessToken(userEntity.getUuid(), userEntity.getRole()),
            generateRefreshToken(userEntity),
            userEntity.getUserName()
        );

        return refreshResponse;
    }

}