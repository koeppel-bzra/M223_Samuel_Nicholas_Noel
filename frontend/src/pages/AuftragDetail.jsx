import { useParams } from 'react-router-dom'

// Platzhalter-Seite. TODO selbst umsetzen: Auftrag-Detail + Aktionen
// (disponieren / ausfuehren / freigeben / verrechnen) je nach Rolle und Status.
export default function AuftragDetail() {
  const { id } = useParams()
  return (
    <div>
      <h2>Auftrag-Detail (#{id})</h2>
      <p>Platzhalter – hier kommen Details + Aktionsbuttons hin.</p>
    </div>
  )
}
