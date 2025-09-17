package com.github.sugayamidori.viaseguraapi.controller;

import com.github.sugayamidori.viaseguraapi.controller.dto.ClientDTO;
import com.github.sugayamidori.viaseguraapi.controller.mappers.ClientMapper;
import com.github.sugayamidori.viaseguraapi.model.Client;
import com.github.sugayamidori.viaseguraapi.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("clients")
@RequiredArgsConstructor
@Slf4j
public class ClientController implements GenericController {

    private final ClientService service;
    private final ClientMapper mapper;

    @PostMapping
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<Void> salvar(@RequestBody ClientDTO dto) {
        log.info("Registrando novo Client: {} com scope: {}", dto.clientId(), dto.scope());
        Client client = mapper.toEntity(dto);
        service.salvar(client);

        URI location = gerarHeaderLocation(client.getId());

        return ResponseEntity.created(location).build();

    }
}
