package com.sangue.api.controller;

import com.sangue.api.dto.AgendamentoDTO;
import com.sangue.api.dto.HistoricoDoacaoDTO;
import com.sangue.api.entity.Agendamento;
import com.sangue.api.entity.Usuario;
import com.sangue.api.security.JwtUtil;
import com.sangue.api.service.AgendamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;

/**
 * Endpoints de agendamento.
 * Protegidos por JWT; preferimos obter o usuário pelo SecurityContext.
 */
@RestController
@RequestMapping("/api/agendamentos")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AgendamentoController {

    private final AgendamentoService agendamentoService;
    private final JwtUtil jwtUtil; // mantido apenas para /historico via header; ideal: também usar Authentication

    /** Cria agendamento para o usuário autenticado */
    @PostMapping
    public ResponseEntity<Agendamento> agendar(@Valid @RequestBody AgendamentoDTO dto,
                                               Authentication auth) {
        Usuario user = (Usuario) auth.getPrincipal();
        Agendamento novo = agendamentoService.agendar(user.getEmail(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novo);
    }

    /** Lista agendamentos do usuário autenticado */
    @GetMapping("/me")
    public ResponseEntity<List<Agendamento>> meusAgendamentos(Authentication auth) {
        Usuario user = (Usuario) auth.getPrincipal();
        return ResponseEntity.ok(agendamentoService.listarAgendamentosDoUsuario(user.getEmail()));
    }

    /** Cancela um agendamento do próprio usuário */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Long id, Authentication auth) {
        Usuario user = (Usuario) auth.getPrincipal();
        agendamentoService.cancelarAgendamento(id, user.getEmail());
        return ResponseEntity.noContent().build();
    }

    /** Horários ocupados em um posto/data (para o front bloquear seleção) */
    @GetMapping("/ocupados")
    public ResponseEntity<List<LocalTime>> horariosOcupados(@RequestParam Long postoId,
                                                            @RequestParam String data) {
        return ResponseEntity.ok(agendamentoService.buscarHorariosOcupados(postoId, data));
    }

    /** Histórico do usuário logado (se preferir, troque para Authentication também) */
    @GetMapping("/historico")
    public ResponseEntity<List<HistoricoDoacaoDTO>> historicoDoUsuario(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extrairEmail(token);
        return ResponseEntity.ok(agendamentoService.buscarHistoricoDoUsuario(email));
    }
}
