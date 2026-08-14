package com.example.bankcards.security;

import com.example.bankcards.exception.security.NotRolesInAccessTokenException;
import com.example.bankcards.service.model.enums.UserRole;
import io.jsonwebtoken.Jwts;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtUtils {
    private final PrivateKey privateKey;
    @Getter
    private final PublicKey publicKey;
    private final String keyStoreType = "PKCS12";
    @Value("${app.jwt.users.access-key-audience}")
    List<String> usersAccessKeyAudience;
    @Value("${app.jwt.users.expiration-ms}")
    private long usersJwtExpirationMs;
    @Value("${app.jwt.common.key-id}")
    private String jwtKeyId;
    @Value("${app.jwt.common.issuer}")
    private String issuer;

    public JwtUtils(
        @Value("${app.jwt.common.keystore.location}") Resource storeLocation,
        @Value("${app.jwt.common.keystore.password}") String storePassword,
        @Value("${app.jwt.common.keystore.alias}") String keyAlias
    ) {
        try {
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            try (InputStream inputStream = storeLocation.getInputStream()) {
                keyStore.load(inputStream, storePassword.toCharArray());
            }
            this.privateKey = (PrivateKey) keyStore.getKey(keyAlias, storePassword.toCharArray());
            this.publicKey = keyStore.getCertificate(keyAlias).getPublicKey();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка загрузки KeyStore", e);
        }
    }

    public String generateAccessToken(UUID userUuid, UserRole userRole) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + usersJwtExpirationMs);

        return Jwts.builder()
            .header()
            .keyId(jwtKeyId)
            .and()
            .issuer(issuer)
            .audience().add(usersAccessKeyAudience)
            .and()
            .subject(userUuid.toString())
            .claims(Map.of("roles", List.of(userRole.name())))
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(privateKey, Jwts.SIG.ES256)
            .compact();
    }

    public String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }

    public boolean validateAccessToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(this.publicKey)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public UUID getUuidFromToken(String token) {
        return UUID.fromString(
            Jwts.parser()
                .verifyWith(this.publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject()
        );
    }

    public List<UserRole> getRolesFromToken(String token) {
        List<?> roles = Jwts.parser()
            .verifyWith(this.publicKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .get("roles", List.class);

        if (roles != null && !roles.isEmpty()) {
            return roles.stream()
                .map(roleObj -> UserRole.valueOf(roleObj.toString()))
                .collect(Collectors.toList());
        }

        throw new NotRolesInAccessTokenException();
    }
}
