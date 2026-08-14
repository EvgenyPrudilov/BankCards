package com.example.bankcards.exception.security;

import com.example.bankcards.exception.CommonException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public sealed abstract class AuthException
    extends CommonException
    permits BadCredentialsException, EmailAlreadyUsedException, InvitationAlreadyUsedException, InvitationExpiredException, InvitationNotFoundException, NotRolesInAccessTokenException, RefreshTokenExpiredException, RefreshTokenNotFoundException, UserIsNotEnabledException {

    public AuthException(String message, HttpStatus status) {
        super(message, status);
    }

}
