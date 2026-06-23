package ch.glauser.serviceauftrag.controller;

import ch.glauser.serviceauftrag.exception.RessourceNichtGefundenException;
import ch.glauser.serviceauftrag.exception.UngueltigerStatusUebergangException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Wandelt Exceptions in saubere HTTP-Antworten mit JSON-Fehlerkörper um.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(RessourceNichtGefundenException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> nichtGefunden(RessourceNichtGefundenException ex) {
        return fehlerKoerper(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(UngueltigerStatusUebergangException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> ungueltigerUebergang(UngueltigerStatusUebergangException ex) {
        return fehlerKoerper(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, Object> loginFehlgeschlagen(AuthenticationException ex) {
        return fehlerKoerper(HttpStatus.UNAUTHORIZED, "Login fehlgeschlagen: ungültiger Benutzername oder Passwort.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> validierungsfehler(MethodArgumentNotValidException ex) {
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Ungültige Eingabe");
        return fehlerKoerper(HttpStatus.BAD_REQUEST, details);
    }

    private Map<String, Object> fehlerKoerper(HttpStatus status, String nachricht) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("zeitpunkt", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("fehler", status.getReasonPhrase());
        body.put("nachricht", nachricht);
        return body;
    }
}
