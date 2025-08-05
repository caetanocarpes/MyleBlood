package com.sangue.api.repository;

import com.sangue.api.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Repositório JPA para manipular a entidade Usuario (CRUD + buscas personalizadas)
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Busca um usuário pelo e-mail
    Optional<Usuario> findByEmail(String email);

    // Verifica se já existe um usuário com o e-mail informado
    boolean existsByEmail(String email);

    // Verifica se já existe um usuário com o CPF informado
    boolean existsByCpf(String cpf);
}
