package com.github.sugayamidori.viaseguraapi.integrationtests.repository;

import com.github.sugayamidori.viaseguraapi.integrationtests.testcontainers.PostgresTestContainer;
import com.github.sugayamidori.viaseguraapi.integrationtests.testcontainers.RepositoryIntegrationTest;
import com.github.sugayamidori.viaseguraapi.model.User;
import com.github.sugayamidori.viaseguraapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

@RepositoryIntegrationTest
class UserRepositoryTest implements PostgresTestContainer {

    @Autowired
    private UserRepository repository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void findByEmail() {

        user = repository.findByEmail("admin@gmail.com");

        assertNotNull(user);
        assertNotNull(user.getId());
        assertNotNull(user.getEmail());
        assertEquals("admin@gmail.com", user.getEmail());
        assertNotNull(user.getName());
        assertNotNull(user.getPassword());
        assertNotNull(user.getRoles());
    }

    @Test
    void findByEmailWithNoResults() {

        user = repository.findByEmail("user@noexists.com");

        assertNull(user);
    }
}