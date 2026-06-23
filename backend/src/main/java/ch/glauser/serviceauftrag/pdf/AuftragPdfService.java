package ch.glauser.serviceauftrag.pdf;

import ch.glauser.serviceauftrag.model.Arbeitstyp;
import ch.glauser.serviceauftrag.model.Auftrag;
import ch.glauser.serviceauftrag.model.Benutzer;
import ch.glauser.serviceauftrag.model.Kunde;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

/**
 * Erzeugt das druckbare Auftragsdokument (PDF) zu einem Auftrag.
 *
 * Deckt den Use-Case „Auftragsdokument generieren/drucken" ab und spiegelt die
 * Detailansicht aus den Wireframes (Kundendaten, Arbeiten, Status, Rapport ...).
 * Genutzt wird OpenPDF; das fertige Dokument wird als Byte-Array zurückgegeben,
 * damit der Controller es direkt als Download ausliefern kann.
 */
@Service
public class AuftragPdfService {

    private static final DateTimeFormatter DATUM = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter ZEITPUNKT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private static final Font TITEL = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
    private static final Font UNTERTITEL = FontFactory.getFont(FontFactory.HELVETICA, 11);
    private static final Font ABSCHNITT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
    private static final Font LABEL = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
    private static final Font WERT = FontFactory.getFont(FontFactory.HELVETICA, 10);

    /** Baut das Auftragsdokument auf und liefert es als PDF-Bytes. */
    public byte[] erzeugeAuftragsdokument(Auftrag auftrag) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, out);
            document.open();

            schreibeKopf(document, auftrag);
            schreibeKunde(document, auftrag.getKunde());
            schreibeAuftragsdetails(document, auftrag);
            schreibeAusfuehrung(document, auftrag);

            document.close();
        } catch (DocumentException e) {
            throw new IllegalStateException("PDF konnte nicht erzeugt werden", e);
        }
        return out.toByteArray();
    }

    private void schreibeKopf(Document document, Auftrag auftrag) {
        Paragraph titel = new Paragraph("Service Auftrag", TITEL);
        document.add(titel);
        document.add(new Paragraph("Glauser Illnau AG – Sanitärunternehmen", UNTERTITEL));

        Paragraph kopfzeile = new Paragraph(
                "Auftrag Nr. " + auftrag.getId() + "   ·   Status: " + auftrag.getStatus(),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11));
        kopfzeile.setSpacingBefore(8f);
        kopfzeile.setSpacingAfter(12f);
        document.add(kopfzeile);
    }

    private void schreibeKunde(Document document, Kunde kunde) {
        document.add(abschnitt("Kunde"));
        PdfPTable tabelle = neueTabelle();
        if (kunde != null) {
            zeile(tabelle, "Name", kunde.getName());
            zeile(tabelle, "Strasse", kunde.getStrasse());
            zeile(tabelle, "PLZ / Ort", verbinde(kunde.getPlz(), kunde.getOrt()));
            zeile(tabelle, "Telefon", kunde.getTelefon());
            zeile(tabelle, "Natel", kunde.getNatel());
        }
        document.add(tabelle);
    }

    private void schreibeAuftragsdetails(Document document, Auftrag auftrag) {
        document.add(abschnitt("Auftrag"));
        PdfPTable tabelle = neueTabelle();
        zeile(tabelle, "Objektadresse", auftrag.getObjektAdresse());
        zeile(tabelle, "Arbeiten", arbeitenAlsText(auftrag));
        zeile(tabelle, "Beschreibung", auftrag.getBeschreibung());
        zeile(tabelle, "Terminwunsch", auftrag.getTerminwunsch());
        if (auftrag.getDatum() != null) {
            zeile(tabelle, "Datum", auftrag.getDatum().format(DATUM));
        }
        if (auftrag.getErstelltAm() != null) {
            zeile(tabelle, "Erstellt am", auftrag.getErstelltAm().format(ZEITPUNKT));
        }
        document.add(tabelle);
    }

    private void schreibeAusfuehrung(Document document, Auftrag auftrag) {
        document.add(abschnitt("Ausführung"));
        PdfPTable tabelle = neueTabelle();
        Benutzer mitarbeiter = auftrag.getZugewiesenerMitarbeiter();
        zeile(tabelle, "Mitarbeiter", mitarbeiter != null ? mitarbeiter.getName() : "–");
        zeile(tabelle, "Geplanter Termin",
                auftrag.getGeplanterTermin() != null ? auftrag.getGeplanterTermin().format(DATUM) : "–");
        zeile(tabelle, "Rapport",
                auftrag.getRapportText() != null && !auftrag.getRapportText().isBlank()
                        ? auftrag.getRapportText() : "–");
        document.add(tabelle);
    }

    // ---------- Hilfsmethoden ----------

    private Paragraph abschnitt(String text) {
        Paragraph p = new Paragraph(text, ABSCHNITT);
        p.setSpacingBefore(12f);
        p.setSpacingAfter(4f);
        return p;
    }

    private PdfPTable neueTabelle() {
        PdfPTable tabelle = new PdfPTable(new float[]{1f, 3f});
        tabelle.setWidthPercentage(100f);
        return tabelle;
    }

    private void zeile(PdfPTable tabelle, String label, String wert) {
        tabelle.addCell(zelle(new Phrase(label, LABEL)));
        tabelle.addCell(zelle(new Phrase(wert != null && !wert.isBlank() ? wert : "–", WERT)));
    }

    private PdfPCell zelle(Phrase inhalt) {
        PdfPCell zelle = new PdfPCell(inhalt);
        zelle.setBorder(PdfPCell.NO_BORDER);
        zelle.setPaddingBottom(4f);
        zelle.setVerticalAlignment(Element.ALIGN_TOP);
        return zelle;
    }

    private String arbeitenAlsText(Auftrag auftrag) {
        if (auftrag.getArbeiten() == null || auftrag.getArbeiten().isEmpty()) {
            return null;
        }
        return auftrag.getArbeiten().stream()
                .map(Arbeitstyp::name)
                .sorted()
                .collect(Collectors.joining(", "));
    }

    private String verbinde(String a, String b) {
        String links = a != null ? a.trim() : "";
        String rechts = b != null ? b.trim() : "";
        return (links + " " + rechts).trim();
    }
}
