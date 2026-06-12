package ch.glauser.serviceauftrag.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * VORLAEUFIGE Security-Konfiguration fuer das Geruest.
 *
 * Aktuell ist alles offen (permitAll) und CSRF ist aus, damit das
 * Backend ohne Login getestet werden kann.
 *
 * TODO (selbst umsetzen): echtes Login + Rollen (ADMIN / BEREICHSLEITER
 * / MITARBEITER), Zugriffsschutz pro Endpoint, CSRF/CORS sauber setzen.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}
