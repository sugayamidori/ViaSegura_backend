package com.github.sugayamidori.viaseguraapi.controller;

import com.github.sugayamidori.viaseguraapi.controller.dto.SalvarUsuarioDTO;
import com.github.sugayamidori.viaseguraapi.controller.mappers.UsuarioMapper;
import com.github.sugayamidori.viaseguraapi.model.Usuario;
import com.github.sugayamidori.viaseguraapi.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("usuarios")
@RequiredArgsConstructor
public class UsuarioController implements GenericController {

    private final UsuarioService service;
    private final UsuarioMapper mapper;

    @PostMapping
    public ResponseEntity<Void> salvar(@RequestBody @Valid SalvarUsuarioDTO dto) {
        Usuario usuario = mapper.toEntity(dto);
        service.salvar(usuario);

        URI location = gerarHeaderLocation(usuario.getId());

        return ResponseEntity.created(location).build();
    }
}
