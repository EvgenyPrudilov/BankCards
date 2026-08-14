package com.example.bankcards.repository;

import com.example.bankcards.entity.AdminInvitationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdminInvitationRepository extends JpaRepository<AdminInvitationEntity, Long> {
    Optional<AdminInvitationEntity> findByToken(UUID token);

    Optional<AdminInvitationEntity> findByEmail(String email);
}
