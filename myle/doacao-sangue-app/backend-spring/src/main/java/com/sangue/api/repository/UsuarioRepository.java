package com.sangue.api.repository;

import com.sangue.api.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório JPA para a entidade Usuario.
 * Fornece CRUD padrão e buscas customizadas por email e CPF.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);
}
