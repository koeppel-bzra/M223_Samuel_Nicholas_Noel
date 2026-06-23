package ch.glauser.serviceauftrag.dto;

import ch.glauser.serviceauftrag.model.Arbeitstyp;
import ch.glauser.serviceauftrag.model.Auftrag;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

/**
 * Ausgabedaten eines Auftrags (flache Sicht, damit keine Entities nach aussen
 * gelangen). Wird über {@link #von(Auftrag)} aus der Entität erzeugt.
 */
public record AuftragResponse(
        Long id,
        String status,
        String kundeName,
        String objektAdresse,
        Set<Arbeitstyp> arbeiten,
        String beschreibung,
        String terminwunsch,
        LocalDate datum,
        LocalTime zeit,
        Long mitarbeiterId,
        String mitarbeiterName,
        LocalDate geplanterTermin,
        String rapportText,
        LocalDateTime erstelltAm
) {
    public static AuftragResponse von(Auftrag a) {
        return new AuftragResponse(
                a.getId(),
                a.getStatus() != null ? a.getStatus().name() : null,
                a.getKunde() != null ? a.getKunde().getName() : null,
                a.getObjektAdresse(),
                a.getArbeiten(),
                a.getBeschreibung(),
                a.getTerminwunsch(),
                a.getDatum(),
                a.getZeit(),
                a.getZugewiesenerMitarbeiter() != null ? a.getZugewiesenerMitarbeiter().getId() : null,
                a.getZugewiesenerMitarbeiter() != null ? a.getZugewiesenerMitarbeiter().getName() : null,
                a.getGeplanterTermin(),
                a.getRapportText(),
                a.getErstelltAm()
        );
    }
}
