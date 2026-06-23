package ch.glauser.serviceauftrag.repository;

import ch.glauser.serviceauftrag.model.Arbeitstyp;
import ch.glauser.serviceauftrag.model.Auftrag;
import ch.glauser.serviceauftrag.model.AuftragStatus;
import ch.glauser.serviceauftrag.model.Benutzer;
import ch.glauser.serviceauftrag.model.Kunde;
import ch.glauser.serviceauftrag.model.Rolle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Prüft das JPA-Mapping des Domänenmodells gegen die (eingebettete) Datenbank.
 */
@DataJpaTest
class AuftragRepositoryTest {

    @Autowired
    private KundeRepository kundeRepo;
    @Autowired
    private BenutzerRepository benutzerRepo;
    @Autowired
    private AuftragRepository auftragRepo;

    @Test
    void speichertUndFindetAuftragNachStatus() {
        Kunde k = new Kunde();
        k.setName("Brandenberger");
        k.setOrt("Weisslingen");
        kundeRepo.save(k);

        Benutzer ma = new Benutzer();
        ma.setBenutzername("rrutishauser");
        ma.setName("R. Rutishauser");
        ma.setRolle(Rolle.MITARBEITER);
        benutzerRepo.save(ma);

        Auftrag a = new Auftrag();
        a.setKunde(k);
        a.setDatum(LocalDate.now());
        a.setBeschreibung("Wasserhahn tropft");
        a.setArbeiten(Set.of(Arbeitstyp.REPARATUR, Arbeitstyp.SANITAER));
        auftragRepo.save(a); // Status default ERFASST

        List<Auftrag> erfasst = auftragRepo.findByStatusOrderByErstelltAmDesc(AuftragStatus.ERFASST);
        assertEquals(1, erfasst.size());

        Auftrag gespeichert = erfasst.get(0);
        assertEquals(AuftragStatus.ERFASST, gespeichert.getStatus());
        assertEquals("Brandenberger", gespeichert.getKunde().getName());
        assertEquals(2, gespeichert.getArbeiten().size());
        assertTrue(gespeichert.getArbeiten().contains(Arbeitstyp.REPARATUR));
        assertTrue(gespeichert.getArbeiten().contains(Arbeitstyp.SANITAER));
        assertNotNull(gespeichert.getErstelltAm());
    }

    @Test
    void findetBenutzerNachBenutzername() {
        Benutzer admin = new Benutzer();
        admin.setBenutzername("admin");
        admin.setRolle(Rolle.ADMIN);
        benutzerRepo.save(admin);

        assertTrue(benutzerRepo.findByBenutzername("admin").isPresent());
        assertTrue(benutzerRepo.findByBenutzername("gibtsnicht").isEmpty());
    }
}
