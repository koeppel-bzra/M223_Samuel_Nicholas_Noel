import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { listeAuftraege } from '../api/auftraege'
import { fehlerText } from '../api/client'
import { useAuth } from '../auth/useAuth'
import { STATUS } from '../constants'

// Liste aller Aufträge mit Filter nach Status. Klick auf eine Zeile -> Detail.
export default function AuftragListe() {
  const { user } = useAuth()
  const [status, setStatus] = useState('')
  const [auftraege, setAuftraege] = useState([])
  const [fehler, setFehler] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    let abbrechen = false
    listeAuftraege(status)
      .then((data) => {
        if (!abbrechen) {
          setAuftraege(data)
          setFehler(null)
        }
      })
      .catch((err) => {
        if (!abbrechen) setFehler(fehlerText(err))
      })
      .finally(() => {
        if (!abbrechen) setLoading(false)
      })
    return () => {
      abbrechen = true
    }
  }, [status])

  return (
    <div>
      <div className="zeile" style={{ marginBottom: '1rem' }}>
        <h2 style={{ margin: 0 }}>Aufträge</h2>
        {user?.rolle === 'ADMIN' && (
          <Link className="button" to="/auftraege/neu">
            + Neu
          </Link>
        )}
      </div>

      <label style={{ maxWidth: 200, marginBottom: '1rem' }}>
        Status
        <select value={status} onChange={(e) => setStatus(e.target.value)}>
          <option value="">Alle</option>
          {STATUS.map((s) => (
            <option key={s} value={s}>
              {s}
            </option>
          ))}
        </select>
      </label>

      {fehler && <p className="fehler">{fehler}</p>}

      {loading ? (
        <p>Lädt…</p>
      ) : (
        <table>
          <thead>
            <tr>
              <th>Nr</th>
              <th>Kunde</th>
              <th>Status</th>
              <th>Arbeiten</th>
            </tr>
          </thead>
          <tbody>
            {auftraege.map((a) => (
              <tr key={a.id}>
                <td>
                  <Link to={`/auftraege/${a.id}`}>#{a.id}</Link>
                </td>
                <td>{a.kundeName}</td>
                <td>
                  <span className={`badge badge-${a.status}`}>{a.status}</span>
                </td>
                <td>{(a.arbeiten || []).join(', ') || '–'}</td>
              </tr>
            ))}
            {auftraege.length === 0 && (
              <tr>
                <td colSpan="4" className="hinweis">
                  Keine Aufträge vorhanden.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      )}
    </div>
  )
}
