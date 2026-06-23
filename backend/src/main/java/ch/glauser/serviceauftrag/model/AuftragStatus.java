package ch.glauser.serviceauftrag.model;

/**
 * Lebenszyklus eines Auftrags (siehe Zustandsdiagramm).
 * Reihenfolge: ERFASST -> DISPONIERT -> AUSGEFUEHRT -> FREIGEGEBEN -> VERRECHNET
 */
public enum AuftragStatus {
    ERFASST,
    DISPONIERT,
    AUSGEFUEHRT,
    FREIGEGEBEN,
    VERRECHNET
}
