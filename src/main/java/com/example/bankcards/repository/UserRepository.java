package com.example.bankcards.repository;

import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.service.model.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUserName(String username);

    Optional<UserEntity> findByUuid(UUID uuid);

    boolean existsByUserName(String username);

    boolean existsByEmail(String email);

    Optional<UserEntity> findByEmail(String email);

    boolean existsByRole(UserRole role);

    @Query("SELECT DISTINCT u FROM UserEntity u " +
        "LEFT JOIN u.cardEntities c " +
        "WHERE (CAST(:userName AS string) IS NULL OR LOWER(u.userName) = LOWER(CAST(:userName AS string))) " +
        "AND (CAST(:holderName AS string) IS NULL OR LOWER(c.holderName) LIKE LOWER(CONCAT('%', CAST(:holderName AS string), '%')))")
    Page<UserEntity> searchByUserNameAndCardHolderName(
        @Param("userName") String userName,
        @Param("holderName") String holderName,
        Pageable pageable
    );
}
