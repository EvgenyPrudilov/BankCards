package com.example.bankcards.entity;

import com.example.bankcards.service.model.enums.CardStatus;
import com.example.bankcards.util.CardNumberConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "cards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    @Column(nullable = false, unique = true, updatable = false)
    @Builder.Default
    private UUID uuid = UUID.randomUUID();

    @Convert(converter = CardNumberConverter.class)
    @Column(name = "encrypted_number", nullable = false, unique = true)
    private String cardNumber;

    @Column(name = "holder_name", nullable = false, length = 100)
    private String holderName;

    @Column(name = "expiry_date", nullable = false)
    private Instant expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CardStatus status;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;
}