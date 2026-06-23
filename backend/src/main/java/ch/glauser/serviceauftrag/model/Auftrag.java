package ch.glauser.serviceauftrag.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Serviceauftrag – zentrale Entität. Durchläuft den Lebenszyklus
 * ERFASST -> DISPONIERT -> AUSGEFUEHRT -> FREIGEGEBEN -> VERRECHNET.
 */
@Entity
public class Auftrag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate datum;
    private LocalTime zeit;

    @Enumerated(EnumType.STRING)
    private AuftragStatus status = AuftragStatus.ERFASST;

    // Mehrere Arbeitstypen pro Auftrag -> eigene Tabelle (vgl. ERM: auftrag_arbeitstyp)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "auftrag_arbeitstyp", joinColumns = @JoinColumn(name = "auftrag_id"))
    @Column(name = "arbeitstyp")
    @Enumerated(EnumType.STRING)
    private Set<Arbeitstyp> arbeiten = new HashSet<>();

    @Column(length = 500)
    private String beschreibung;

    private String terminwunsch;

    private String objektAdresse;

    @ManyToOne
    @JoinColumn(name = "kunde_id")
    private Kunde kunde;

    // wird erst beim Disponieren gesetzt
    @ManyToOne
    @JoinColumn(name = "benutzer_id")
    private Benutzer zugewiesenerMitarbeiter;

    private LocalDate geplanterTermin;

    @Column(length = 1000)
    private String rapportText;

    private LocalDateTime erstelltAm;

    public Auftrag() {
    }

    @PrePersist
    public void prePersist() {
        if (erstelltAm == null) {
            erstelltAm = LocalDateTime.now();
        }
        if (status == null) {
            status = AuftragStatus.ERFASST;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDatum() {
        return datum;
    }

    public void setDatum(LocalDate datum) {
        this.datum = datum;
    }

    public LocalTime getZeit() {
        return zeit;
    }

    public void setZeit(LocalTime zeit) {
        this.zeit = zeit;
    }

    public AuftragStatus getStatus() {
        return status;
    }

    public void setStatus(AuftragStatus status) {
        this.status = status;
    }

    public Set<Arbeitstyp> getArbeiten() {
        return arbeiten;
    }

    public void setArbeiten(Set<Arbeitstyp> arbeiten) {
        this.arbeiten = arbeiten;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public String getTerminwunsch() {
        return terminwunsch;
    }

    public void setTerminwunsch(String terminwunsch) {
        this.terminwunsch = terminwunsch;
    }

    public String getObjektAdresse() {
        return objektAdresse;
    }

    public void setObjektAdresse(String objektAdresse) {
        this.objektAdresse = objektAdresse;
    }

    public Kunde getKunde() {
        return kunde;
    }

    public void setKunde(Kunde kunde) {
        this.kunde = kunde;
    }

    public Benutzer getZugewiesenerMitarbeiter() {
        return zugewiesenerMitarbeiter;
    }

    public void setZugewiesenerMitarbeiter(Benutzer zugewiesenerMitarbeiter) {
        this.zugewiesenerMitarbeiter = zugewiesenerMitarbeiter;
    }

    public LocalDate getGeplanterTermin() {
        return geplanterTermin;
    }

    public void setGeplanterTermin(LocalDate geplanterTermin) {
        this.geplanterTermin = geplanterTermin;
    }

    public String getRapportText() {
        return rapportText;
    }

    public void setRapportText(String rapportText) {
        this.rapportText = rapportText;
    }

    public LocalDateTime getErstelltAm() {
        return erstelltAm;
    }

    public void setErstelltAm(LocalDateTime erstelltAm) {
        this.erstelltAm = erstelltAm;
    }
}
