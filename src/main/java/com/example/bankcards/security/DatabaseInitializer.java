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

    @Value("${app.admin.main.name}")
    private String adminUserName;
    @Value("${app.admin.main.password}")
    private String adminPassword;
    @Value("${app.admin.main.email}")
    private String adminEmail;

    @Override
    public void run(String... args) {
        boolean adminExists = userRepository.existsByRole(UserRole.ADMIN);

        if (!adminExists) {
            UserEntity rootAdmin = UserEntity.builder()
                .uuid(UUID.randomUUID())
                .userName(adminUserName)
                .password(passwordEncoder.encode(adminPassword))
                .email(adminEmail)
                .enabled(true)
                .role(UserRole.ADMIN)
                .build();
            userRepository.save(rootAdmin);
        }
    }
}
