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
 * Seed de desenvolvimento (executa somente com profile 'dev').
 * Para habilitar: adicione em application.properties -> spring.profiles.active=dev
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
        seedPostos();
        seedUsuarioDemo();
    }

    private void seedPostos() {
        // Evita duplicar por nome (ajuste se tiver unique no banco)
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
            System.out.println("[seed] Postos criados.");
        } else {
            System.out.println("[seed] Postos já existentes. Pulando.");
        }
    }

    private void seedUsuarioDemo() {
        final String emailDemo = "demo@myle.com";
        final String cpfDemoRaw = "111.222.333-44"; // pode ter máscara; será normalizado
        final String cpfDemo = cpfDemoRaw.replaceAll("\\D", ""); // só dígitos

        boolean existeEmail = usuarioRepository.existsByEmail(emailDemo);
        boolean existeCpf = usuarioRepository.existsByCpf(cpfDemo);

        if (!existeEmail && !existeCpf) {
            Usuario u = new Usuario();
            u.setNome("Usuário Demo");
            u.setEmail(emailDemo); // setter já normaliza pra lowercase
            u.setCpf(cpfDemo);
            u.setSenha(passwordEncoder.encode("123456")); // BCrypt
            u.setDataNascimento(LocalDate.of(2000, 1, 1));
            u.setTipoSanguineo(TipoSanguineo.O_POS);
            u.setPesoKg(new BigDecimal("75.00"));
            u.setAlturaCm(180);

            usuarioRepository.save(u);
            System.out.println("[seed] Usuário demo criado: " + emailDemo + " / senha: 123456");
        } else {
            System.out.println("[seed] Usuário demo já existe (email ou CPF). Pulando.");
        }
    }
}
