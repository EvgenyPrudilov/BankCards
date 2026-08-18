package com.example.bankcards.controller.docs.admin;

import com.example.bankcards.dto.user.GetUsersRequestDto;
import com.example.bankcards.dto.user.GetUsersResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Admin Users Management", description = "Administrative operations for system users management.")
@SecurityRequirement(name = "BearerAuth")
public interface UserControllerDocs {

    @Operation(
        summary = "Search users with pagination. (YES, it's POST)",
        description = "Returns a paginated list of users matching the search criteria (exact match for username, partial match for card holder name). Requires **ROLE_ADMIN** authority."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User list successfully retrieved.",
            content = @Content(schema = @Schema(implementation = GetUsersResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid search or pagination parameters provided.", content = @Content),
        @ApiResponse(responseCode = "401", description = "User is not authenticated.", content = @Content),
        @ApiResponse(responseCode = "403", description = "User does not have the required administrative permissions.", content = @Content)
    })
    @PostMapping("/search")
    ResponseEntity<GetUsersResponseDto> searchUsers(
        @Valid @RequestBody GetUsersRequestDto requestDto,
        @Parameter(
            description = "Pagination parameters including page, size, and sort.",
            example = "{\"page\": 0, \"size\": 10}",
            schema = @Schema(type = "object")
        ) @PageableDefault(size = 10) Pageable pageable
    );
}