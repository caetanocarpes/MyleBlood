package com.sangue.api.controller;

import com.sangue.api.dto.LoginDTO;
import com.sangue.api.dto.UsuarioDTO;
import com.sangue.api.entity.Usuario;
import com.sangue.api.repository.UsuarioRepository;
import com.sangue.api.security.JwtUtil;
import com.sangue.api.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*; // importa todas as anotações (RestController, GetMapping etc.)

import java.util.HashMap;
import java.util.Map;

/**
 * Controller de autenticação: registro, login e /me
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // Encoder local apenas para comparar senhas no login
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // Registro de usuário
    @PostMapping("/register")
    public ResponseEntity<?> registrar(@Valid @RequestBody UsuarioDTO dto) {
        try {
            usuarioService.cadastrar(dto);
            return ResponseEntity.ok("Usuário cadastrado com sucesso!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Login: valida credenciais e retorna JWT + dados básicos
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        Usuario usuario = usuarioRepository.findByEmail(loginDTO.getEmail())
                .orElse(null);

        if (usuario == null || !encoder.matches(loginDTO.getSenha(), usuario.getSenha())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email ou senha inválidos");
        }

        String token = jwtUtil.gerarToken(usuario.getEmail());

        Map<String, Object> resposta = new HashMap<>();
        resposta.put("token", token);
        resposta.put("id", usuario.getId());
        resposta.put("nome", usuario.getNome());
        resposta.put("email", usuario.getEmail());
        resposta.put("tipoSanguineo", usuario.getTipoSanguineo());
        resposta.put("pesoKg", usuario.getPesoKg());
        resposta.put("alturaCm", usuario.getAlturaCm());

        return ResponseEntity.ok(resposta);
    }

    // Retorna dados do usuário autenticado com base no token
    @GetMapping("/me")
    public ResponseEntity<?> perfil(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtUtil.extrairEmail(token);

            Usuario usuario = usuarioService.buscarPorEmail(email);
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
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
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
        }
    }
}
