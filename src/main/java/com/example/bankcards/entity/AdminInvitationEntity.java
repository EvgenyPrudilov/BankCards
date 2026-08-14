package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@Table(name = "admin_invitations")
public class AdminInvitationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, columnDefinition = "UUID")
    private UUID token = UUID.randomUUID();

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private Instant expiryDate;

    @Column(nullable = false)
    private boolean used = false;

    public AdminInvitationEntity(String email, Long expirationMs) {
        this.email = email;
        this.expiryDate = Instant.now().plusMillis(expirationMs);
    }

    public boolean isExpired() {
        return Instant.now().isAfter(this.expiryDate);
    }

    public boolean isNotExpired() {
        return !isExpired();
    }
}
