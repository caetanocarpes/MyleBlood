package com.sangue.api.service;

import com.sangue.api.dto.AgendamentoDTO;
import com.sangue.api.entity.Agendamento;
import com.sangue.api.entity.Posto;
import com.sangue.api.entity.Usuario;
import com.sangue.api.repository.AgendamentoRepository;
import com.sangue.api.repository.PostoRepository;
import com.sangue.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sangue.api.dto.HistoricoDoacaoDTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Serviço responsável por operações relacionadas a agendamentos
 */
@Service
public class AgendamentoService {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PostoRepository postoRepository;

    public List<HistoricoDoacaoDTO> buscarHistoricoDoUsuario(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return agendamentoRepository.findByUsuario(usuario).stream()
                .sorted((a1, a2) -> a2.getData().compareTo(a1.getData())) // Ordena por data decrescente
                .map(a -> new HistoricoDoacaoDTO(
                        a.getPosto().getNome(),
                        a.getPosto().getCidade(),
                        a.getPosto().getEstado(),
                        a.getData().toString(),
                        a.getHorario().toString()
                ))
                .toList();
    }
    // AgendamentoService.java
    public Agendamento agendar(String email, AgendamentoDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Posto posto = postoRepository.findById(dto.getPostoId())
                .orElseThrow(() -> new RuntimeException("Posto não encontrado"));

        LocalDate novaData = LocalDate.parse(dto.getData());

        // Busca todos os agendamentos anteriores desse usuário
        List<Agendamento> agendamentos = agendamentoRepository.findByUsuario(usuario);

        // Verifica se existe alguma doação feita nos últimos 60 dias
        for (Agendamento a : agendamentos) {
            if (a.getData().isAfter(novaData.minusDays(60))) {
                throw new RuntimeException("Você só pode agendar uma nova doação após 60 dias da última.");
            }
        }

        // Cria o novo agendamento
        Agendamento agendamento = new Agendamento();
        agendamento.setUsuario(usuario);
        agendamento.setPosto(posto);
        agendamento.setData(novaData);
        agendamento.setHorario(LocalTime.parse(dto.getHorario()));

        return agendamentoRepository.save(agendamento);
    }

    public List<Agendamento> listarAgendamentosDoUsuario(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return agendamentoRepository.findByUsuario(usuario);
    }

    public boolean cancelarAgendamento(Long id, String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));

        if (!agendamento.getUsuario().getId().equals(usuario.getId())) {
            return false;
        }

        agendamentoRepository.delete(agendamento);
        return true;
    }

    public List<LocalTime> buscarHorariosOcupados(Long postoId, String dataStr) {
        LocalDate data = LocalDate.parse(dataStr);
        return agendamentoRepository.findHorariosOcupadosPorPostoEData(postoId, data);
    }
}
