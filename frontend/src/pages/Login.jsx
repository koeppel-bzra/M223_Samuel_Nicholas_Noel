import { useState } from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import { useAuth } from '../auth/useAuth'
import { fehlerText } from '../api/client'

// Login-Formular. Nach erfolgreicher Anmeldung zurück zur ursprünglich
// gewünschten Seite (oder zur Auftragsliste).
export default function Login() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const [benutzername, setBenutzername] = useState('')
  const [passwort, setPasswort] = useState('')
  const [fehler, setFehler] = useState(null)
  const [busy, setBusy] = useState(false)

  async function onSubmit(e) {
    e.preventDefault()
    setFehler(null)
    setBusy(true)
    try {
      await login(benutzername, passwort)
      const ziel = location.state?.from?.pathname || '/auftraege'
      navigate(ziel, { replace: true })
    } catch (err) {
      setFehler(fehlerText(err))
    } finally {
      setBusy(false)
    }
  }

  return (
    <div className="card" style={{ maxWidth: 360 }}>
      <h2>Anmelden</h2>
      <form onSubmit={onSubmit}>
        <label>
          Benutzername
          <input value={benutzername} onChange={(e) => setBenutzername(e.target.value)} autoFocus />
        </label>
        <label>
          Passwort
          <input
            type="password"
            value={passwort}
            onChange={(e) => setPasswort(e.target.value)}
          />
        </label>
        {fehler && <p className="fehler">{fehler}</p>}
        <button type="submit" disabled={busy}>
          {busy ? '…' : 'Anmelden'}
        </button>
      </form>
      <p className="hinweis" style={{ marginTop: '0.75rem' }}>
        Demo: admin/admin · bereichsleiter/bereichsleiter · mitarbeiter/mitarbeiter
      </p>
    </div>
  )
}
