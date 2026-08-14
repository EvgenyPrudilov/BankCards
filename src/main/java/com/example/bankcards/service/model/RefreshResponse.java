package com.example.bankcards.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshResponse {
    private String accessToken;
    private String refreshToken;
    private String username;
}