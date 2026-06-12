package ch.glauser.serviceauftrag.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Geruest-Controller, nur zum Pruefen, dass das Backend laeuft.
 * GET /api/ping  ->  {"status":"ok", ...}
 * Kann geloescht werden, sobald die echten Controller stehen.
 */
@RestController
@RequestMapping("/api")
public class PingController {

    @GetMapping("/ping")
    public Map<String, String> ping() {
        return Map.of(
                "status", "ok",
                "app", "serviceauftrag"
        );
    }
}
