import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { erfasseAuftrag } from '../api/auftraege'
import { fehlerText } from '../api/client'
import { ARBEITSTYPEN } from '../constants'

const LEER = {
  kundeName: '',
  strasse: '',
  plz: '',
  ort: '',
  telefon: '',
  natel: '',
  objektAdresse: '',
  beschreibung: '',
  terminwunsch: '',
}

// Formular zum Erfassen eines Auftrags (nur ADMIN, siehe Routing).
export default function AuftragErfassen() {
  const navigate = useNavigate()
  const [form, setForm] = useState(LEER)
  const [arbeiten, setArbeiten] = useState([])
  const [fehler, setFehler] = useState(null)
  const [busy, setBusy] = useState(false)

  function setFeld(name, value) {
    setForm((f) => ({ ...f, [name]: value }))
  }

  function toggleArbeit(typ) {
    setArbeiten((a) => (a.includes(typ) ? a.filter((x) => x !== typ) : [...a, typ]))
  }

  async function onSubmit(e) {
    e.preventDefault()
    setFehler(null)
    setBusy(true)
    try {
      const neu = await erfasseAuftrag({ ...form, arbeiten })
      navigate(`/auftraege/${neu.id}`)
    } catch (err) {
      setFehler(fehlerText(err))
    } finally {
      setBusy(false)
    }
  }

  return (
    <div className="card" style={{ maxWidth: 520 }}>
      <h2>Auftrag erfassen</h2>
      <form onSubmit={onSubmit}>
        <label>
          Kunde (Name) *
          <input value={form.kundeName} onChange={(e) => setFeld('kundeName', e.target.value)} required />
        </label>
        <label>
          Strasse
          <input value={form.strasse} onChange={(e) => setFeld('strasse', e.target.value)} />
        </label>
        <div className="zeile">
          <label>
            PLZ
            <input value={form.plz} onChange={(e) => setFeld('plz', e.target.value)} />
          </label>
          <label>
            Ort
            <input value={form.ort} onChange={(e) => setFeld('ort', e.target.value)} />
          </label>
        </div>
        <div className="zeile">
          <label>
            Telefon
            <input value={form.telefon} onChange={(e) => setFeld('telefon', e.target.value)} />
          </label>
          <label>
            Natel
            <input value={form.natel} onChange={(e) => setFeld('natel', e.target.value)} />
          </label>
        </div>
        <label>
          Objektadresse
          <input value={form.objektAdresse} onChange={(e) => setFeld('objektAdresse', e.target.value)} />
        </label>

        <fieldset>
          <legend>Auszuführende Arbeiten</legend>
          {ARBEITSTYPEN.map((t) => (
            <label key={t} className="check">
              <input type="checkbox" checked={arbeiten.includes(t)} onChange={() => toggleArbeit(t)} />
              {t}
            </label>
          ))}
        </fieldset>

        <label>
          Beschreibung *
          <textarea
            value={form.beschreibung}
            onChange={(e) => setFeld('beschreibung', e.target.value)}
            required
          />
        </label>
        <label>
          Terminwunsch
          <input value={form.terminwunsch} onChange={(e) => setFeld('terminwunsch', e.target.value)} />
        </label>

        {fehler && <p className="fehler">{fehler}</p>}

        <div className="zeile">
          <button type="submit" disabled={busy}>
            {busy ? '…' : 'Speichern'}
          </button>
          <button type="button" className="sekundaer" onClick={() => navigate('/auftraege')}>
            Abbrechen
          </button>
        </div>
      </form>
    </div>
  )
}
