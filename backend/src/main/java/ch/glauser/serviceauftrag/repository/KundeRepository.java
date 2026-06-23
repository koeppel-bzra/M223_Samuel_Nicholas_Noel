package ch.glauser.serviceauftrag.repository;

import ch.glauser.serviceauftrag.model.Kunde;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Datenzugriff für Kunden. Spring Data JPA liefert CRUD-Methoden automatisch.
 */
public interface KundeRepository extends JpaRepository<Kunde, Long> {
}
