import axios from 'axios'

// Zentrale axios-Instanz fuer alle Backend-Aufrufe.
// baseURL "/api" geht ueber den Vite-Dev-Proxy ans Spring-Boot-Backend.
// withCredentials: true -> Session-Cookie (JSESSIONID) wird mitgeschickt.
const client = axios.create({
  baseURL: '/api',
  withCredentials: true,
})

// Holt die lesbare Fehlermeldung aus der Backend-Antwort
// (ApiExceptionHandler liefert ein JSON mit Feld "nachricht").
export function fehlerText(err) {
  return err?.response?.data?.nachricht || err?.message || 'Unbekannter Fehler'
}

export default client
