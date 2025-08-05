package com.sangue.api.controller;

import com.sangue.api.dto.LoginDTO;
import com.sangue.api.dto.UsuarioDTO;
import com.sangue.api.entity.Usuario;
import com.sangue.api.repository.UsuarioRepository;
import com.sangue.api.security.JwtUtil;
import com.sangue.api.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // Endpoint de cadastro - cria um novo usuário com validações
    @PostMapping("/register")
    public ResponseEntity<?> registrar(@Valid @RequestBody UsuarioDTO dto) {
        try {
            Usuario usuario = usuarioService.cadastrar(dto);
            return ResponseEntity.ok().body("Usuário cadastrado com sucesso!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint de login - autentica e retorna token + dados do usuário
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        // Busca usuário pelo email
        Usuario usuario = usuarioRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("Email ou senha inválidos"));

        // Verifica se a senha está correta
        boolean senhaOk = encoder.matches(loginDTO.getSenha(), usuario.getSenha());
        if (!senhaOk) {
            return ResponseEntity.badRequest().body("Email ou senha inválidos");
        }

        // Gera o token JWT
        String token = jwtUtil.gerarToken(usuario.getEmail());

        // Monta a resposta com token e informações do usuário
        Map<String, Object> resposta = new HashMap<>();
        resposta.put("token", token);
        resposta.put("id", usuario.getId());
        resposta.put("nome", usuario.getNome());
        resposta.put("email", usuario.getEmail());

        return ResponseEntity.ok(resposta);
    }

    // Endpoint que retorna os dados do usuário logado com base no token JWT
    @GetMapping("/me")
    public ResponseEntity<?> perfil(@RequestHeader("Authorization") String authHeader) {
        try {
            // Extrai o email do token JWT
            String token = authHeader.replace("Bearer ", "");
            String email = jwtUtil.extrairEmail(token);

            // Busca o usuário pelo email
            Usuario usuario = usuarioService.buscarPorEmail(email);
            if (usuario == null) {
                return ResponseEntity.status(404).body("Usuário não encontrado");
            }

            // Retorna os dados do usuário (sem a senha)
            Map<String, Object> dados = new HashMap<>();
            dados.put("id", usuario.getId());
            dados.put("nome", usuario.getNome());
            dados.put("email", usuario.getEmail());
            dados.put("cpf", usuario.getCpf());
            dados.put("dataNascimento", usuario.getDataNascimento());

            return ResponseEntity.ok(dados);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Token inválido");
        }
    }
}
