package com.example.bankcards.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    description = "Response payload containing the generated authentication access token upon successful authentication."
)
public class TokenResponseDto {

    @Schema(
        description = "The JWT access token used to authenticate subsequent protected resource requests.",
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    private String accessToken;

    @Schema(
        description = "The unique username of the authenticated account holder.",
        example = "bank_customer_7"
    )
    private String username;
}
