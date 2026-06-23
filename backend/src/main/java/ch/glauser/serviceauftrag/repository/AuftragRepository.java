package ch.glauser.serviceauftrag.repository;

import ch.glauser.serviceauftrag.model.Auftrag;
import ch.glauser.serviceauftrag.model.AuftragStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Datenzugriff für Aufträge. Enthält Finder für die "Liste nach Zuständen".
 */
public interface AuftragRepository extends JpaRepository<Auftrag, Long> {

    List<Auftrag> findByStatusOrderByErstelltAmDesc(AuftragStatus status);

    List<Auftrag> findAllByOrderByErstelltAmDesc();
}
