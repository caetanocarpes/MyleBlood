package com.sangue.api.repository;

import com.sangue.api.entity.Posto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repositório JPA para manipular a entidade Posto (CRUD automático)
@Repository
public interface PostoRepository extends JpaRepository<Posto, Long> {
}
