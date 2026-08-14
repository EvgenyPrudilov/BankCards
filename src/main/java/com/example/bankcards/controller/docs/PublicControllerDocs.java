package com.example.bankcards.controller.docs;

import com.example.bankcards.dto.auth.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Public Authentication Management (no token needed)", description = "Public operations for user registration, authentication, and token refreshing.")
public interface PublicControllerDocs {

    @Operation(
        summary = "Register a new user.",
        description = "Creates a new client account in the system with the provided credentials. This endpoint is public."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User successfully registered."),
        @ApiResponse(responseCode = "400", description = "Invalid registration data or username already exists.", content = @Content)
    })
    ResponseEntity<Void> registerUser(RegisterRequestDto request);

    @Operation(
        summary = "Authenticate user and login.",
        description = "Verifies user credentials. Returns an access token in the response body and sets a secure HttpOnly refresh token cookie. This endpoint is public."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Authentication successful. Tokens generated.",
            headers = @Header(name = "Set-Cookie", description = "Contains the HttpOnly 'refreshToken' cookie.", schema = @Schema(type = "string")),
            content = @Content(schema = @Schema(implementation = TokenResponseDto.class))
        ),
        @ApiResponse(responseCode = "400", description = "Missing or invalid request payload fields.", content = @Content),
        @ApiResponse(responseCode = "401", description = "Bad credentials or account is disabled.", content = @Content)
    })
    ResponseEntity<TokenResponseDto> login(LoginRequestDto requestDto);

    @Operation(
        summary = "Refresh access token.",
        description = "Validates the provided HttpOnly refresh token cookie. Issues a new access token and updates the refresh token cookie. This endpoint is public."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Tokens successfully refreshed.",
            headers = @Header(name = "Set-Cookie", description = "Contains the updated HttpOnly 'refreshToken' cookie.", schema = @Schema(type = "string")),
            content = @Content(schema = @Schema(implementation = TokenResponseDto.class))
        ),
        @ApiResponse(responseCode = "401", description = "Refresh token is expired, invalid, or missing.", content = @Content)
    })
    ResponseEntity<TokenResponseDto> refresh(
        @Parameter(
            name = "refreshToken",
            in = ParameterIn.COOKIE,
            description = "The secure HttpOnly refresh token required to get a new access token.",
            required = true,
            schema = @Schema(type = "string")
        ) String refreshToken
    );

    @Operation(
        summary = "Complete administrative registration.",
        description = "Registers an administrative user account using a special sign-up token or pre-authorized invitation payload. This endpoint is public."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Administrative user successfully registered."),
        @ApiResponse(responseCode = "400", description = "Invalid administrative registration payload.", content = @Content),
        @ApiResponse(responseCode = "403", description = "The registration token or signature is invalid.", content = @Content)
    })
    ResponseEntity<Void> completeAdminRegistration(AdminRegisterDto registerDto);
}
