package com.sangue.api.controller;

import com.sangue.api.dto.AgendamentoDTO;
import com.sangue.api.dto.HistoricoDoacaoDTO;
import com.sangue.api.entity.Agendamento;
import com.sangue.api.security.JwtUtil;
import com.sangue.api.service.AgendamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/agendamentos")
@CrossOrigin(origins = "*")
public class AgendamentoController {

    @Autowired
    private AgendamentoService agendamentoService;

    @Autowired
    private JwtUtil jwtUtil;

    // Cria um novo agendamento usando o e-mail do usuário extraído do token JWT
    @PostMapping
    public ResponseEntity<?> agendar(@RequestBody AgendamentoDTO dto,
                                     @RequestHeader("Authorization") String authHeader) {
        try {
            String email = jwtUtil.extrairEmail(authHeader.replace("Bearer ", ""));
            Agendamento novo = agendamentoService.agendar(email, dto);
            return ResponseEntity.ok(novo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Lista todos os agendamentos do usuário logado
    @GetMapping("/me")
    public ResponseEntity<?> meusAgendamentos(@RequestHeader("Authorization") String authHeader) {
        try {
            String email = jwtUtil.extrairEmail(authHeader.replace("Bearer ", ""));
            List<Agendamento> lista = agendamentoService.listarAgendamentosDoUsuario(email);
            return ResponseEntity.ok(lista);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body("Token inválido ou usuário não encontrado.");
        }
    }

    // Cancela um agendamento, se for do próprio usuário
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelar(@PathVariable Long id,
                                      @RequestHeader("Authorization") String authHeader) {
        try {
            String email = jwtUtil.extrairEmail(authHeader.replace("Bearer ", ""));
            boolean sucesso = agendamentoService.cancelarAgendamento(id, email);

            if (sucesso) return ResponseEntity.ok("Agendamento cancelado com sucesso.");
            else return ResponseEntity.status(403).body("Você não tem permissão para cancelar este agendamento.");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Erro ao cancelar agendamento.");
        }
    }

    // Retorna os horários ocupados de um posto em uma data
    @GetMapping("/ocupados")
    public ResponseEntity<?> horariosOcupados(@RequestParam Long postoId,
                                              @RequestParam String data) {
        try {
            List<LocalTime> ocupados = agendamentoService.buscarHorariosOcupados(postoId, data);
            return ResponseEntity.ok(ocupados);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao buscar horários ocupados.");
        }
    }

    // Retorna o histórico de doações do usuário logado
    @GetMapping("/historico")
    public ResponseEntity<?> historicoDoUsuario(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtUtil.extrairEmail(token);

            List<HistoricoDoacaoDTO> historico = agendamentoService.buscarHistoricoDoUsuario(email);
            return ResponseEntity.ok(historico);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Token inválido ou erro ao buscar histórico.");
        }
    }

}
