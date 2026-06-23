package ch.glauser.serviceauftrag.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Prüft die rollenbasierte Autorisierung der REST-API.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String AUFTRAG_JSON = "{\"kundeName\":\"Test\",\"beschreibung\":\"Wasserhahn\"}";

    @Test
    void ohneAnmeldung_listeLiefert401() throws Exception {
        mockMvc.perform(get("/api/auftraege"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "MITARBEITER")
    void mitarbeiter_darfNichtErfassen_403() throws Exception {
        mockMvc.perform(post("/api/auftraege")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(AUFTRAG_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void admin_darfErfassen_201() throws Exception {
        mockMvc.perform(post("/api/auftraege")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(AUFTRAG_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "MITARBEITER")
    void angemeldeterBenutzer_darfListeSehen_200() throws Exception {
        mockMvc.perform(get("/api/auftraege"))
                .andExpect(status().isOk());
    }
}
