package com.github.sugayamidori.viaseguraapi.service;

import com.github.sugayamidori.viaseguraapi.model.User;
import com.github.sugayamidori.viaseguraapi.repository.UserRepository;
import com.github.sugayamidori.viaseguraapi.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final UserValidator validator;

    public void save(User user) {
        validator.validate(user);
        var password = user.getPassword();
        user.setPassword(encoder.encode(password));
        user.setRoles(List.of("USER"));
        repository.save(user);
    }

    public void saveByAdmin(User user) {
        validator.validate(user);
        String email = user.getEmail();
        String prefix = email.substring(0, email.indexOf('@'));
        var password = prefix + "123";
        user.setPassword(encoder.encode(password));
        repository.save(user);
    }

    public void update(User user) {
        validator.validate(user);
        var password = user.getPassword();
        user.setPassword(encoder.encode(password));
        repository.save(user);
    }

    public void delete(User user) {
        repository.delete(user);
    }

    public Optional<User> findById(UUID id) {
        return repository.findById(id);
    }

    public User findByEmail(String email) {
        return repository.findByEmail(email);
    }

    private String generateRandomPassword(int size) {
        final String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "abcdefghijklmnopqrstuvwxyz"
                + "0123456789"
                + "!@#$%&*";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < size; i++) {
            int index = random.nextInt(caracteres.length());
            password.append(caracteres.charAt(index));
        }

        return password.toString();
    }
}
