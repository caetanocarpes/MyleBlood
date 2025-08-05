package com.sangue.api.controller;

import com.sangue.api.repository.AgendamentoRepository;
import com.sangue.api.repository.PostoRepository;
import com.sangue.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private PostoRepository postoRepository;

    /**
     * Endpoint que retorna dados agregados para o dashboard administrativo:
     * - Total de usuários
     * - Total de agendamentos
     * - Total de postos
     * - Última data de doação registrada
     */
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardData() {
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
     * Endpoint que retorna um ranking dos postos com mais agendamentos.
     * Retorna uma lista de objetos com nome do posto e total de agendamentos.
     */
    @GetMapping("/ranking-postos")
    public ResponseEntity<?> getRankingPostos() {
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
     * Retorna o histórico de doações de um usuário específico (admin visualiza).
     * Requer o ID do usuário como parâmetro.
     */
    @GetMapping("/historico-doacoes/{usuarioId}")
    public ResponseEntity<?> getHistoricoPorUsuario(@PathVariable Long usuarioId) {
        try {
            List<Map<String, Object>> historico = agendamentoRepository.buscarHistoricoPorUsuario(usuarioId);
            return ResponseEntity.ok(historico);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Erro ao buscar histórico de doações.");
        }
    }
}
