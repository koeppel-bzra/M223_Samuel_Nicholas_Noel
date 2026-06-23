-- =====================================================================
-- data.sql  --  Testdatensaetze (PostgreSQL / Supabase)
-- ---------------------------------------------------------------------
-- Nach schema.sql ausfuehren. Passwoerter sind BCrypt-Hashes;
-- Login jeweils Benutzername = Passwort (admin/admin usw.).
-- =====================================================================

-- Benutzer (ein Konto pro Rolle) ---------------------------------------
INSERT INTO benutzer (id, benutzername, passwort, name, rolle) VALUES
  (1, 'admin',          '$2a$10$dJHiZIFrVsHwgHqRc1WtKOfK7h16AVNKerDSj0Ot1vLjSefQkry92', 'Administrator',       'ADMIN'),
  (2, 'bereichsleiter', '$2a$10$cmtO6iKOupUtuzRSOsg08.kI8Mnyc2bCfVYhUEq7Bb50OuOfMXZOO', 'Bea Bereichsleiter',  'BEREICHSLEITER'),
  (3, 'mitarbeiter',    '$2a$10$ARUC5yo5c6mESWRyOpXgcOTDLg4v/6GT8h5Mf79OklLhSH85scjtS', 'Rudolf Rutishauser',  'MITARBEITER');

-- Kunden ----------------------------------------------------------------
INSERT INTO kunde (id, name, strasse, plz, ort, telefon, natel) VALUES
  (1, 'Beatrice Brandenberger', 'Obderdorfstrasse 11', '8484', 'Weisslingen', '098 765 43 21', '079 797 97 97'),
  (2, 'Hans Müller',            'Bahnhofstrasse 3',     '8400', 'Winterthur',  '052 222 11 00', NULL);

-- Auftraege (verschiedene Zustaende) -----------------------------------
INSERT INTO auftrag (id, datum, zeit, status, beschreibung, terminwunsch, objekt_adresse, geplanter_termin, rapport_text, erstellt_am, kunde_id, benutzer_id) VALUES
  (1, DATE '2026-06-10', TIME '10:00', 'ERFASST',    'Wasserhahn in der Küche tropft', 'so schnell wie möglich', 'dito', NULL,               NULL,                       TIMESTAMP '2026-06-10 10:00:00', 1, NULL),
  (2, DATE '2026-06-11', TIME '08:30', 'DISPONIERT', 'Heizung jährliche Wartung',      'nächste Woche',          NULL,   DATE '2026-06-20',  NULL,                       TIMESTAMP '2026-06-11 08:30:00', 2, 3),
  (3, DATE '2026-06-05', TIME '14:00', 'VERRECHNET', 'Rohrbruch im Keller behoben',    'sofort',                 'dito', DATE '2026-06-05',  'Rohr ersetzt, dicht.',     TIMESTAMP '2026-06-05 14:00:00', 1, 3);

-- Arbeitstypen je Auftrag (n:m) ----------------------------------------
INSERT INTO auftrag_arbeitstyp (auftrag_id, arbeitstyp) VALUES
  (1, 'REPARATUR'),
  (1, 'SANITAER'),
  (2, 'HEIZUNG'),
  (3, 'SANITAER');

-- Identity-Sequenzen hinter die manuell vergebenen IDs setzen,
-- damit die Anwendung kollisionsfrei neue Datensaetze anlegen kann.
SELECT setval(pg_get_serial_sequence('benutzer', 'id'), (SELECT MAX(id) FROM benutzer));
SELECT setval(pg_get_serial_sequence('kunde',    'id'), (SELECT MAX(id) FROM kunde));
SELECT setval(pg_get_serial_sequence('auftrag',  'id'), (SELECT MAX(id) FROM auftrag));
