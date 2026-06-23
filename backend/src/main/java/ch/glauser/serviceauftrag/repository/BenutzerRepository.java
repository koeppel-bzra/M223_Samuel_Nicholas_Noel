package ch.glauser.serviceauftrag.repository;

import ch.glauser.serviceauftrag.model.Benutzer;
import ch.glauser.serviceauftrag.model.Rolle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Datenzugriff für Benutzer.
 */
public interface BenutzerRepository extends JpaRepository<Benutzer, Long> {

    Optional<Benutzer> findByBenutzername(String benutzername);

    List<Benutzer> findByRolle(Rolle rolle);
}
