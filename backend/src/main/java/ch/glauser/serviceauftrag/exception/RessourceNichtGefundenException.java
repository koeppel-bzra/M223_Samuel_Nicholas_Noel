package ch.glauser.serviceauftrag.exception;

/**
 * Wird geworfen, wenn eine angeforderte Entität (z. B. Auftrag, Benutzer)
 * nicht existiert. Führt zu HTTP 404.
 */
public class RessourceNichtGefundenException extends RuntimeException {

    public RessourceNichtGefundenException(String typ, Long id) {
        super(typ + " mit ID " + id + " wurde nicht gefunden.");
    }
}
