package com.sangue.api.controller;

import com.sangue.api.dto.LoginDTO;
import com.sangue.api.dto.UsuarioDTO;
import com.sangue.api.entity.Usuario;
import com.sangue.api.repository.UsuarioRepository;
import com.sangue.api.security.JwtUtil;
import com.sangue.api.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Endpoints de autenticação:
 * - POST /auth/register  → cria usuário
 * - POST /auth/login     → gera JWT
 * - GET  /auth/me        → retorna dados do usuário autenticado
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    /** Payload de resposta do login. */
    public record LoginResponse(
            String token,
            String tokenType,
            long   expiresAt,
            Map<String, Object> usuario
    ) {}

    /** Registro de usuário */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registrar(@Valid @RequestBody UsuarioDTO dto) {
        Usuario criado = usuarioService.cadastrar(dto);

        Map<String, Object> body = new HashMap<>();
        body.put("message", "Usuário cadastrado com sucesso!");
        body.put("id", criado.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    /** Login */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            final String email = loginDTO.getEmail();
            final String senha = loginDTO.getSenha();

            if (email == null || senha == null) {
                return ResponseEntity.badRequest().body("Email e senha são obrigatórios");
            }

            Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
            if (usuario == null || !passwordEncoder.matches(senha, usuario.getSenha())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email ou senha inválidos");
            }

            // Gera JWT
            String token = jwtUtil.gerarToken(usuario.getEmail());
            long expiresAt = System.currentTimeMillis() + jwtUtil.getExpirationMillis();

            // Payload essencial
            Map<String, Object> usr = new HashMap<>();
            usr.put("id", usuario.getId());
            usr.put("nome", usuario.getNome());
            usr.put("email", usuario.getEmail());
            usr.put("cpf", usuario.getCpf());
            usr.put("tipoSanguineo", usuario.getTipoSanguineo());
            usr.put("pesoKg", usuario.getPesoKg());
            usr.put("alturaCm", usuario.getAlturaCm());

            return ResponseEntity.ok(new LoginResponse(token, "Bearer", expiresAt, usr));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro inesperado ao efetuar login");
        }
    }

    /** Perfil do usuário autenticado */
    @GetMapping("/me")
    public ResponseEntity<?> perfil(Authentication auth) {
        if (auth == null || !(auth.getPrincipal() instanceof Usuario usuario)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Não autenticado");
        }

        Map<String, Object> dados = new HashMap<>();
        dados.put("id", usuario.getId());
        dados.put("nome", usuario.getNome());
        dados.put("email", usuario.getEmail());
        dados.put("cpf", usuario.getCpf());
        dados.put("dataNascimento", usuario.getDataNascimento());
        dados.put("tipoSanguineo", usuario.getTipoSanguineo());
        dados.put("pesoKg", usuario.getPesoKg());
        dados.put("alturaCm", usuario.getAlturaCm());

        return ResponseEntity.ok(dados);
    }
}
