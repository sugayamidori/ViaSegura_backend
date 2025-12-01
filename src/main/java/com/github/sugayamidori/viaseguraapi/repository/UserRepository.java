package com.github.sugayamidori.viaseguraapi.repository;

import com.github.sugayamidori.viaseguraapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    User findByEmail(String email);
}
