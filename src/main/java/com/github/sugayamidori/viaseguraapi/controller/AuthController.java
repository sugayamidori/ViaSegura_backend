package com.github.sugayamidori.viaseguraapi.controller;

import com.github.sugayamidori.viaseguraapi.controller.docs.AuthControllerDocs;
import com.github.sugayamidori.viaseguraapi.controller.dto.LoginRequest;
import com.github.sugayamidori.viaseguraapi.controller.dto.TokenDTO;
import com.github.sugayamidori.viaseguraapi.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user authentication")
public class AuthController implements AuthControllerDocs {

    private final AuthService service;

    @PostMapping("/login")
    @Override
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        TokenDTO tokenDTO = service.signIn(loginRequest);
        return ResponseEntity.ok(tokenDTO);
    }

    @PutMapping("/refresh/{username}")
    @Override
    public ResponseEntity<?> refresh(@PathVariable("username") String username,
                                     @RequestHeader("Authorization") String refreshToken) {
        TokenDTO tokenDTO = service.signIn(username, refreshToken);
        return ResponseEntity.ok(tokenDTO);
    }
}
