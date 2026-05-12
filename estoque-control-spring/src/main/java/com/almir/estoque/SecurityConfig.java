package com.almir.estoque;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                        // Páginas que NÃO exigem login
                        .requestMatchers(
                                "/",                 // raiz
                                "/login",            // tela de login
                                "/setup-admin",      // tela para criar o primeiro admin
                                "/access-denied",    // página de acesso negado
                                "/h2-console/**",    // console H2
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll()

                        // Somente ADMIN pode gerenciar usuários
                        .requestMatchers("/users/**").hasRole("ADMIN")

                        // Home e produtos: qualquer usuário autenticado (ADMIN ou USER)
                        .requestMatchers("/home", "/products/**").authenticated()

                        // Qualquer outra rota exige login
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/home", true) // depois de logar, manda para /home
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/access-denied")
                )
                // CSRF desabilitado para simplificar (e permitir logout via GET)
                .csrf(csrf -> csrf.disable())
                // Necessário para o H2 console funcionar em iframe
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
