package com.sangue.api;

import com.sangue.api.entity.Posto;
import com.sangue.api.entity.TipoSanguineo;
import com.sangue.api.entity.Usuario;
import com.sangue.api.repository.PostoRepository;
import com.sangue.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Popula dados de desenvolvimento.
 * Executa somente no profile 'dev' (defina spring.profiles.active=dev).
 */
@Component
@Profile("dev")
@RequiredArgsConstructor
public class ConfigDataLoader implements CommandLineRunner {

    private final PostoRepository postoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (postoRepository.count() == 0) {
            Posto p1 = new Posto();
            p1.setNome("Hemocentro Central");
            p1.setEndereco("Av. Principal, 1000");
            p1.setCidade("Araranguá");
            p1.setEstado("SC");

            Posto p2 = new Posto();
            p2.setNome("Unidade Sul");
            p2.setEndereco("Rua das Flores, 123");
            p2.setCidade("Araranguá");
            p2.setEstado("SC");

            postoRepository.save(p1);
            postoRepository.save(p2);
        }

        // Usuário de teste (opcional)
        if (usuarioRepository.findByEmail("demo@myle.com").isEmpty()) {
            Usuario u = new Usuario();
            u.setNome("Usuário Demo");
            u.setEmail("demo@myle.com");
            u.setCpf("11122233344");
            u.setSenha(passwordEncoder.encode("123456"));
            u.setDataNascimento(LocalDate.of(2000, 1, 1));
            u.setTipoSanguineo(TipoSanguineo.O_POS); // <-- corrigido
            u.setPesoKg(new BigDecimal("75.0"));
            u.setAlturaCm(180);
            usuarioRepository.save(u);
        }
    }
}
