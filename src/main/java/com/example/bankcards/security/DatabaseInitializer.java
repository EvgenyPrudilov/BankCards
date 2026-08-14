package com.example.bankcards.security;

import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.model.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${INIT_ADMIN_USERNAME}")
    private String adminUsername;
    @Value("${INIT_ADMIN_PASSWORD}")
    private String adminPassword;
    @Value("${INIT_ADMIN_EMAIL}")
    private String adminEmail;

    @Override
    public void run(String... args) {
        boolean adminExists = userRepository.existsByRole(UserRole.ROLE_ADMIN);

        if (!adminExists) {
            UserEntity rootAdmin = UserEntity.builder()
                .uuid(UUID.randomUUID())
                .username(adminUsername)
                .password(passwordEncoder.encode(adminPassword))
                .email(adminEmail)
                .enabled(true)
                .role(UserRole.ROLE_ADMIN)
                .build();
            userRepository.save(rootAdmin);
        }
    }
}
