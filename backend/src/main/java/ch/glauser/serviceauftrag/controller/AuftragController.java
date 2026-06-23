package ch.glauser.serviceauftrag.controller;

import ch.glauser.serviceauftrag.dto.AuftragErstellenRequest;
import ch.glauser.serviceauftrag.dto.AuftragResponse;
import ch.glauser.serviceauftrag.dto.AusfuehrenRequest;
import ch.glauser.serviceauftrag.dto.DisponierenRequest;
import ch.glauser.serviceauftrag.model.Arbeitstyp;
import ch.glauser.serviceauftrag.model.Auftrag;
import ch.glauser.serviceauftrag.model.AuftragStatus;
import ch.glauser.serviceauftrag.model.Kunde;
import ch.glauser.serviceauftrag.pdf.AuftragPdfService;
import ch.glauser.serviceauftrag.service.AuftragService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;

/**
 * REST-Endpoints für den Auftrags-Workflow. Deckt die Anforderungen ab:
 * erfassen, Liste nach Zuständen, disponieren, ausführen, freigeben, verrechnen.
 */
@RestController
@RequestMapping("/api/auftraege")
public class AuftragController {

    private final AuftragService auftragService;
    private final AuftragPdfService auftragPdfService;

    public AuftragController(AuftragService auftragService, AuftragPdfService auftragPdfService) {
        this.auftragService = auftragService;
        this.auftragPdfService = auftragPdfService;
    }

    /** Auftrag erfassen. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuftragResponse erfassen(@Valid @RequestBody AuftragErstellenRequest req) {
        Kunde kunde = new Kunde();
        kunde.setName(req.kundeName());
        kunde.setStrasse(req.strasse());
        kunde.setPlz(req.plz());
        kunde.setOrt(req.ort());
        kunde.setTelefon(req.telefon());
        kunde.setNatel(req.natel());

        Auftrag auftrag = new Auftrag();
        auftrag.setDatum(req.datum());
        auftrag.setZeit(req.zeit());
        auftrag.setObjektAdresse(req.objektAdresse());
        auftrag.setBeschreibung(req.beschreibung());
        auftrag.setTerminwunsch(req.terminwunsch());
        if (req.arbeiten() != null) {
            auftrag.setArbeiten(new HashSet<>(req.arbeiten()));
        }

        return AuftragResponse.von(auftragService.erfassen(auftrag, kunde));
    }

    /** Liste aller Aufträge, optional nach Status gefiltert. */
    @GetMapping
    public List<AuftragResponse> liste(@RequestParam(required = false) AuftragStatus status) {
        List<Auftrag> auftraege = (status == null)
                ? auftragService.findeAlle()
                : auftragService.findeNachStatus(status);
        return auftraege.stream().map(AuftragResponse::von).toList();
    }

    /** Einzelnen Auftrag abrufen. */
    @GetMapping("/{id}")
    public AuftragResponse detail(@PathVariable Long id) {
        return AuftragResponse.von(auftragService.findeById(id));
    }

    /** Auftragsdokument als PDF herunterladen (druckbares Auftragsblatt). */
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> pdf(@PathVariable Long id) {
        byte[] pdf = auftragPdfService.erzeugeAuftragsdokument(auftragService.findeById(id));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"auftrag-" + id + ".pdf\"")
                .body(pdf);
    }

    /** Auftrag disponieren (Mitarbeiter + Termin). */
    @PatchMapping("/{id}/disponieren")
    public AuftragResponse disponieren(@PathVariable Long id, @Valid @RequestBody DisponierenRequest req) {
        return AuftragResponse.von(
                auftragService.disponieren(id, req.mitarbeiterId(), req.geplanterTermin()));
    }

    /** Auftrag als ausgeführt markieren (mit Rapport). */
    @PatchMapping("/{id}/ausfuehren")
    public AuftragResponse ausfuehren(@PathVariable Long id, @Valid @RequestBody AusfuehrenRequest req) {
        return AuftragResponse.von(auftragService.alsAusgefuehrtMarkieren(id, req.rapportText()));
    }

    /** Rapport prüfen und zur Verrechnung freigeben. */
    @PatchMapping("/{id}/freigeben")
    public AuftragResponse freigeben(@PathVariable Long id) {
        return AuftragResponse.von(auftragService.freigeben(id));
    }

    /** Auftrag als verrechnet markieren. */
    @PatchMapping("/{id}/verrechnen")
    public AuftragResponse verrechnen(@PathVariable Long id) {
        return AuftragResponse.von(auftragService.alsVerrechnetMarkieren(id));
    }
}
