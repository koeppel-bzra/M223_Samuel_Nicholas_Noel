package ch.glauser.serviceauftrag.controller;

import ch.glauser.serviceauftrag.dto.BenutzerResponse;
import ch.glauser.serviceauftrag.model.Benutzer;
import ch.glauser.serviceauftrag.model.Rolle;
import ch.glauser.serviceauftrag.service.BenutzerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST-Endpoints für Benutzer. Wird u. a. fürs Disponieren gebraucht
 * (Auswahl der Mitarbeiter: GET /api/benutzer?rolle=MITARBEITER).
 */
@RestController
@RequestMapping("/api/benutzer")
public class BenutzerController {

    private final BenutzerService benutzerService;

    public BenutzerController(BenutzerService benutzerService) {
        this.benutzerService = benutzerService;
    }

    @GetMapping
    public List<BenutzerResponse> liste(@RequestParam(required = false) Rolle rolle) {
        List<Benutzer> benutzer = (rolle == null)
                ? benutzerService.findeAlle()
                : benutzerService.findeNachRolle(rolle);
        return benutzer.stream().map(BenutzerResponse::von).toList();
    }
}
