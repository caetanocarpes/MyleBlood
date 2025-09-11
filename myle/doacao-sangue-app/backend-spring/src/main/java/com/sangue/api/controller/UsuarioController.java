package com.sangue.api.controller;

import com.sangue.api.dto.UsuarioUpdateDTO;
import com.sangue.api.entity.Usuario;
import com.sangue.api.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Endpoints administrativos de usuários.
 * (Cadastro é feito pelo AuthController -> /auth/register)
 * Aqui ficam listagem, busca, atualização e exclusão.
 */
@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    /** Lista todos os usuários cadastrados. */
    @GetMapping
    public ResponseEntity<List<Usuario>> listarTodos() {
        return ResponseEntity.ok(usuarioService.buscarTodos());
    }

    /** Busca usuário por ID. Retorna 404 se não encontrar. */
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        Usuario usuario = usuarioService.buscarPorId(id);
        return (usuario == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(usuario);
    }

    /**
     * Atualiza dados de um usuário (parcial).
     * - Usa UsuarioUpdateDTO (campos opcionais).
     * - Exceções (404/409/400) são tratadas pelo GlobalExceptionHandler.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizar(@PathVariable Long id,
                                             @Valid @RequestBody UsuarioUpdateDTO dto) {
        Usuario patch = mapUpdateDtoParaUsuario(dto);
        Usuario atualizado = usuarioService.atualizarUsuario(id, patch);
        return ResponseEntity.ok(atualizado);
    }

    /** Deleta um usuário por ID. Retorna 204 se sucesso. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        usuarioService.deletarUsuario(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // ==========================================================
    // Helpers
    // ==========================================================

    /** Converte UsuarioUpdateDTO → Usuario (aplica apenas campos presentes). */
    private Usuario mapUpdateDtoParaUsuario(UsuarioUpdateDTO dto) {
        Usuario u = new Usuario();
        if (dto.getNome() != null) u.setNome(dto.getNome());
        if (dto.getEmail() != null) u.setEmail(dto.getEmail());
        if (dto.getCpf() != null) u.setCpf(dto.getCpf()); // avalie bloquear no service
        if (dto.getSenha() != null) u.setSenha(dto.getSenha());
        if (dto.getTipoSanguineo() != null) u.setTipoSanguineo(dto.getTipoSanguineo());
        if (dto.getPesoKg() != null) u.setPesoKg(dto.getPesoKg());
        if (dto.getAlturaCm() != null) u.setAlturaCm(dto.getAlturaCm());
        if (dto.getDataNascimento() != null) u.setDataNascimento(LocalDate.parse(dto.getDataNascimento()));
        return u;
    }
}
