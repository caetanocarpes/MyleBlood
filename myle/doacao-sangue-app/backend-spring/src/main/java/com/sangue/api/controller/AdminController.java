package com.sangue.api.controller;

import com.sangue.api.repository.AgendamentoRepository;
import com.sangue.api.repository.PostoRepository;
import com.sangue.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AdminController {

    private final UsuarioRepository usuarioRepository;
    private final AgendamentoRepository agendamentoRepository;
    private final PostoRepository postoRepository;

    /**
     * Dados agregados para o dashboard:
     * - totalUsuarios, totalAgendamentos, totalPostos, ultimaDoacao
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardData() {
        Map<String, Object> dados = new HashMap<>();
        long totalUsuarios = usuarioRepository.count();
        long totalAgendamentos = agendamentoRepository.count();
        long totalPostos = postoRepository.count();
        LocalDate ultimaData = agendamentoRepository.findUltimaDataAgendamento();

        dados.put("totalUsuarios", totalUsuarios);
        dados.put("totalAgendamentos", totalAgendamentos);
        dados.put("totalPostos", totalPostos);
        dados.put("ultimaDoacao", ultimaData != null ? ultimaData.toString() : "Nenhuma");

        return ResponseEntity.ok(dados);
    }

    /**
     * Ranking dos postos com mais agendamentos.
     * Retorna: [{ nome, totalAgendamentos }, ...]
     */
    @GetMapping("/ranking-postos")
    public ResponseEntity<List<Map<String, Object>>> getRankingPostos() {
        List<Object[]> resultados = agendamentoRepository.rankingPorPosto();

        List<Map<String, Object>> ranking = resultados.stream().map(obj -> {
            Map<String, Object> posto = new HashMap<>();
            posto.put("nome", obj[0]);
            posto.put("totalAgendamentos", obj[1]);
            return posto;
        }).toList();

        return ResponseEntity.ok(ranking);
    }

    /**
     * Histórico de doações por usuário (admin).
     */
    @GetMapping("/historico-doacoes/{usuarioId}")
    public ResponseEntity<List<Map<String, Object>>> getHistoricoPorUsuario(@PathVariable Long usuarioId) {
        // Se ocorrer erro (ex.: ID inválido), teu GlobalExceptionHandler resolve.
        return ResponseEntity.ok(agendamentoRepository.buscarHistoricoPorUsuario(usuarioId));
    }
}
