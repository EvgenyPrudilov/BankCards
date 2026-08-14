package com.example.bankcards.service.auth;

import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.service.model.LoginRequest;
import com.example.bankcards.service.model.LoginResponse;
import com.example.bankcards.service.model.RefreshResponse;
import com.example.bankcards.service.model.RegisterRequest;

public interface AuthService {
    LoginResponse login(LoginRequest request);

    void logout(String userName);

    String generateRefreshToken(UserEntity userEntity);

    RefreshResponse useRefreshToken(String token);

    void registerNewUser(RegisterRequest request);

}