package com.sangue.api.service;

import com.sangue.api.dto.AgendamentoDTO;
import com.sangue.api.dto.HistoricoDoacaoDTO;
import com.sangue.api.entity.Agendamento;
import com.sangue.api.entity.Posto;
import com.sangue.api.entity.Usuario;
import com.sangue.api.repository.AgendamentoRepository;
import com.sangue.api.repository.PostoRepository;
import com.sangue.api.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;

/**
 * Regras de negócio dos agendamentos.
 * - Não permitir passado
 * - Intervalo mínimo de 60 dias entre doações
 * - Evitar conflito de horário no mesmo posto
 * - Evitar que o mesmo usuário marque dois horários iguais
 */
@Service
@RequiredArgsConstructor
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PostoRepository postoRepository;

    /** Histórico do usuário (ordenado desc por data) */
    public List<HistoricoDoacaoDTO> buscarHistoricoDoUsuario(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        return agendamentoRepository.findByUsuario(usuario).stream()
                .sorted(Comparator.comparing(Agendamento::getData).reversed())
                .map(a -> new HistoricoDoacaoDTO(
                        a.getPosto().getNome(),
                        a.getPosto().getCidade(),
                        a.getPosto().getEstado(),
                        a.getData().toString(),
                        a.getHorario().toString()
                ))
                .toList();
    }

    /** Cria agendamento (email vem do token, mas controller injeta o principal quando possível) */
    @Transactional
    public Agendamento agendar(String email, AgendamentoDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        Posto posto = postoRepository.findById(dto.getPostoId())
                .orElseThrow(() -> new EntityNotFoundException("Posto não encontrado"));

        LocalDate data = parseData(dto.getData());
        LocalTime horario = parseHora(dto.getHorario());

        // 1) Não permitir passado (data/hora comparados com "agora")
        validarFuturo(data, horario);

        // 2) Intervalo mínimo de 60 dias entre doações (pega a MAIOR data anterior)
        var agsUsuario = agendamentoRepository.findByUsuario(usuario);
        var ultimaDoacao = agsUsuario.stream()
                .map(Agendamento::getData)
                .max(LocalDate::compareTo)
                .orElse(null);
        if (ultimaDoacao != null && !data.isAfter(ultimaDoacao.plusDays(60).minusDays(1))) {
            throw new IllegalArgumentException("Você só pode agendar uma nova doação após 60 dias da última.");
        }

        // 3) Conflito de horário no posto
        if (agendamentoRepository.existsByPosto_IdAndDataAndHorario(posto.getId(), data, horario)) {
            throw new IllegalStateException("Horário já ocupado para este posto.");
        }

        // 4) Mesmo usuário já marcado no mesmo instante (qualquer posto)
        if (agendamentoRepository.existsByUsuario_IdAndDataAndHorario(usuario.getId(), data, horario)) {
            throw new IllegalStateException("Você já possui um agendamento nesse horário.");
        }

        Agendamento ag = new Agendamento();
        ag.setUsuario(usuario);
        ag.setPosto(posto);
        ag.setData(data);
        ag.setHorario(horario);

        return agendamentoRepository.save(ag);
    }

    /** Lista agendamentos do usuário */
    public List<Agendamento> listarAgendamentosDoUsuario(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        return agendamentoRepository.findByUsuario(usuario);
    }

    /** Cancela agendamento (apenas do próprio usuário) */
    @Transactional
    public void cancelarAgendamento(Long id, String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Agendamento não encontrado"));

        if (!agendamento.getUsuario().getId().equals(usuario.getId())) {
            throw new SecurityException("Você não tem permissão para cancelar este agendamento");
        }

        agendamentoRepository.delete(agendamento);
    }

    /** Horários ocupados por posto/data (para o front bloquear) */
    public List<LocalTime> buscarHorariosOcupados(Long postoId, String dataStr) {
        LocalDate data = parseData(dataStr);
        return agendamentoRepository.findHorariosOcupadosPorPostoEData(postoId, data);
    }

    // ----------------- Helpers -----------------

    private LocalDate parseData(String raw) {
        try {
            return LocalDate.parse(raw); // yyyy-MM-dd
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Data inválida. Use o formato yyyy-MM-dd.");
        }
    }

    private LocalTime parseHora(String raw) {
        try {
            return LocalTime.parse(raw); // HH:mm
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Horário inválido. Use o formato HH:mm.");
        }
    }

    private void validarFuturo(LocalDate data, LocalTime horario) {
        LocalDateTime dt = LocalDateTime.of(data, horario);
        if (dt.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Não é possível agendar no passado.");
        }
    }
}
