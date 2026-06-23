// Werte gemäss Backend-Enums (AuftragStatus, Arbeitstyp).
export const STATUS = ['ERFASST', 'DISPONIERT', 'AUSGEFUEHRT', 'FREIGEGEBEN', 'VERRECHNET']

export const ARBEITSTYPEN = ['REPARATUR', 'SANITAER', 'HEIZUNG', 'GARANTIE']

// Pro Status: was als Nächstes passiert und welche Rolle das auslöst.
// Dient als Hinweis im Detail, auch wenn der angemeldete Benutzer nicht zuständig ist.
export const NAECHSTER_SCHRITT = {
  ERFASST: 'Disponieren (Bereichsleiter)',
  DISPONIERT: 'Ausführen & rapportieren (Mitarbeiter)',
  AUSGEFUEHRT: 'Rapport freigeben (Bereichsleiter)',
  FREIGEGEBEN: 'Verrechnen (Administration)',
  VERRECHNET: 'Abgeschlossen',
}
