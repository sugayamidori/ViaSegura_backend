package com.github.sugayamidori.viaseguraapi.service;

import com.github.sugayamidori.viaseguraapi.exceptions.DuplicatedRegistryException;
import com.github.sugayamidori.viaseguraapi.model.User;
import com.github.sugayamidori.viaseguraapi.repository.UserRepository;
import com.github.sugayamidori.viaseguraapi.validator.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private UserValidator validator;

    @InjectMocks
    private UserService service;

    private User user;

    @BeforeEach
    void setUp() {
        this.user = new User();
        user.setEmail("user@exemplo.com");
        user.setPassword("password");
    }

    @Test
    void shouldValidateEncodePasswordSetRolesAndSave() {
        when(encoder.encode("password")).thenReturn("encodedPassword");

        service.save(user);
        verify(validator).validate(user);
        verify(encoder).encode("password");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(repository).save(captor.capture());
        User entity = captor.getValue();

        assertEquals("encodedPassword", entity.getPassword());
        assertEquals(List.of("USER"), entity.getRoles());
        assertEquals("user@exemplo.com", entity.getEmail());
    }

    @Test
    void shouldNotSaveAndThrowException() {
        doThrow(new DuplicatedRegistryException("User already exists")).when(validator).validate(user);

        assertThrows(DuplicatedRegistryException.class, () -> service.save(user));
        verify(encoder, never()).encode(anyString());
        verify(repository, never()).save(any());
    }

    @Test
    void shouldDelegateToRepository() {
        when(repository.findByEmail("user@exemplo.com")).thenReturn(user);

        User result = service.findByEmail("user@exemplo.com");

        verify(repository).findByEmail("user@exemplo.com");
        assertNotNull(result);
        assertSame(user, result);
    }

    @Test
    void shouldReturnNull() {
        when(repository.findByEmail("user@exemplo.com")).thenReturn(null);

        User result = service.findByEmail("user@exemplo.com");

        verify(repository).findByEmail("user@exemplo.com");
        assertNull(result);
    }

    @Test
    void shouldSaveByAdminWithDefaultPassword() {
        user.setRoles(List.of("ADMIN")); // roles inicial para garantir que será sobrescrito
        // prefixo esperado: "usuario" -> senha gerada: "usuario123"
        when(encoder.encode("user123")).thenReturn("encodedPasswordByAdmin");

        service.saveByAdmin(user);

        verify(validator).validate(user);
        verify(encoder).encode("user123");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(repository).save(captor.capture());
        User entity = captor.getValue();

        assertEquals("encodedPasswordByAdmin", entity.getPassword());
        assertEquals("user@exemplo.com", entity.getEmail());
        assertNotNull(user.getRoles());
    }

    @Test
    void shouldUpdateUserEncodingPasswordAndCallRepository() {
        when(encoder.encode("password")).thenReturn("encodedPasswordUpdated");

        service.update(user);

        verify(validator).validate(user);
        verify(encoder).encode("password");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(repository).save(captor.capture());
        User entity = captor.getValue();

        assertEquals("encodedPasswordUpdated", entity.getPassword());
    }

    @Test
    void shouldNotUpdateAndThrowException() {
        doThrow(new DuplicatedRegistryException("User already exists")).when(validator).validate(user);

        assertThrows(DuplicatedRegistryException.class, () -> service.update(user));
        verify(encoder, never()).encode(anyString());
        verify(repository, never()).save(any());
    }

    @Test
    void shouldDeleteUserDelegatingToRepository() {
        service.delete(user);
        verify(repository).delete(user);
    }

    @Test
    void shouldFindByIdDelegatingToRepository() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.of(user));

        Optional<User> result = service.findById(id);

        verify(repository).findById(id);
        assertTrue(result.isPresent());
        assertSame(user, result.get());
    }

}