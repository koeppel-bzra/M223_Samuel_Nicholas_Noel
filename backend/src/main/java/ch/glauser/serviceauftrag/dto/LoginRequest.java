package ch.glauser.serviceauftrag.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Eingabedaten für das Login.
 */
public record LoginRequest(
        @NotBlank String benutzername,
        @NotBlank String passwort
) {
}
