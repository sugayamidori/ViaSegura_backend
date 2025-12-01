package com.github.sugayamidori.viaseguraapi.controller.docs;

import com.github.sugayamidori.viaseguraapi.controller.dto.ErrorResponse;
import com.github.sugayamidori.viaseguraapi.controller.dto.LoginRequest;
import com.github.sugayamidori.viaseguraapi.controller.dto.TokenDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface AuthControllerDocs {
    @Operation(summary = "User login",
            description = "Endpoint for user authentication and token generation",
            tags = "Authentication",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login successful",
                            content = @Content(
                                    schema = @Schema(implementation = TokenDTO.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class)
                            ))
            })
    ResponseEntity<?> login(@RequestBody LoginRequest loginRequest);

    @Operation(summary = "Refresh token",
            description = "Endpoint to refresh authentication tokens",
            tags = "Authentication",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Token refreshed successfully",
                            content = @Content(
                                    schema = @Schema(implementation = TokenDTO.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class)
                            ))
            })
    ResponseEntity<?> refresh(@PathVariable("username") String username,
                              @RequestHeader("Authorization") String refreshToken);
}
