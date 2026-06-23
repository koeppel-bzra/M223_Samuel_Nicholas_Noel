package ch.glauser.serviceauftrag.service;

import ch.glauser.serviceauftrag.exception.UngueltigerStatusUebergangException;
import ch.glauser.serviceauftrag.model.Arbeitstyp;
import ch.glauser.serviceauftrag.model.Auftrag;
import ch.glauser.serviceauftrag.model.AuftragStatus;
import ch.glauser.serviceauftrag.model.Benutzer;
import ch.glauser.serviceauftrag.model.Kunde;
import ch.glauser.serviceauftrag.model.Rolle;
import ch.glauser.serviceauftrag.repository.BenutzerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testet die Statusübergangs-Logik des AuftragService (Zustandsdiagramm).
 */
@SpringBootTest
class AuftragServiceTest {

    @Autowired
    private AuftragService auftragService;
    @Autowired
    private BenutzerRepository benutzerRepository;

    @Test
    void durchlaeuftDenGesamtenWorkflow() {
        Kunde kunde = new Kunde();
        kunde.setName("Brandenberger");

        Auftrag auftrag = new Auftrag();
        auftrag.setBeschreibung("Wasserhahn tropft");
        auftrag.setArbeiten(Set.of(Arbeitstyp.REPARATUR, Arbeitstyp.SANITAER));

        Auftrag erfasst = auftragService.erfassen(auftrag, kunde);
        assertEquals(AuftragStatus.ERFASST, erfasst.getStatus());
        Long id = erfasst.getId();

        Benutzer mitarbeiter = new Benutzer();
        mitarbeiter.setBenutzername("ma_" + System.nanoTime());
        mitarbeiter.setRolle(Rolle.MITARBEITER);
        mitarbeiter = benutzerRepository.save(mitarbeiter);

        Auftrag disponiert = auftragService.disponieren(id, mitarbeiter.getId(), LocalDate.now());
        assertEquals(AuftragStatus.DISPONIERT, disponiert.getStatus());
        assertEquals(mitarbeiter.getId(), disponiert.getZugewiesenerMitarbeiter().getId());

        Auftrag ausgefuehrt = auftragService.alsAusgefuehrtMarkieren(id, "erledigt, Dichtung ersetzt");
        assertEquals(AuftragStatus.AUSGEFUEHRT, ausgefuehrt.getStatus());

        Auftrag freigegeben = auftragService.freigeben(id);
        assertEquals(AuftragStatus.FREIGEGEBEN, freigegeben.getStatus());

        Auftrag verrechnet = auftragService.alsVerrechnetMarkieren(id);
        assertEquals(AuftragStatus.VERRECHNET, verrechnet.getStatus());
    }

    @Test
    void verbietetUngueltigenUebergang() {
        Kunde kunde = new Kunde();
        kunde.setName("Müller");
        Auftrag auftrag = new Auftrag();
        auftrag.setBeschreibung("Heizung prüfen");

        Auftrag erfasst = auftragService.erfassen(auftrag, kunde);

        // direkt verrechnen (ohne disponieren/ausführen/freigeben) muss scheitern
        assertThrows(UngueltigerStatusUebergangException.class,
                () -> auftragService.alsVerrechnetMarkieren(erfasst.getId()));
    }
}
