import { Link } from 'react-router-dom'
import { useAuth } from '../auth/useAuth'

// Startseite: kurze Begrüssung und Einstieg je nach Anmeldestatus.
export default function Home() {
  const { user } = useAuth()
  return (
    <div>
      <h1>Service Auftrag</h1>
      <p>Glauser Illnau AG – interne Auftragsverwaltung.</p>
      <div className="card" style={{ marginTop: '1rem' }}>
        {user ? (
          <>
            <p>
              Angemeldet als <strong>{user.name || user.benutzername}</strong> ({user.rolle}).
            </p>
            <p>
              <Link to="/auftraege">→ Zur Auftragsliste</Link>
            </p>
          </>
        ) : (
          <p>
            <Link to="/login">→ Anmelden</Link>, um Aufträge zu sehen.
          </p>
        )}
      </div>
    </div>
  )
}
