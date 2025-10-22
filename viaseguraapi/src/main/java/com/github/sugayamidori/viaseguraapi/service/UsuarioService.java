package com.github.sugayamidori.viaseguraapi.service;

import com.github.sugayamidori.viaseguraapi.model.Usuario;
import com.github.sugayamidori.viaseguraapi.repository.UsuarioRepository;
import com.github.sugayamidori.viaseguraapi.validator.UsuarioValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;
    private final UsuarioValidator validator;

    public void salvar(Usuario usuario) {
        validator.validar(usuario);
        var senha = usuario.getSenha();
        usuario.setSenha(encoder.encode(senha));
        usuario.setRoles(List.of("OPERADOR"));
        repository.save(usuario);
    }

    public void salvarByAdmin(Usuario usuario) {
        validator.validar(usuario);
        String email = usuario.getEmail();
        String prefixo = email.substring(0, email.indexOf('@'));
        var senha = prefixo + "123";
        usuario.setSenha(encoder.encode(senha));
        repository.save(usuario);
    }

    public void atualizar(Usuario usuario) {
        validator.validar(usuario);
        var senha = usuario.getSenha();
        usuario.setSenha(encoder.encode(senha));
        repository.save(usuario);
    }

    public void deletar(Usuario usuario) {
        repository.delete(usuario);
    }

    public Optional<Usuario> obterPorId(UUID id) {
        return repository.findById(id);
    }

    public Usuario obterPorEmail(String email) {
        return repository.findByEmail(email);
    }

    private String gerarSenhaAleatoria(int tamanho) {
        final String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "abcdefghijklmnopqrstuvwxyz"
                + "0123456789"
                + "!@#$%&*";
        SecureRandom random = new SecureRandom();
        StringBuilder senha = new StringBuilder();

        for (int i = 0; i < tamanho; i++) {
            int index = random.nextInt(caracteres.length());
            senha.append(caracteres.charAt(index));
        }

        return senha.toString();
    }
}
