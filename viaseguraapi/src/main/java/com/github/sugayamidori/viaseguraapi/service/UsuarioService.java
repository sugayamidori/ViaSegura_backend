package com.github.sugayamidori.viaseguraapi.service;

import com.github.sugayamidori.viaseguraapi.model.Usuario;
import com.github.sugayamidori.viaseguraapi.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;

    public void salvar(Usuario usuario) {
        var senha = usuario.getSenha();
        usuario.setSenha(encoder.encode(senha));
        usuario.setRoles(List.of("OPERADOR"));
        repository.save(usuario);
    }

    public Usuario obterPorEmail(String email) {
        return repository.findByEmail(email);
    }
}
