package com.github.sugayamidori.viaseguraapi.validator;

import com.github.sugayamidori.viaseguraapi.exceptions.DuplicatedRegistryException;
import com.github.sugayamidori.viaseguraapi.model.User;
import com.github.sugayamidori.viaseguraapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository repository;

    public void validate(User user) {
        if(existsUserWithEmail(user)) {
            throw new DuplicatedRegistryException("User with email " + user.getEmail() + " already exists.");
        }
    }

    private boolean existsUserWithEmail(User user) {
        Optional<User> entity = Optional.ofNullable(repository.findByEmail(user.getEmail()));

        if(user.getId() == null) {
            return entity.isPresent();
        }

        return entity
                .map(User::getId)
                .stream()
                .anyMatch( id -> !id.equals(user.getId()));
    }
}
