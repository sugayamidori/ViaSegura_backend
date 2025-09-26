package com.github.sugayamidori.viaseguraapi.security.jwt;

import com.auth0.jwt.algorithms.Algorithm;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("security.jwt.token.secret-key")
    private String secret;

    @Value("security.jwt.token.expire-length")
    private Long validityInMilliseconds;

    private final UserDetailsService userDetailsService;

    Algorithm algorithm = null;

    @PostConstruct
    protected void init() {
    }
}
