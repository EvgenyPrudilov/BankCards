package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Entity
@NoArgsConstructor
@Table(name = "refresh_tokens")
public class RefreshTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Instant expiryDate;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity userEntity;

    public RefreshTokenEntity(String token, Instant expiryDate, UserEntity userEntity) {
        this.token = token;
        this.expiryDate = expiryDate;
        this.userEntity = userEntity;
    }

    public boolean isExpired() {
        return this.expiryDate.isBefore(Instant.now());
    }
}