import { useEffect, useState } from 'react'
import client from '../api/client'

// Startseite des Geruests: prueft, ob das Backend erreichbar ist (GET /api/ping).
// Dient nur als Verbindungstest und kann spaeter ersetzt werden.
export default function Home() {
  const [status, setStatus] = useState('pruefe...')

  useEffect(() => {
    client
      .get('/ping')
      .then((res) => setStatus('Backend erreichbar: ' + JSON.stringify(res.data)))
      .catch(() => setStatus('Backend NICHT erreichbar (laeuft das Spring-Backend auf :8080?)'))
  }, [])

  return (
    <div>
      <h1>Service Auftrag</h1>
      <p>Geruest steht. Ab hier wird die Logik selbst programmiert.</p>
      <p><strong>{status}</strong></p>
    </div>
  )
}
