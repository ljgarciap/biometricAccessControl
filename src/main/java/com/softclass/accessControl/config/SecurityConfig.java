package com.softclass.accessControl.config;

import com.softclass.accessControl.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserRepository usuarioRepo;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> usuarioRepo.findByUsername(username)
                .map(u -> User.withUsername(u.getUsername())
                        .password(u.getPassword())
                        .disabled(!u.isEnabled())
                        .authorities(u.getRoles().toArray(new String[0]))
                        .build())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/h2/**", "/login", "/error").permitAll()
                        .requestMatchers("/personas/**", "/enrolar/**", "/export/**").hasRole("ADMIN")
                        .requestMatchers("/verificar/**", "/", "/dashboard").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/api/**").hasAnyRole("ADMIN", "USER") // 👉 API protegida
                        .anyRequest().authenticated()
                )
                .headers(h -> h.frameOptions(f -> f.disable()))
                // 👇 Aquí mantienes el login web
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .permitAll()
                )
                // 👇 Y añades soporte a Basic Auth (para Postman)
                .httpBasic(Customizer.withDefaults())
                .logout(logout -> logout.permitAll());

        return http.build();
    }

}
