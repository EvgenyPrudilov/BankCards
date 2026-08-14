package com.example.bankcards.repository;

import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.service.model.enums.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<CardEntity, Long> {
    @Query("SELECT c FROM CardEntity c WHERE " +
        "(:userId IS NULL OR c.userEntity.uuid = :userId) AND " +
        "(:status IS NULL OR c.status = :status)")
    Page<CardEntity> findByUserIdAndOptionalStatus(
        @Param("userId") UUID userId,
        @Param("status") CardStatus status,
        Pageable pageable
    );

    Optional<CardEntity> findByUuid(UUID uuid);

    Optional<CardEntity> findByUuidAndUserEntity_Uuid(UUID id, UUID userId);

    boolean existsByUuid(UUID cardId);

    void deleteByUuid(UUID cardId);

    Page<CardEntity> findAllByUuid(UUID uuid, Pageable pageable);

    Page<CardEntity> findAllByUserEntity_Uuid(UUID userUuid, Pageable pageable);
}