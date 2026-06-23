package ch.glauser.serviceauftrag.security;

import ch.glauser.serviceauftrag.model.Benutzer;
import ch.glauser.serviceauftrag.repository.BenutzerRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Lädt einen Benutzer für Spring Security aus der Datenbank.
 * Die Rolle wird zur Spring-Authority "ROLE_<ROLLE>" (z. B. ROLE_ADMIN).
 */
@Service
public class BenutzerDetailsService implements UserDetailsService {

    private final BenutzerRepository benutzerRepository;

    public BenutzerDetailsService(BenutzerRepository benutzerRepository) {
        this.benutzerRepository = benutzerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String benutzername) throws UsernameNotFoundException {
        Benutzer benutzer = benutzerRepository.findByBenutzername(benutzername)
                .orElseThrow(() -> new UsernameNotFoundException("Benutzer nicht gefunden: " + benutzername));

        return User.withUsername(benutzer.getBenutzername())
                .password(benutzer.getPasswort())
                .authorities("ROLE_" + benutzer.getRolle().name())
                .build();
    }
}
