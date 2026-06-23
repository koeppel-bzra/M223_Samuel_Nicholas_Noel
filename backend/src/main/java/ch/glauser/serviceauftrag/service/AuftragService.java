package ch.glauser.serviceauftrag.service;

import ch.glauser.serviceauftrag.exception.RessourceNichtGefundenException;
import ch.glauser.serviceauftrag.exception.UngueltigerStatusUebergangException;
import ch.glauser.serviceauftrag.model.Auftrag;
import ch.glauser.serviceauftrag.model.AuftragStatus;
import ch.glauser.serviceauftrag.model.Benutzer;
import ch.glauser.serviceauftrag.model.Kunde;
import ch.glauser.serviceauftrag.repository.AuftragRepository;
import ch.glauser.serviceauftrag.repository.BenutzerRepository;
import ch.glauser.serviceauftrag.repository.KundeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Businesslogik rund um den Auftrag. Hier wird der Lebenszyklus
 * (Zustandsdiagramm) durchgesetzt: ein Statuswechsel ist nur aus dem
 * jeweils korrekten Vorgängerstatus erlaubt.
 */
@Service
public class AuftragService {

    private final AuftragRepository auftragRepository;
    private final KundeRepository kundeRepository;
    private final BenutzerRepository benutzerRepository;

    public AuftragService(AuftragRepository auftragRepository,
                          KundeRepository kundeRepository,
                          BenutzerRepository benutzerRepository) {
        this.auftragRepository = auftragRepository;
        this.kundeRepository = kundeRepository;
        this.benutzerRepository = benutzerRepository;
    }

    // ---------- Lesen ----------

    public List<Auftrag> findeAlle() {
        return auftragRepository.findAllByOrderByErstelltAmDesc();
    }

    public List<Auftrag> findeNachStatus(AuftragStatus status) {
        return auftragRepository.findByStatusOrderByErstelltAmDesc(status);
    }

    public Auftrag findeById(Long id) {
        return auftragRepository.findById(id)
                .orElseThrow(() -> new RessourceNichtGefundenException("Auftrag", id));
    }

    // ---------- Schritt 1: Erfassen ----------

    /** Nimmt einen Auftrag an. Der Kunde wird mitgespeichert. Status -> ERFASST. */
    @Transactional
    public Auftrag erfassen(Auftrag auftrag, Kunde kunde) {
        Kunde gespeicherterKunde = kundeRepository.save(kunde);
        auftrag.setKunde(gespeicherterKunde);
        auftrag.setStatus(AuftragStatus.ERFASST);
        return auftragRepository.save(auftrag);
    }

    // ---------- Schritt 2: Disponieren ----------

    /** Weist einen Mitarbeiter zu und setzt optional den Termin. ERFASST -> DISPONIERT. */
    @Transactional
    public Auftrag disponieren(Long auftragId, Long mitarbeiterId, LocalDate geplanterTermin) {
        Auftrag auftrag = findeById(auftragId);
        pruefeUebergang(auftrag, AuftragStatus.ERFASST, AuftragStatus.DISPONIERT);

        Benutzer mitarbeiter = benutzerRepository.findById(mitarbeiterId)
                .orElseThrow(() -> new RessourceNichtGefundenException("Benutzer", mitarbeiterId));

        auftrag.setZugewiesenerMitarbeiter(mitarbeiter);
        auftrag.setGeplanterTermin(geplanterTermin);
        auftrag.setStatus(AuftragStatus.DISPONIERT);
        return auftragRepository.save(auftrag);
    }

    // ---------- Schritt 3: Ausführen / Rapportieren ----------

    /** Markiert als ausgeführt und speichert den Rapport. DISPONIERT -> AUSGEFUEHRT. */
    @Transactional
    public Auftrag alsAusgefuehrtMarkieren(Long auftragId, String rapportText) {
        Auftrag auftrag = findeById(auftragId);
        pruefeUebergang(auftrag, AuftragStatus.DISPONIERT, AuftragStatus.AUSGEFUEHRT);

        auftrag.setRapportText(rapportText);
        auftrag.setStatus(AuftragStatus.AUSGEFUEHRT);
        return auftragRepository.save(auftrag);
    }

    // ---------- Schritt 5: Rapport prüfen & freigeben ----------

    /** Gibt den Rapport zur Verrechnung frei. AUSGEFUEHRT -> FREIGEGEBEN. */
    @Transactional
    public Auftrag freigeben(Long auftragId) {
        Auftrag auftrag = findeById(auftragId);
        pruefeUebergang(auftrag, AuftragStatus.AUSGEFUEHRT, AuftragStatus.FREIGEGEBEN);

        auftrag.setStatus(AuftragStatus.FREIGEGEBEN);
        return auftragRepository.save(auftrag);
    }

    // ---------- Schritt 6: Verrechnen ----------

    /** Markiert den Auftrag als verrechnet. FREIGEGEBEN -> VERRECHNET. */
    @Transactional
    public Auftrag alsVerrechnetMarkieren(Long auftragId) {
        Auftrag auftrag = findeById(auftragId);
        pruefeUebergang(auftrag, AuftragStatus.FREIGEGEBEN, AuftragStatus.VERRECHNET);

        auftrag.setStatus(AuftragStatus.VERRECHNET);
        return auftragRepository.save(auftrag);
    }

    // ---------- Hilfsmethode: Statusübergang prüfen ----------

    private void pruefeUebergang(Auftrag auftrag, AuftragStatus erwarteterStatus, AuftragStatus zielStatus) {
        if (auftrag.getStatus() != erwarteterStatus) {
            throw new UngueltigerStatusUebergangException(auftrag.getStatus(), zielStatus, erwarteterStatus);
        }
    }
}
