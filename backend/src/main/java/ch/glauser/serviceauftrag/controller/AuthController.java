package ch.glauser.serviceauftrag.controller;

import ch.glauser.serviceauftrag.dto.BenutzerResponse;
import ch.glauser.serviceauftrag.dto.LoginRequest;
import ch.glauser.serviceauftrag.exception.RessourceNichtGefundenException;
import ch.glauser.serviceauftrag.model.Benutzer;
import ch.glauser.serviceauftrag.repository.BenutzerRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Login / Logout / aktueller Benutzer. Nach erfolgreichem Login wird die
 * Authentifizierung in der HTTP-Session gespeichert (Cookie JSESSIONID).
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final BenutzerRepository benutzerRepository;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    public AuthController(AuthenticationManager authenticationManager, BenutzerRepository benutzerRepository) {
        this.authenticationManager = authenticationManager;
        this.benutzerRepository = benutzerRepository;
    }

    /** Anmelden. Wirft bei falschen Daten eine AuthenticationException -> HTTP 401. */
    @PostMapping("/login")
    public BenutzerResponse login(@Valid @RequestBody LoginRequest req,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(req.benutzername(), req.passwort()));

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);

        return BenutzerResponse.von(ladeBenutzer(authentication.getName()));
    }

    /** Aktuell angemeldeter Benutzer (für das Frontend, um die Rolle zu kennen). */
    @GetMapping("/me")
    public BenutzerResponse me(Authentication authentication) {
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Nicht angemeldet");
        }
        return BenutzerResponse.von(ladeBenutzer(authentication.getName()));
    }

    /** Abmelden: Session invalidieren und Security-Context leeren. */
    @PostMapping("/logout")
    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
    }

    private Benutzer ladeBenutzer(String benutzername) {
        return benutzerRepository.findByBenutzername(benutzername)
                .orElseThrow(() -> new RessourceNichtGefundenException("Benutzer", 0L));
    }
}
