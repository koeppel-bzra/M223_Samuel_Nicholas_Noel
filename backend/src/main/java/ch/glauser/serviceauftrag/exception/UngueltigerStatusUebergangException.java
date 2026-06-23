package ch.glauser.serviceauftrag.exception;

import ch.glauser.serviceauftrag.model.AuftragStatus;

/**
 * Wird geworfen, wenn ein Statuswechsel nicht erlaubt ist (z. B. einen Auftrag
 * verrechnen, der noch nicht freigegeben wurde). Führt zu HTTP 409 (Conflict).
 */
public class UngueltigerStatusUebergangException extends RuntimeException {

    public UngueltigerStatusUebergangException(AuftragStatus aktuell, AuftragStatus ziel, AuftragStatus erwartet) {
        super("Übergang nach " + ziel + " nicht möglich: Auftrag ist im Status " + aktuell
                + " (erwartet: " + erwartet + ").");
    }
}
