package ch.glauser.serviceauftrag.dto;

import ch.glauser.serviceauftrag.model.Benutzer;
import ch.glauser.serviceauftrag.model.Rolle;

/**
 * Ausgabedaten eines Benutzers (ohne Passwort).
 */
public record BenutzerResponse(
        Long id,
        String benutzername,
        String name,
        Rolle rolle
) {
    public static BenutzerResponse von(Benutzer b) {
        return new BenutzerResponse(b.getId(), b.getBenutzername(), b.getName(), b.getRolle());
    }
}
