import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import {
  ladeAuftrag,
  disponiere,
  ausfuehren,
  freigeben,
  verrechnen,
  ladeMitarbeiter,
  ladeAuftragPdf,
} from '../api/auftraege'
import { fehlerText } from '../api/client'
import { useAuth } from '../auth/useAuth'
import { NAECHSTER_SCHRITT } from '../constants'

// Detailansicht eines Auftrags inkl. statusabhängiger Aktionen je Rolle:
// disponieren / ausführen / freigeben / verrechnen sowie PDF-Download.
export default function AuftragDetail() {
  const { id } = useParams()
  const { user } = useAuth()
  const [auftrag, setAuftrag] = useState(null)
  const [fehler, setFehler] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    let abbrechen = false
    ladeAuftrag(id)
      .then((data) => {
        if (!abbrechen) setAuftrag(data)
      })
      .catch((e) => {
        if (!abbrechen) setFehler(fehlerText(e))
      })
      .finally(() => {
        if (!abbrechen) setLoading(false)
      })
    return () => {
      abbrechen = true
    }
  }, [id])

  // Führt eine Aktion aus und übernimmt den aktualisierten Auftrag in den State.
  async function aktion(fn) {
    setFehler(null)
    try {
      setAuftrag(await fn())
    } catch (e) {
      setFehler(fehlerText(e))
    }
  }

  if (loading) return <p>Lädt…</p>
  if (!auftrag) return <p className="fehler">{fehler || 'Auftrag nicht gefunden.'}</p>

  const rolle = user?.rolle
  const s = auftrag.status

  return (
    <div>
      <div className="zeile" style={{ marginBottom: '1rem' }}>
        <h2 style={{ margin: 0 }}>Auftrag #{auftrag.id}</h2>
        <span className={`badge badge-${s}`}>{s}</span>
      </div>

      <div className="card">
        <p>
          <strong>Kunde:</strong> {auftrag.kundeName}
        </p>
        <p>
          <strong>Objektadresse:</strong> {auftrag.objektAdresse || '–'}
        </p>
        <p>
          <strong>Arbeiten:</strong> {(auftrag.arbeiten || []).join(', ') || '–'}
        </p>
        <p>
          <strong>Beschreibung:</strong> {auftrag.beschreibung}
        </p>
        <p>
          <strong>Terminwunsch:</strong> {auftrag.terminwunsch || '–'}
        </p>
        <p>
          <strong>Mitarbeiter:</strong> {auftrag.mitarbeiterName || '–'}
        </p>
        <p>
          <strong>Geplanter Termin:</strong> {auftrag.geplanterTermin || '–'}
        </p>
        <p style={{ marginBottom: 0 }}>
          <strong>Rapport:</strong> {auftrag.rapportText || '–'}
        </p>
      </div>

      {fehler && <p className="fehler">{fehler}</p>}

      <div className="card">
        <h3>Aktionen</h3>
        <div className="aktionen">
          {s === 'ERFASST' && rolle === 'BEREICHSLEITER' && (
            <DisponierenForm id={auftrag.id} onDone={setAuftrag} onError={setFehler} />
          )}
          {s === 'DISPONIERT' && rolle === 'MITARBEITER' && (
            <AusfuehrenForm id={auftrag.id} onDone={setAuftrag} onError={setFehler} />
          )}
          {s === 'AUSGEFUEHRT' && rolle === 'BEREICHSLEITER' && (
            <button onClick={() => aktion(() => freigeben(auftrag.id))}>Rapport freigeben</button>
          )}
          {s === 'FREIGEGEBEN' && rolle === 'ADMIN' && (
            <button onClick={() => aktion(() => verrechnen(auftrag.id))}>Verrechnen</button>
          )}

          <button className="sekundaer" onClick={() => ladeAuftragPdf(auftrag.id)}>
            Auftragsdokument drucken (PDF)
          </button>
        </div>
        <p className="hinweis" style={{ marginTop: '1rem' }}>
          Nächster Schritt: {NAECHSTER_SCHRITT[s]}
        </p>
      </div>
    </div>
  )
}

// Teilformular: Mitarbeiter zuweisen + optionalen Termin setzen (Bereichsleiter).
function DisponierenForm({ id, onDone, onError }) {
  const [mitarbeiter, setMitarbeiter] = useState([])
  const [mitarbeiterId, setMitarbeiterId] = useState('')
  const [geplanterTermin, setGeplanterTermin] = useState('')
  const [busy, setBusy] = useState(false)

  useEffect(() => {
    ladeMitarbeiter()
      .then(setMitarbeiter)
      .catch(() => {})
  }, [])

  async function onSubmit(e) {
    e.preventDefault()
    setBusy(true)
    onError(null)
    try {
      onDone(await disponiere(id, Number(mitarbeiterId), geplanterTermin || null))
    } catch (err) {
      onError(fehlerText(err))
    } finally {
      setBusy(false)
    }
  }

  return (
    <form onSubmit={onSubmit}>
      <label>
        Mitarbeiter
        <select value={mitarbeiterId} onChange={(e) => setMitarbeiterId(e.target.value)} required>
          <option value="">– wählen –</option>
          {mitarbeiter.map((m) => (
            <option key={m.id} value={m.id}>
              {m.name || m.benutzername}
            </option>
          ))}
        </select>
      </label>
      <label>
        Geplanter Termin
        <input type="date" value={geplanterTermin} onChange={(e) => setGeplanterTermin(e.target.value)} />
      </label>
      <button type="submit" disabled={busy}>
        Disponieren
      </button>
    </form>
  )
}

// Teilformular: Rapport erfassen und als ausgeführt markieren (Mitarbeiter).
function AusfuehrenForm({ id, onDone, onError }) {
  const [rapportText, setRapportText] = useState('')
  const [busy, setBusy] = useState(false)

  async function onSubmit(e) {
    e.preventDefault()
    setBusy(true)
    onError(null)
    try {
      onDone(await ausfuehren(id, rapportText))
    } catch (err) {
      onError(fehlerText(err))
    } finally {
      setBusy(false)
    }
  }

  return (
    <form onSubmit={onSubmit}>
      <label>
        Rapport
        <textarea value={rapportText} onChange={(e) => setRapportText(e.target.value)} required />
      </label>
      <button type="submit" disabled={busy}>
        Als ausgeführt markieren
      </button>
    </form>
  )
}
