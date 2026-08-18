package com.example.bankcards.controller.docs.admin;

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

@Tag(name = "Admin Cards Management", description = "Administrative operations for bank cards management.")
@SecurityRequirement(name = "BearerAuth")
public interface CardControllerDocs {

    @Operation(
        summary = "Create a new bank card.",
        description = "Issues a new bank card based on the provided request details. Requires **ROLE_ADMIN** authority."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Card successfully created.",
            content = @Content(schema = @Schema(implementation = CreateCardResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid payload or validation error occurred.", content = @Content),
        @ApiResponse(responseCode = "401", description = "User is not authenticated.", content = @Content),
        @ApiResponse(responseCode = "403", description = "User does not have the required administrative permissions.", content = @Content)
    })
    ResponseEntity<CreateCardResponseDto> createCard(CreateCardRequestDto requestDto);

    @Operation(
        summary = "Get filtered list of all cards. (YES, it's POST)",
        description = "Returns a paginated list of bank cards matching the search criteria specified in the request body. Requires **ROLE_ADMIN** authority."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Card list successfully retrieved.",
            content = @Content(schema = @Schema(implementation = GetCardsResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid search or pagination parameters provided.", content = @Content),
        @ApiResponse(responseCode = "401", description = "User is not authenticated.", content = @Content),
        @ApiResponse(responseCode = "403", description = "User does not have the required administrative permissions.", content = @Content)
    })
    ResponseEntity<GetCardsResponseDto> getAllCards(
        GetCardsAdminRequestDto requestDto,
        @Parameter(
            description = "Pagination parameters including page, size, and sort.",
            example = "{\"page\": 0, \"size\": 10}",
            schema = @Schema(type = "object")
        ) Pageable pageable
    );

    @Operation(
        summary = "Update card status.",
        description = "Changes the status of a specific bank card (e.g., active, blocked, suspended). Requires **ROLE_ADMIN** authority."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Card status successfully updated."),
        @ApiResponse(responseCode = "400", description = "Invalid status value or missing payload fields.", content = @Content),
        @ApiResponse(responseCode = "401", description = "User is not authenticated.", content = @Content),
        @ApiResponse(responseCode = "403", description = "User does not have the required administrative permissions.", content = @Content),
        @ApiResponse(responseCode = "404", description = "Target bank card not found.", content = @Content)
    })
    ResponseEntity<Void> updateStatus(UpdateCardStatusRequestDto requestDto);

    @Operation(
        summary = "Delete a bank card.",
        description = "Permanently deletes or soft-deletes a specific bank card from the system. Requires **ROLE_ADMIN** authority."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Card successfully deleted."),
        @ApiResponse(responseCode = "400", description = "Invalid target card parameters provided.", content = @Content),
        @ApiResponse(responseCode = "401", description = "User is not authenticated.", content = @Content),
        @ApiResponse(responseCode = "403", description = "User does not have the required administrative permissions.", content = @Content),
        @ApiResponse(responseCode = "404", description = "Target bank card not found.", content = @Content)
    })
    ResponseEntity<Void> deleteCard(DeleteCardRequestDto requestDto);
}
