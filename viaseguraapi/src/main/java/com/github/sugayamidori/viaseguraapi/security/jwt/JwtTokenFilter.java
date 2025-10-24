package com.github.sugayamidori.viaseguraapi.security.jwt;

import com.github.sugayamidori.viaseguraapi.model.Usuario;
import com.github.sugayamidori.viaseguraapi.security.CustomAuthentication;
import com.github.sugayamidori.viaseguraapi.service.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends GenericFilterBean {

    private final JwtTokenProvider tokenProvider;
    private final UsuarioService usuarioService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filter) throws IOException, ServletException {
        var token = tokenProvider.resolveToken((HttpServletRequest) request);
        if (StringUtils.isNotBlank(token) && tokenProvider.validateToken(token)) {
            Authentication authentication = tokenProvider.getAuthentication(token);
            if (authentication != null) {
                String login = authentication.getName();
                Usuario usuario = usuarioService.obterPorEmail(login);
                if (usuario != null) {
                    authentication = new CustomAuthentication(usuario);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        filter.doFilter(request, response);
    }
}
