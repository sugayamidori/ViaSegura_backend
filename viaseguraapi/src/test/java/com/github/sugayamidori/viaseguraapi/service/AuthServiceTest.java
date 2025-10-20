package com.github.sugayamidori.viaseguraapi.service;

import com.github.sugayamidori.viaseguraapi.controller.dto.LoginRequest;
import com.github.sugayamidori.viaseguraapi.controller.dto.TokenDTO;
import com.github.sugayamidori.viaseguraapi.model.Usuario;
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
    private UsuarioService usuarioService;

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
    void deveAutenticarUsuarioERetornarTokenAoFazerLogin() {
        String email = "usuario@exemplo.com";
        String senha = "senha";
        LoginRequest request = new LoginRequest(email, senha);
        Usuario usuario = criarUsuario(email, "OPERADOR");
        Authentication authentication = mock(Authentication.class);
        TokenDTO tokenEsperado = new TokenDTO(email, true, new Date(), null, "access-token", "refresh");

        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(usuario);
        when(tokenProvider.createAccessToken(usuario.getEmail(), usuario.getRoles())).thenReturn(tokenEsperado);

        TokenDTO tokenGerado = authService.signIn(request);

        assertSame(tokenEsperado, tokenGerado);
        assertSame(authentication, SecurityContextHolder.getContext().getAuthentication());
        verify(authenticationManager).authenticate(any(Authentication.class));
        verify(tokenProvider).createAccessToken(usuario.getEmail(), usuario.getRoles());
    }

    @Test
    void naoDeveAutenticarUsuarioERetornarException() {
        String email = "usuario@exemplo.com";
        String senha = "senha";
        LoginRequest request = new LoginRequest(email, senha);

        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenThrow(new UsernameNotFoundException("Usuário e/ou senha incorretos"));

        assertThrows(UsernameNotFoundException.class, () -> authService.signIn(request));
        verify(authenticationManager, only()).authenticate(any(Authentication.class));
        verify(tokenProvider, never()).createAccessToken(any(), any());
    }

    @Test
    void deveRetornarNovoTokenQuandoUsuarioExisteAoAtualizarComRefreshToken() {
        String email = "teste@exemplo.com";
        String refreshToken = "refresh-token";
        Usuario usuario = criarUsuario(email, "OPERADOR");
        TokenDTO tokenEsperado = new TokenDTO(email, true, new Date(), null, "access-token", "refresh");

        when(usuarioService.obterPorEmail(email)).thenReturn(usuario);
        when(tokenProvider.refreshToken(refreshToken)).thenReturn(tokenEsperado);

        TokenDTO tokenGerado = authService.signIn(email, refreshToken);

        assertSame(tokenEsperado, tokenGerado);
        verify(usuarioService).obterPorEmail(email);
        verify(tokenProvider).refreshToken(refreshToken);
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExisteAoAtualizarComRefreshToken() {
        String email = "naoexiste@ex.com";
        String refreshToken = "rt";
        when(usuarioService.obterPorEmail(email)).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> authService.signIn(email, refreshToken));
        verify(tokenProvider, never()).refreshToken(anyString());
    }

    private Usuario criarUsuario(String email, String... roles) {
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setRoles(java.util.List.of(roles));
        return usuario;
    }
}