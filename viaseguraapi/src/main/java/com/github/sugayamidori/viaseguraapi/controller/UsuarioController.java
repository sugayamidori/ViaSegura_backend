package com.github.sugayamidori.viaseguraapi.controller;

import com.github.sugayamidori.viaseguraapi.controller.docs.UsuarioControllerDocs;
import com.github.sugayamidori.viaseguraapi.controller.dto.SalvarUsuarioDTO;
import com.github.sugayamidori.viaseguraapi.controller.dto.UsuarioDTO;
import com.github.sugayamidori.viaseguraapi.controller.mappers.UsuarioMapper;
import com.github.sugayamidori.viaseguraapi.model.Usuario;
import com.github.sugayamidori.viaseguraapi.service.UsuarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("usuarios")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Endpoints for managing users")
public class UsuarioController implements GenericController, UsuarioControllerDocs {

    private final UsuarioService service;
    private final UsuarioMapper mapper;

    @PostMapping
    @Override
    public ResponseEntity<Void> salvar(@RequestBody @Valid SalvarUsuarioDTO dto) {
        Usuario usuario = mapper.toEntity(dto);
        service.salvar(usuario);

        URI location = gerarHeaderLocation(usuario.getId());

        return ResponseEntity.created(location).build();
    }

    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public ResponseEntity<Void> salvar(@RequestBody @Valid UsuarioDTO dto) {
        Usuario usuario = mapper.toEntity(dto);
        service.salvarByAdmin(usuario);

        URI location = gerarHeaderLocation(usuario.getId());

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/me")
    @Override
    public ResponseEntity<UsuarioDTO> obterDetalhes(Authentication authentication) {
        Usuario user = (Usuario) authentication.getPrincipal();
        return service.obterPorId(user.getId())
                .map(usuario -> {
                    UsuarioDTO dto = mapper.toDTO(usuario);
                    return ResponseEntity.ok(dto);
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/me")
    @Override
    public ResponseEntity<Object> atualizar(@RequestBody SalvarUsuarioDTO dto,
                                            Authentication authentication) {
        Usuario user = (Usuario) authentication.getPrincipal();
        return service.obterPorId(user.getId())
                .map(usuario -> {
                    Usuario usuarioAtualizado = mapper.toEntity(dto);
                    usuarioAtualizado.setId(usuario.getId());
                    usuarioAtualizado.setRoles(usuario.getRoles());
                    service.atualizar(usuarioAtualizado);
                    return ResponseEntity.noContent().build();
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/me")
    @Override
    public ResponseEntity<Object> deletar(Authentication authentication) {
        Usuario user = (Usuario) authentication.getPrincipal();
        return service.obterPorId(user.getId())
                .map(usuario -> {
                    service.deletar(usuario);
                    return ResponseEntity.noContent().build();
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
