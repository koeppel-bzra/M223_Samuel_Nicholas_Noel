# Service Auftrag (M223)

Webanwendung für den Serviceauftrags-Ablauf der **Glauser Illnau AG** (Sanitärunternehmen).
Ein Auftrag durchläuft den Lebenszyklus

`ERFASST → DISPONIERT → AUSGEFUEHRT → FREIGEGEBEN → VERRECHNET`

mit drei Rollen: **Administration/GL**, **Bereichsleiter** und **Mitarbeiter**. Jeder
Statuswechsel ist nur aus dem korrekten Vorgängerstatus und nur durch die zuständige Rolle erlaubt.

## Funktionen
- Auftrag erfassen (Kunde + Auftragsdaten)
- Auftragsliste mit Filter nach Status
- Auftrag disponieren (Mitarbeiter + Termin zuweisen)
- Auftrag als ausgeführt markieren (mit Rapport)
- Rapport freigeben und Auftrag verrechnen
- Auftragsdokument als **PDF** herunterladen
- Login mit rollenbasiertem Zugriffsschutz

## Technologien
- **Backend:** Java 25, Spring Boot 4.1, Spring Data JPA (Hibernate), Spring Security, OpenPDF, Maven
- **Datenbank:** H2 (eingebettet, Standard) · PostgreSQL bei Supabase (Profil `supabase`)
- **Frontend:** React 19 (Vite), React Router, axios

## Projektstruktur
```
backend/     Spring-Boot-REST-API (Maven, ./mvnw)
frontend/    React-SPA (Vite)
diagramme/   UML- und Datenmodell-Diagramme
```

## Starten

Backend und Frontend laufen parallel (zwei Terminals).

### 1. Backend (Port 8080)
```bash
cd backend
./mvnw spring-boot:run
```
Läuft standardmässig auf der eingebetteten H2-Datenbank – keine DB-Installation nötig.
Beim Start werden drei Demo-Benutzer angelegt.

### 2. Frontend (Port 5173)
```bash
cd frontend
npm install
npm run dev
```
Öffnet http://localhost:5173. `/api` wird per Vite-Proxy ans Backend weitergeleitet (kein CORS nötig).

### Demo-Logins
| Benutzer | Passwort | Rolle |
|---|---|---|
| `admin` | `admin` | Administration / GL |
| `bereichsleiter` | `bereichsleiter` | Bereichsleiter |
| `mitarbeiter` | `mitarbeiter` | Mitarbeiter |

### Ablauf durchspielen
1. Als `admin` einen Auftrag **erfassen**
2. Als `bereichsleiter` den Auftrag **disponieren**
3. Als `mitarbeiter` **ausführen** und rapportieren
4. Als `bereichsleiter` den Rapport **freigeben**
5. Als `admin` den Auftrag **verrechnen**

In der Detailansicht lässt sich jederzeit das **Auftragsdokument (PDF)** drucken.

## REST-API (Auszug)
| Methode | Pfad | Rolle |
|---|---|---|
| POST | `/api/auth/login` · `/api/auth/logout` · GET `/api/auth/me` | – |
| POST | `/api/auftraege` | Admin |
| GET | `/api/auftraege?status=…` · `/api/auftraege/{id}` | angemeldet |
| GET | `/api/auftraege/{id}/pdf` | angemeldet |
| PATCH | `/api/auftraege/{id}/disponieren` · `/freigeben` | Bereichsleiter |
| PATCH | `/api/auftraege/{id}/ausfuehren` | Mitarbeiter |
| PATCH | `/api/auftraege/{id}/verrechnen` | Admin |

## Tests (Backend)
```bash
cd backend
./mvnw test
```

## PostgreSQL bei Supabase
Schema und Daten einmalig im Supabase-SQL-Editor einspielen
(`backend/src/main/resources/schema.sql`, dann `data.sql`) und das Backend mit dem
Profil `supabase` starten:
```bash
cd backend
SUPABASE_DB_URL="jdbc:postgresql://<host>:5432/postgres" \
SUPABASE_DB_USER="<user>" \
SUPABASE_DB_PASSWORD="<passwort>" \
./mvnw spring-boot:run -Dspring-boot.run.profiles=supabase
```
Die Zugangsdaten stehen im Supabase-Dashboard unter **Project Settings → Database**.
