package ch.glauser.serviceauftrag.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring-Security-Konfiguration: rollenbasierter Zugriffsschutz pro Endpoint.
 *
 * Rollen-Zuordnung gemäss Auftrags-Ablauf:
 *  - erfassen, verrechnen        -> ADMIN
 *  - disponieren, freigeben      -> BEREICHSLEITER
 *  - ausführen                   -> MITARBEITER
 *  - Listen / Detail ansehen     -> jeder angemeldete Benutzer
 *
 * Hinweis: CSRF ist für diese JSON-/SPA-API bewusst deaktiviert. Die
 * Authentifizierung läuft über eine Session (Cookie) bzw. HTTP-Basic (Tests).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // öffentlich
                        .requestMatchers("/api/auth/**", "/api/ping").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        // rollengeschützte Aktionen
                        .requestMatchers(HttpMethod.POST, "/api/auftraege").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/auftraege/*/disponieren").hasRole("BEREICHSLEITER")
                        .requestMatchers(HttpMethod.PATCH, "/api/auftraege/*/ausfuehren").hasRole("MITARBEITER")
                        .requestMatchers(HttpMethod.PATCH, "/api/auftraege/*/freigeben").hasRole("BEREICHSLEITER")
                        .requestMatchers(HttpMethod.PATCH, "/api/auftraege/*/verrechnen").hasRole("ADMIN")
                        // alles Übrige (Listen, Detail, Benutzerliste) nur angemeldet
                        .anyRequest().authenticated()
                )
                // H2-Konsole nutzt Frames
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                // HTTP-Basic erlaubt einfaches Testen via curl/IntelliJ und liefert 401 statt Login-Seite
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
