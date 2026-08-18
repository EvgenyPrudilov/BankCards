package com.example.bankcards.controller.docs.user;

import com.example.bankcards.dto.card.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "User Cards Management", description = "Operations related to user bank cards management.")
@SecurityRequirement(name = "BearerAuth")
public interface CardControllerDocs {

    @Operation(
        summary = "Get user cards list. (YES, it's POST)",
        description = "Returns a paginated list of cards belonging to the currently authenticated user. The user identifier is extracted from the JWT token. Requires **ROLE_USER** authority."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Card list successfully retrieved.",
            content = @Content(schema = @Schema(implementation = GetCardsResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters provided.", content = @Content),
        @ApiResponse(responseCode = "401", description = "User is not authenticated.", content = @Content),
        @ApiResponse(responseCode = "403", description = "User does not have the required permissions.", content = @Content)
    })
    ResponseEntity<GetCardsResponseDto> getMyCards(
        UUID uuid,
        GetCardsRequestDto requestDto,
        @Parameter(
            description = "Pagination parameters including page, size, and sort.",
            example = "{\"page\": 0, \"size\": 10}",
            schema = @Schema(type = "object")
        ) Pageable pageable
    );

    @Operation(
        summary = "Get card balance. (YES, it's POST)",
        description = "Returns the current balance and currency for the specified card belonging to the user. Requires **ROLE_USER** authority and ownership of the card."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Card balance successfully retrieved.",
            content = @Content(schema = @Schema(implementation = GetCardBalanceResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid card ID format or empty request body.", content = @Content),
        @ApiResponse(responseCode = "401", description = "User is not authenticated.", content = @Content),
        @ApiResponse(responseCode = "403", description = "User does not have the required permissions.", content = @Content),
        @ApiResponse(responseCode = "404", description = "Card not found or does not belong to the user.", content = @Content)
    })
    ResponseEntity<GetCardBalanceResponseDto> getBalance(UUID uuid, GetCardBalanceRequestDto requestDto);

    @Operation(
        summary = "Block card.",
        description = "Submits a request to block the specified card of the current user. Once blocked, transactions on this card will be unavailable. Requires **ROLE_USER** authority and ownership of the card."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Card successfully blocked."),
        @ApiResponse(responseCode = "400", description = "Data validation error occurred.", content = @Content),
        @ApiResponse(responseCode = "401", description = "User is not authenticated.", content = @Content),
        @ApiResponse(responseCode = "403", description = "User does not have the required permissions.", content = @Content),
        @ApiResponse(responseCode = "404", description = "Card not found.", content = @Content)
    })
    ResponseEntity<Void> blockCard(UUID uuid, BlockCardRequestDto requestDto);

    @Operation(
        summary = "Transfer between own cards.",
        description = "Executes a fund transfer from one card account of the current user to another card account of the same user. Requires **ROLE_USER** authority and ownership of both cards."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transfer successfully completed."),
        @ApiResponse(responseCode = "400", description = "Insufficient funds or invalid transfer parameters.", content = @Content),
        @ApiResponse(responseCode = "401", description = "User is not authenticated.", content = @Content),
        @ApiResponse(responseCode = "403", description = "User does not have the required permissions.", content = @Content),
        @ApiResponse(responseCode = "404", description = "One or both cards not found.", content = @Content)
    })
    ResponseEntity<Void> transferBetweenOwnCards(UUID uuid, TransferRequestDto requestDto);
}
