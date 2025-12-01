package com.github.sugayamidori.viaseguraapi.controller;

import com.github.sugayamidori.viaseguraapi.controller.docs.UserControllerDocs;
import com.github.sugayamidori.viaseguraapi.controller.dto.SaveUserDTO;
import com.github.sugayamidori.viaseguraapi.controller.dto.UserDTO;
import com.github.sugayamidori.viaseguraapi.controller.mappers.UserMapper;
import com.github.sugayamidori.viaseguraapi.model.User;
import com.github.sugayamidori.viaseguraapi.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Endpoints for managing users")
public class UserController implements GenericController, UserControllerDocs {

    private final UserService service;
    private final UserMapper mapper;

    @PostMapping
    @Override
    public ResponseEntity<Void> save(@RequestBody @Valid SaveUserDTO dto) {
        User user = mapper.toEntity(dto);
        service.save(user);

        URI location = generateHeaderLocation(user.getId());

        return ResponseEntity.created(location).build();
    }

    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public ResponseEntity<Void> save(@RequestBody @Valid UserDTO dto) {
        User user = mapper.toEntity(dto);
        service.saveByAdmin(user);

        URI location = generateHeaderLocation(user.getId());

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/me")
    @Override
    public ResponseEntity<UserDTO> getDetails(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return service.findById(user.getId())
                .map(entity -> {
                    UserDTO dto = mapper.toDTO(entity);
                    return ResponseEntity.ok(dto);
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/me")
    @Override
    public ResponseEntity<Object> update(@RequestBody SaveUserDTO dto,
                                         Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return service.findById(user.getId())
                .map(entity -> {
                    User updatedUser = mapper.toEntity(dto);
                    updatedUser.setId(entity.getId());
                    updatedUser.setRoles(entity.getRoles());
                    service.update(updatedUser);
                    return ResponseEntity.noContent().build();
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/me")
    @Override
    public ResponseEntity<Object> delete(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return service.findById(user.getId())
                .map(entity -> {
                    service.delete(entity);
                    return ResponseEntity.noContent().build();
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
