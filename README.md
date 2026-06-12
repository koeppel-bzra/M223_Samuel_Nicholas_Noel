# Service Auftrag (M223)

Webanwendung für den Serviceauftrags-Ablauf der **Glauser Illnau AG**
(Sanitärunternehmen). Ablauf:

`ERFASST → DISPONIERT → AUSGEFÜHRT → FREIGEGEBEN → VERRECHNET`

mit den Rollen **Admin/GL**, **Bereichsleiter** und **Mitarbeiter**.

> Stand: **Projekt-Gerüst**. Die Geschäftslogik wird Schritt für Schritt selbst programmiert.

## Technologien
- **Backend:** Java 25, Spring Boot 4.1, Spring Data JPA, Spring Security, Maven
- **DB:** H2 (eingebettet, Gerüst-Default) → später PostgreSQL bei Supabase
- **Frontend:** React (Vite), React Router, axios

## Projektstruktur
```
backend/    Spring-Boot-REST-API (Maven, mit ./mvnw)
frontend/   React-SPA (Vite)
```

## Starten

### Backend (Port 8080)
```bash
cd backend
./mvnw spring-boot:run
```
Test der Verbindung: http://localhost:8080/api/ping → `{"status":"ok",...}`
H2-Konsole: http://localhost:8080/h2-console (JDBC-URL `jdbc:h2:mem:serviceauftrag`, User `sa`)

### Frontend (Port 5173)
```bash
cd frontend
npm install
npm run dev
```
Öffnet http://localhost:5173 — die Startseite prüft automatisch, ob das Backend läuft
(`/api` wird per Vite-Proxy ans Backend weitergeleitet).

### Tests (Backend)
```bash
cd backend
./mvnw test
```

## Auf Supabase (PostgreSQL) umstellen
In `backend/src/main/resources/application.properties` den H2-Block auskommentieren
und den PostgreSQL-/Supabase-Block aktivieren (Connection-Daten aus dem Supabase-Dashboard).

## Nächste Schritte (selbst umsetzen)
1. Entities + Enums in `model/` (Auftrag, Kunde, Benutzer; Status-/Rollen-Enums)
2. Repositories in `repository/`
3. `AuftragService` mit Statusübergangs- und Rollenlogik
4. REST-Controller + DTOs
5. `SecurityConfig` durch echtes Login + Rollen ersetzen (aktuell offen/permitAll)
6. PDF-Service für das Auftragsblatt
7. React-Seiten mit echter Logik füllen
8. `schema.sql` / `data.sql` befüllen und in den Properties aktivieren
