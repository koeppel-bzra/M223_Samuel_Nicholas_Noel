package ch.glauser.serviceauftrag.dto;

import ch.glauser.serviceauftrag.model.Arbeitstyp;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

/**
 * Eingabedaten zum Erfassen eines Auftrags (Kundendaten + Auftragsdaten).
 */
public record AuftragErstellenRequest(
        @NotBlank String kundeName,
        String strasse,
        String plz,
        String ort,
        String telefon,
        String natel,
        String objektAdresse,
        LocalDate datum,
        LocalTime zeit,
        Set<Arbeitstyp> arbeiten,
        @NotBlank String beschreibung,
        String terminwunsch
) {
}
