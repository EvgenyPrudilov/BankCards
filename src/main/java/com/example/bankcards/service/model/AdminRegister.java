package com.example.bankcards.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class AdminRegister {
    private String username;
    private String password;
    private UUID token;
}
