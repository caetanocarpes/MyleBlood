package com.sangue.api.service;

import com.sangue.api.dto.UsuarioDTO;
import com.sangue.api.entity.Usuario;
import com.sangue.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // Cadastra um novo usuário após validações
    public Usuario cadastrar(UsuarioDTO dto) {
        // Verifica duplicação por email ou CPF
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }

        if (usuarioRepository.existsByCpf(dto.getCpf())) {
            throw new RuntimeException("CPF já cadastrado");
        }

        // Validação de idade mínima e máxima (16 a 69 anos)
        LocalDate hoje = LocalDate.now();
        LocalDate nascimento = LocalDate.parse(dto.getDataNascimento());
        int idade = Period.between(nascimento, hoje).getYears();

        if (idade < 16 || idade > 69) {
            throw new RuntimeException("Para doar sangue é necessário ter entre 16 e 69 anos");
        }

        // Cria novo objeto Usuario e popula com dados do DTO
        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setCpf(dto.getCpf());
        usuario.setDataNascimento(nascimento);
        usuario.setSenha(encoder.encode(dto.getSenha())); // Criptografa a senha

        return usuarioRepository.save(usuario); // Salva no banco
    }

    // Retorna todos os usuários cadastrados
    public List<Usuario> buscarTodos() {
        return usuarioRepository.findAll();
    }

    // Retorna um usuário pelo ID
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    // Retorna um usuário pelo email (usado para /auth/me)
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    // Atualiza os dados de um usuário existente
    public Usuario atualizarUsuario(Long id, Usuario novosDados) {
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        existente.setNome(novosDados.getNome());
        existente.setEmail(novosDados.getEmail());
        existente.setCpf(novosDados.getCpf());

        if (!novosDados.getSenha().isBlank()) {
            existente.setSenha(encoder.encode(novosDados.getSenha()));
        }

        return usuarioRepository.save(existente);
    }

    // Deleta o usuário do banco
    public void deletarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado");
        }

        usuarioRepository.deleteById(id);
    }
}
