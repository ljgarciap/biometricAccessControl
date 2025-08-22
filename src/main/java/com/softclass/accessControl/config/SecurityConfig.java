package com.softclass.accessControl.config;

import com.softclass.accessControl.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserRepository usuarioRepo;

    public SecurityConfig(UserRepository usuarioRepo, User u) {
        this.usuarioRepo = usuarioRepo;
    }

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> usuarioRepo.findByUsername(username)
                .map(u -> User.withUsername(u.getUsername())
                        .password(u.getPassword())
                        .disabled(!u.isEnabled())
                        .authorities(u.getRoles().toArray(String[]::new))
                        .build())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Para simplificar en local. Si lo prefieres, deja CSRF ON y añade tokens a los formularios.
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/h2/**", "/login", "/error").permitAll()
                        .requestMatchers("/personas/**", "/enrolar/**", "/export/**").hasRole("ADMIN")
                        .requestMatchers("/verificar/**", "/").hasAnyRole("ADMIN", "USER")
                        .anyRequest().authenticated()
                )
                .headers(h -> h.frameOptions(f -> f.disable())) // Permitir consola H2
                .formLogin(form -> form.loginPage("/login").permitAll().defaultSuccessUrl("/", true))
                .logout(Customizer.withDefaults());
        return http.build();
    }
}