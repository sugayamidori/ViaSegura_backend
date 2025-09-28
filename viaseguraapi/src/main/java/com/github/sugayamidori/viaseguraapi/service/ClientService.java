package com.github.sugayamidori.viaseguraapi.service;

import com.github.sugayamidori.viaseguraapi.model.Client;
import com.github.sugayamidori.viaseguraapi.repository.ClientRepository;
import com.github.sugayamidori.viaseguraapi.validator.ClientValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository repository;
    private final PasswordEncoder encoder;
    private final ClientValidator validator;

    public Client salvar(Client client) {
        validator.validar(client);
        String senhaCriptografada = encoder.encode(client.getClientSecret());
        client.setClientSecret(senhaCriptografada);
        return repository.save(client);
    }

    public Client obterPorClientID(String clientId){
        return repository.findByClientId(clientId);
    }
}
