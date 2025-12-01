package com.github.sugayamidori.viaseguraapi.validator;

import com.github.sugayamidori.viaseguraapi.exceptions.DuplicatedRegistryException;
import com.github.sugayamidori.viaseguraapi.model.User;
import com.github.sugayamidori.viaseguraapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserValidator validator;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("user@exemplo.com");
    }

    @Test
    void shouldValidateSuccessfully() {
        when(repository.findByEmail(anyString())).thenReturn(null);

        validator.validate(user);

        verify(repository).findByEmail(anyString());
        assertDoesNotThrow(() -> validator.validate(user));

    }

    @Test
    void shouldThrowException() {
        when(repository.findByEmail(anyString())).thenReturn(user);

        assertThrows(DuplicatedRegistryException.class, () -> validator.validate(user));

        verify(repository).findByEmail(anyString());

    }
}