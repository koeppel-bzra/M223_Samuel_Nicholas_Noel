package ch.glauser.serviceauftrag.pdf;

import ch.glauser.serviceauftrag.model.Arbeitstyp;
import ch.glauser.serviceauftrag.model.Auftrag;
import ch.glauser.serviceauftrag.model.AuftragStatus;
import ch.glauser.serviceauftrag.model.Kunde;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testet die PDF-Erzeugung des Auftragsdokuments. Geprüft wird, dass ein
 * nicht-leeres PDF mit gültiger Signatur entsteht – auch wenn (wie im Status
 * ERFASST) Mitarbeiter, Termin und Rapport noch nicht gesetzt sind.
 */
class AuftragPdfServiceTest {

    private final AuftragPdfService service = new AuftragPdfService();

    @Test
    void erzeugtGueltigesPdf() {
        Kunde kunde = new Kunde();
        kunde.setName("Beatrice Brandenberger");
        kunde.setStrasse("Obderdorfstrasse 11");
        kunde.setPlz("8484");
        kunde.setOrt("Weisslingen");
        kunde.setTelefon("098 765 43 21");

        Auftrag auftrag = new Auftrag();
        auftrag.setId(12L);
        auftrag.setStatus(AuftragStatus.ERFASST);
        auftrag.setKunde(kunde);
        auftrag.setObjektAdresse("dito");
        auftrag.setArbeiten(Set.of(Arbeitstyp.REPARATUR, Arbeitstyp.SANITAER));
        auftrag.setBeschreibung("Wasserhahn in der Küche tropft");
        auftrag.setTerminwunsch("so schnell wie möglich");

        byte[] pdf = service.erzeugeAuftragsdokument(auftrag);

        assertTrue(pdf.length > 0, "PDF darf nicht leer sein");
        String signatur = new String(pdf, 0, 4, StandardCharsets.US_ASCII);
        assertTrue(signatur.equals("%PDF"), "PDF muss mit der Signatur %PDF beginnen, war: " + signatur);
    }
}
