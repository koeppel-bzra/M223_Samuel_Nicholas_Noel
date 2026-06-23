package ch.glauser.serviceauftrag.model;

/**
 * Art der auszuführenden Arbeiten (entspricht den Checkboxen auf dem
 * Papier-Auftragsblatt). Ein Auftrag kann mehrere Arbeitstypen haben.
 */
public enum Arbeitstyp {
    REPARATUR,
    SANITAER,
    HEIZUNG,
    GARANTIE
}
