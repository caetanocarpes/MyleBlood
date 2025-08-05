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

    // Busca todos os agendamentos de um posto em uma data específica
    List<Agendamento> findByPostoIdAndData(Long postoId, LocalDate data);

    // Busca todos os agendamentos feitos por um usuário
    List<Agendamento> findByUsuario(Usuario usuario);

    // Busca os horários ocupados em um posto numa data específica (para mostrar no frontend)
    @Query("SELECT a.horario FROM Agendamento a WHERE a.posto.id = :postoId AND a.data = :data")
    List<LocalTime> findHorariosOcupadosPorPostoEData(Long postoId, LocalDate data);

    // Retorna a última data registrada de agendamento (para o dashboard admin)
    @Query("SELECT MAX(a.data) FROM Agendamento a")
    LocalDate findUltimaDataAgendamento();

    // Ranking dos postos com mais agendamentos (nome + total), ordenado do maior pro menor
    @Query("SELECT a.posto.nome, COUNT(a) FROM Agendamento a GROUP BY a.posto.nome ORDER BY COUNT(a) DESC")
    List<Object[]> rankingPorPosto();

    // Retorna o histórico de doações de um usuário específico (usado no admin)
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
