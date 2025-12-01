package com.github.sugayamidori.viaseguraapi.security;

import com.github.sugayamidori.viaseguraapi.model.User;
import com.github.sugayamidori.viaseguraapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserService userService;
    private final PasswordEncoder encoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String login = authentication.getName();
        String typedPassword = authentication.getCredentials().toString();

        User entity = userService.findByEmail(login);

        if (entity == null) {
            throw getUserNotFoundError();
        }

        String encryptedPassword = entity.getPassword();

        boolean passwordsMatches = encoder.matches(typedPassword, encryptedPassword);

        if (passwordsMatches) {
            return new CustomAuthentication(entity);
        }

        throw getUserNotFoundError();
    }

    private static UsernameNotFoundException getUserNotFoundError() {
        return new UsernameNotFoundException("Invalid login or password");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(UsernamePasswordAuthenticationToken.class);
    }
}
