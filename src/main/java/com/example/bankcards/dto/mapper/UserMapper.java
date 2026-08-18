package com.example.bankcards.dto.mapper;

import com.example.bankcards.dto.auth.*;
import com.example.bankcards.dto.user.GetUserResponseDto;
import com.example.bankcards.dto.user.GetUsersRequestDto;
import com.example.bankcards.dto.user.GetUsersResponseDto;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.service.model.*;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    LoginRequest toDomain(LoginRequestDto dto);

    RegisterRequest toDomain(RegisterRequestDto dto);

    TokenResponseDto toDto(LoginResponseDto loginResponseDto);

    RefreshResponseDto toDto(RefreshResponse refreshResponse);

    LoginResponseDto toDto(LoginResponse loginResponse);

    AdminRegister toDomain(AdminRegisterDto adminRegisterDto);

    default GetUsersResponseDto toDto(Page<UserEntity> page) {
        if (page == null) {
            return null;
        }
        GetUsersResponseDto response = new GetUsersResponseDto();
        response.setUsers(toUserResponseDtoList(page.getContent()));
        return response;
    }

    List<GetUserResponseDto> toUserResponseDtoList(List<UserEntity> content);

    GetUsersRequest toDomain(GetUsersRequestDto requestDto);
}
