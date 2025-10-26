package com.github.sugayamidori.viaseguraapi.controller.docs;

import com.github.sugayamidori.viaseguraapi.controller.dto.SalvarUsuarioDTO;
import com.github.sugayamidori.viaseguraapi.controller.dto.UsuarioDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;

public interface UsuarioControllerDocs {

    @Operation(summary = "Register a new user",
            description = "Endpoint to register a new user in the system",
            tags = "Users",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User created successfully", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
                    @ApiResponse(responseCode = "409", description = "User already exists", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            })
    ResponseEntity<Void> salvar(@RequestBody @Valid SalvarUsuarioDTO dto);

    @Operation(summary = "Register a new user by admin",
            description = "Endpoint for admin to register a new user in the system",
            tags = "Users",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User created successfully", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
                    @ApiResponse(responseCode = "409", description = "User already exists", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            })
    ResponseEntity<Void> salvar(@RequestBody @Valid UsuarioDTO dto);

    @Operation(summary = "Get current user details",
            description = "Endpoint to retrieve details of the currently authenticated user",
            tags = "Users",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User details retrieved successfully",
                            content = @Content(schema = @Schema(implementation = UsuarioDTO.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            })
    ResponseEntity<UsuarioDTO> obterDetalhes(Authentication authentication);

    @Operation(summary = "Update current user details",
            description = "Endpoint to update details of the currently authenticated user",
            tags = "Users",
            responses = {
                    @ApiResponse(responseCode = "204", description = "User updated successfully", content = @Content),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            })
    ResponseEntity<Object> atualizar(@RequestBody SalvarUsuarioDTO dto,
                                     Authentication authentication);

    @Operation(summary = "Delete current user",
            description = "Endpoint to delete the currently authenticated user",
            tags = "Users",
            responses = {
                    @ApiResponse(responseCode = "204", description = "User deleted successfully", content = @Content),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            })
    ResponseEntity<Object> deletar(Authentication authentication);
}
