package ch.glauser.serviceauftrag.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * Eingabedaten zum Disponieren: zuzuweisender Mitarbeiter und optionaler Termin.
 */
public record DisponierenRequest(
        @NotNull Long mitarbeiterId,
        LocalDate geplanterTermin
) {
}
