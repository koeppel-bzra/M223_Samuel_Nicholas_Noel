package ch.glauser.serviceauftrag.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Eingabedaten beim Markieren als ausgeführt: der Rapport-Text.
 */
public record AusfuehrenRequest(
        @NotBlank String rapportText
) {
}
