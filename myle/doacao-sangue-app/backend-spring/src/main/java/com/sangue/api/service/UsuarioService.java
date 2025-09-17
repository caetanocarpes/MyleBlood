package com.sangue.api.service;

import com.sangue.api.dto.UsuarioDTO;
import com.sangue.api.entity.Usuario;
import com.sangue.api.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Regras de negócio do usuário:
 * - Cadastro com validações (idade, duplicidades, CPF normalizado)
 * - Atualização com checagens de conflito
 * - Busca e remoção
 *
 * Observação:
 * - Exceções lançadas aqui são tratadas pelo GlobalExceptionHandler.
 */
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder; // BCrypt injetado via SecurityConfig

    /** Cadastra um novo usuário após validações. */
    @Transactional
    public Usuario cadastrar(UsuarioDTO dto) {
        // Normaliza email e CPF
        String email = normalizarEmail(dto.getEmail());
        String cpf = normalizarCpf(dto.getCpf());

        if (usuarioRepository.existsByEmail(email)) {
            throw new IllegalStateException("Email já cadastrado");
        }
        if (usuarioRepository.existsByCpf(cpf)) {
            throw new IllegalStateException("CPF já cadastrado");
        }

        LocalDate nascimento = parseDataNascimento(dto.getDataNascimento());
        validarIdadeDoacao(nascimento); // 16..69 anos

        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(email);
        usuario.setCpf(cpf);
        usuario.setDataNascimento(nascimento);
        usuario.setSenha(passwordEncoder.encode(dto.getSenha())); // BCrypt

        // Campos adicionais
        usuario.setTipoSanguineo(dto.getTipoSanguineo());
        usuario.setPesoKg(dto.getPesoKg());
        usuario.setAlturaCm(dto.getAlturaCm());

        return usuarioRepository.save(usuario);
    }

    /** Retorna todos os usuários. */
    public List<Usuario> buscarTodos() {
        return usuarioRepository.findAll();
    }

    /** Retorna um usuário pelo ID, ou null se não existir. */
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    /** Retorna um usuário pelo email, ou null se não existir. */
    public Usuario buscarPorEmail(String emailRaw) {
        String email = normalizarEmail(emailRaw);
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    /**
     * Atualiza dados do usuário existente (parcial).
     * - Checa conflito de email/CPF quando alterados.
     * - Re-hash da senha apenas se informada e não vazia.
     */
    @Transactional
    public Usuario atualizarUsuario(Long id, Usuario novosDados) {
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        // Nome
        if (isNonBlank(novosDados.getNome())) {
            existente.setNome(novosDados.getNome().trim());
        }

        // Email (normaliza + checa duplicidade)
        if (isNonBlank(novosDados.getEmail())) {
            String novoEmail = normalizarEmail(novosDados.getEmail());
            if (!novoEmail.equalsIgnoreCase(existente.getEmail())
                    && usuarioRepository.existsByEmail(novoEmail)) {
                throw new IllegalStateException("Email já cadastrado");
            }
            existente.setEmail(novoEmail);
        }

        // CPF (normaliza + checa duplicidade)
        if (isNonBlank(novosDados.getCpf())) {
            String novoCpf = normalizarCpf(novosDados.getCpf());
            if (!novoCpf.equals(existente.getCpf())
                    && usuarioRepository.existsByCpf(novoCpf)) {
                throw new IllegalStateException("CPF já cadastrado");
            }
            existente.setCpf(novoCpf);
        }

        // Data de nascimento (se vier, valida idade)
        if (novosDados.getDataNascimento() != null) {
            validarIdadeDoacao(novosDados.getDataNascimento());
            existente.setDataNascimento(novosDados.getDataNascimento());
        }

        // Campos opcionais (enum e numéricos)
        if (novosDados.getTipoSanguineo() != null) {
            existente.setTipoSanguineo(novosDados.getTipoSanguineo());
        }
        if (novosDados.getPesoKg() != null) {
            existente.setPesoKg(novosDados.getPesoKg());
        }
        if (novosDados.getAlturaCm() != null) {
            existente.setAlturaCm(novosDados.getAlturaCm());
        }

        // Senha (só re-hash se informada e não vazia)
        if (isNonBlank(novosDados.getSenha())) {
            existente.setSenha(passwordEncoder.encode(novosDados.getSenha()));
        }

        return usuarioRepository.save(existente);
    }

    /** Remove usuário por ID. */
    @Transactional
    public void deletarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new EntityNotFoundException("Usuário não encontrado");
        }
        usuarioRepository.deleteById(id);
    }

    // ==========================================================
    // Helpers / validações internas
    // ==========================================================

    private String normalizarEmail(String raw) {
        return raw == null ? null : raw.trim().toLowerCase();
    }

    private String normalizarCpf(String raw) {
        return raw == null ? null : raw.replaceAll("\\D", "");
    }

    private boolean isNonBlank(String s) {
        return s != null && !s.isBlank();
    }

    private LocalDate parseDataNascimento(String data) {
        try {
            return LocalDate.parse(data); // ISO-8601 (yyyy-MM-dd)
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Data de nascimento inválida. Use o formato yyyy-MM-dd.");
        }
    }

    /** Regra básica de idade para doação: 16 a 69 anos. */
    private void validarIdadeDoacao(LocalDate nascimento) {
        int idade = Period.between(nascimento, LocalDate.now()).getYears();
        if (idade < 16 || idade > 69) {
            throw new IllegalArgumentException("Para doar sangue é necessário ter entre 16 e 69 anos");
        }
    }
}
