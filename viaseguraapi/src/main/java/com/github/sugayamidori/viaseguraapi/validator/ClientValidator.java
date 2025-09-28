package com.github.sugayamidori.viaseguraapi.validator;

import com.github.sugayamidori.viaseguraapi.exceptions.RegistroDuplicadoException;
import com.github.sugayamidori.viaseguraapi.model.Client;
import com.github.sugayamidori.viaseguraapi.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ClientValidator {

    private final ClientRepository repository;

    public void validar(Client client) {
        if(existeCLientId(client)) {
            throw new RegistroDuplicadoException("Client já cadastrado");
        }
    }

    private boolean existeCLientId(Client client) {
        Optional<Client> clientEncontrado = Optional.ofNullable(repository.findByClientId(client.getClientId()));

        if(client.getId() == null) {
            return clientEncontrado.isPresent();
        }

        return clientEncontrado
                .map(Client::getId)
                .stream()
                .anyMatch( id -> !id.equals(client.getId()));
    }
}
