package ch.glauser.serviceauftrag.service;

import ch.glauser.serviceauftrag.exception.RessourceNichtGefundenException;
import ch.glauser.serviceauftrag.model.Benutzer;
import ch.glauser.serviceauftrag.model.Rolle;
import ch.glauser.serviceauftrag.repository.BenutzerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Businesslogik rund um Benutzer. Wird u. a. fürs Disponieren benötigt
 * (Auswahl der Mitarbeiter) und später für das Login (Etappe 4).
 */
@Service
public class BenutzerService {

    private final BenutzerRepository benutzerRepository;

    public BenutzerService(BenutzerRepository benutzerRepository) {
        this.benutzerRepository = benutzerRepository;
    }

    public List<Benutzer> findeAlle() {
        return benutzerRepository.findAll();
    }

    public List<Benutzer> findeNachRolle(Rolle rolle) {
        return benutzerRepository.findByRolle(rolle);
    }

    public Benutzer findeById(Long id) {
        return benutzerRepository.findById(id)
                .orElseThrow(() -> new RessourceNichtGefundenException("Benutzer", id));
    }
}
