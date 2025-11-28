package com.github.sugayamidori.viaseguraapi.service;

import com.github.sugayamidori.viaseguraapi.controller.dto.LoginRequest;
import com.github.sugayamidori.viaseguraapi.controller.dto.TokenDTO;
import com.github.sugayamidori.viaseguraapi.model.User;
import com.github.sugayamidori.viaseguraapi.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void init() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void mustAuthenticateUserANDReturnTokenWhenLogin() {
        String email = "user@exemplo.com";
        String password = "password";
        LoginRequest request = new LoginRequest(email, password);
        User user = createUser(email, "USER");
        Authentication authentication = mock(Authentication.class);
        TokenDTO expectedToken = new TokenDTO(email, true, new Date(), null, "access-token", "refresh");

        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(tokenProvider.createAccessToken(user.getEmail(), user.getRoles())).thenReturn(expectedToken);

        TokenDTO generatedToken = authService.signIn(request);

        assertSame(expectedToken, generatedToken);
        assertSame(authentication, SecurityContextHolder.getContext().getAuthentication());
        verify(authenticationManager).authenticate(any(Authentication.class));
        verify(tokenProvider).createAccessToken(user.getEmail(), user.getRoles());
    }

    @Test
    void doNotAuthenticateUserThenReturnException() {
        String email = "user@exemplo.com";
        String password = "password";
        LoginRequest request = new LoginRequest(email, password);

        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenThrow(new UsernameNotFoundException("Invalid credentials"));

        assertThrows(UsernameNotFoundException.class, () -> authService.signIn(request));
        verify(authenticationManager, only()).authenticate(any(Authentication.class));
        verify(tokenProvider, never()).createAccessToken(any(), any());
    }

    @Test
    void shouldReturnNewTokenWhenUserExistsWhenUpdatingWithRefreshToken() {
        String email = "user@exemplo.com";
        String refreshToken = "refresh-token";
        User user = createUser(email, "USER");
        TokenDTO expectedToken = new TokenDTO(email, true, new Date(), null, "access-token", "refresh");

        when(userService.findByEmail(email)).thenReturn(user);
        when(tokenProvider.refreshToken(refreshToken)).thenReturn(expectedToken);

        TokenDTO generatedToken = authService.signIn(email, refreshToken);

        assertSame(expectedToken, generatedToken);
        verify(userService).findByEmail(email);
        verify(tokenProvider).refreshToken(refreshToken);
    }

    @Test
    void mustThrowExceptionWhenUserDoesNotExistWhenUpdatingWithRefreshToken() {
        String email = "user@ex.com";
        String refreshToken = "rt";
        when(userService.findByEmail(email)).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> authService.signIn(email, refreshToken));
        verify(tokenProvider, never()).refreshToken(anyString());
    }

    private User createUser(String email, String... roles) {
        User user = new User();
        user.setEmail(email);
        user.setRoles(java.util.List.of(roles));
        return user;
    }
}