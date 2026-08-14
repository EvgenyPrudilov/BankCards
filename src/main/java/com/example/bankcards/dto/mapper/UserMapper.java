package com.example.bankcards.dto.mapper;

import com.example.bankcards.dto.auth.*;
import com.example.bankcards.service.model.*;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    LoginRequest toDomain(LoginRequestDto dto);

    RegisterRequest toDomain(RegisterRequestDto dto);

    TokenResponseDto toDto(LoginResponseDto loginResponseDto);

    RefreshResponseDto toDto(RefreshResponse refreshResponse);

    LoginResponseDto toDto(LoginResponse loginResponse);

    AdminRegister toDomain(AdminRegisterDto adminRegisterDto);
}
