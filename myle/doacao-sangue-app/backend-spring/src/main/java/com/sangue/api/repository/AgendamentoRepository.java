package com.sangue.api.repository;

import com.sangue.api.entity.Agendamento;
import com.sangue.api.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    // Todos os agendamentos de um posto numa data
    List<Agendamento> findByPostoIdAndData(Long postoId, LocalDate data);

    // Todos os agendamentos de um usuário
    List<Agendamento> findByUsuario(Usuario usuario);

    // Horários ocupados num posto numa data (para o front bloquear)
    @Query("SELECT a.horario FROM Agendamento a WHERE a.posto.id = :postoId AND a.data = :data")
    List<LocalTime> findHorariosOcupadosPorPostoEData(Long postoId, LocalDate data);

    // Verificação de conflito (posto + data + hora)
    boolean existsByPosto_IdAndDataAndHorario(Long postoId, LocalDate data, LocalTime horario);

    // Verifica se o usuário já tem agendamento no mesmo instante (qualquer posto)
    boolean existsByUsuario_IdAndDataAndHorario(Long usuarioId, LocalDate data, LocalTime horario);

    // --- métricas/admin (opcionais que você já usa) ---
    @Query("SELECT MAX(a.data) FROM Agendamento a")
    LocalDate findUltimaDataAgendamento();

    @Query("SELECT a.posto.nome, COUNT(a) FROM Agendamento a GROUP BY a.posto.nome ORDER BY COUNT(a) DESC")
    List<Object[]> rankingPorPosto();

    @Query("""
        SELECT new map(
            a.posto.nome as posto,
            a.posto.cidade as cidade,
            a.data as data,
            a.horario as horario
        )
        FROM Agendamento a
        WHERE a.usuario.id = :usuarioId
        ORDER BY a.data DESC
    """)
    List<Map<String, Object>> buscarHistoricoPorUsuario(Long usuarioId);
}
