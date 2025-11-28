// language: java
package com.github.sugayamidori.viaseguraapi.security.jwt;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.github.sugayamidori.viaseguraapi.controller.dto.TokenDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    @Mock
    private UserDetailsService userDetailsService;

    private JwtTokenProvider provider;

    private String username;
    private List<String> roles;

    @BeforeEach
    void setUp() {
        provider = new JwtTokenProvider(userDetailsService);
        ReflectionTestUtils.setField(provider, "secretKey", "my-very-secret-key");
        ReflectionTestUtils.setField(provider, "validityInMilliseconds", 3600_000L);
        provider.init();

        username = "usuario@exemplo.com";
        roles = List.of("USER");

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setServerName("localhost");
        mockRequest.setServerPort(8080);
        mockRequest.setScheme("http");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void createAccessTokenDeveRetornarTokenPreenchido() {
        TokenDTO dto = provider.createAccessToken(username, roles);

        assertNotNull(dto);
        assertEquals(username, dto.username());
        assertNotNull(dto.accessToken());
        assertNotNull(dto.refreshToken());

        assertTrue(provider.validateToken(dto.accessToken()));
        assertTrue(provider.validateToken(dto.refreshToken()));
    }

    @Test
    void refreshToken_withBearerPrefix_shouldReturnNewTokenDTO() {
        TokenDTO created = provider.createAccessToken(username, roles);
        String refresh = created.refreshToken();

        TokenDTO refreshed = provider.refreshToken("Bearer " + refresh);

        assertNotNull(refreshed);
        assertEquals(created.username(), refreshed.username());
        assertNotNull(refreshed.accessToken());
        assertNotNull(refreshed.refreshToken());

        assertTrue(provider.validateToken(refreshed.accessToken()));
        assertTrue(provider.validateToken(refreshed.refreshToken()));
    }

    @Test
    void refreshToken_withoutBearerPrefix_shouldReturnNewTokenDTO() {
        TokenDTO created = provider.createAccessToken(username, roles);
        String refresh = created.refreshToken();

        assertThrows(JWTDecodeException.class, () -> provider.refreshToken(refresh));
    }

    @Test
    void resolveToken_shouldExtractTokenFromAuthorizationHeader() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getHeader("Authorization")).thenReturn("Bearer abc.def.ghi");

        String resolved = provider.resolveToken(req);

        assertEquals("abc.def.ghi", resolved);
    }

    @Test
    void validateToken_validToken_returnsTrue() {
        TokenDTO dto = provider.createAccessToken(username, roles);

        assertTrue(provider.validateToken(dto.accessToken()));
    }

    @Test
    void getAuthentication_shouldReturnAuthentication_withUserDetailsFromUserDetailsService() {
        TokenDTO dto = provider.createAccessToken(username, roles);
        String accessToken = dto.accessToken();

        when(userDetailsService.loadUserByUsername(username))
                .thenReturn(new User(username, "pwd", List.of(new SimpleGrantedAuthority("USER"))));

        Authentication auth = provider.getAuthentication(accessToken);

        assertNotNull(auth);
        assertNotNull(auth.getPrincipal());
        assertEquals(username, ((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername());
        assertFalse(auth.getAuthorities().isEmpty());
        verify(userDetailsService).loadUserByUsername(username);
    }
}