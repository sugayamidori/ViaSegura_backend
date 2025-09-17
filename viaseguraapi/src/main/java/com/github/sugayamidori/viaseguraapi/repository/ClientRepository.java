package com.github.sugayamidori.viaseguraapi.repository;

import com.github.sugayamidori.viaseguraapi.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {

    Client findByClientId(String clientId);
}
