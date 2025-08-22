package com.softclass.accessControl.config;

import com.softclass.accessControl.domain.User;
import com.softclass.accessControl.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class DataLoader {
    private final PasswordEncoder encoder;

    @Bean
    CommandLineRunner initUsers(UserRepository usuarioRepo) {
        return args -> {
            if (usuarioRepo.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(encoder.encode("admin"));
                admin.setEnabled(true);
                admin.setRoles(Set.of("ROLE_ADMIN", "ROLE_USER"));
                usuarioRepo.save(admin);
            }
            if (usuarioRepo.findByUsername("user").isEmpty()) {
                User user = new User();
                user.setUsername("user");
                user.setPassword(encoder.encode("user"));
                user.setEnabled(true);
                user.setRoles(Set.of("ROLE_USER"));
                usuarioRepo.save(user);
            }
        };
    }
}
