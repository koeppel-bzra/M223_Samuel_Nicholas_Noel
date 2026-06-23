package ch.glauser.serviceauftrag.config;

import ch.glauser.serviceauftrag.model.Benutzer;
import ch.glauser.serviceauftrag.model.Rolle;
import ch.glauser.serviceauftrag.repository.BenutzerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Legt beim Start Demo-Benutzer an (einen pro Rolle), falls die Tabelle leer ist.
 * Die Passwörter werden mit BCrypt gehasht. Login jeweils: Benutzername = Passwort.
 *   admin / admin   |   bereichsleiter / bereichsleiter   |   mitarbeiter / mitarbeiter
 *
 * Hinweis: Dieser Seeder dient der lokalen H2-Datenbank. Für Supabase/PostgreSQL
 * werden Schema und Datensätze über schema.sql + data.sql eingespielt (data.sql
 * nutzt PostgreSQL-spezifische Funktionen und läuft daher nicht auf H2).
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final BenutzerRepository benutzerRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(BenutzerRepository benutzerRepository, PasswordEncoder passwordEncoder) {
        this.benutzerRepository = benutzerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (benutzerRepository.count() > 0) {
            return;
        }
        benutzerRepository.save(neu("admin", "Administrator", Rolle.ADMIN));
        benutzerRepository.save(neu("bereichsleiter", "Bea Bereichsleiter", Rolle.BEREICHSLEITER));
        benutzerRepository.save(neu("mitarbeiter", "Rudolf Rutishauser", Rolle.MITARBEITER));
    }

    private Benutzer neu(String benutzername, String name, Rolle rolle) {
        Benutzer b = new Benutzer();
        b.setBenutzername(benutzername);
        b.setName(name);
        b.setPasswort(passwordEncoder.encode(benutzername)); // Passwort = Benutzername (gehasht)
        b.setRolle(rolle);
        return b;
    }
}
