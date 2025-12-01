package com.github.sugayamidori.viaseguraapi.service;

import com.github.sugayamidori.viaseguraapi.controller.dto.LoginRequest;
import com.github.sugayamidori.viaseguraapi.controller.dto.TokenDTO;
import com.github.sugayamidori.viaseguraapi.model.User;
import com.github.sugayamidori.viaseguraapi.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;

    public TokenDTO signIn(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.username(),
                        loginRequest.password()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = (User) authentication.getPrincipal();

        return tokenProvider.createAccessToken(user.getEmail(), user.getRoles());
    }

    public TokenDTO signIn(String username, String refreshToken) {
        var user = userService.findByEmail(username);

        if(user == null) {
            throw new UsernameNotFoundException("User " + username + " not found");
        }

        return tokenProvider.refreshToken(refreshToken);
    }

}
