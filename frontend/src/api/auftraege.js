import client from './client'

// Alle Auftrags-Aufrufe ans Backend gebündelt (REST-Endpoints des AuftragController).

export const listeAuftraege = (status) =>
  client.get('/auftraege', { params: status ? { status } : {} }).then((r) => r.data)

export const ladeAuftrag = (id) =>
  client.get(`/auftraege/${id}`).then((r) => r.data)

export const erfasseAuftrag = (daten) =>
  client.post('/auftraege', daten).then((r) => r.data)

export const disponiere = (id, mitarbeiterId, geplanterTermin) =>
  client
    .patch(`/auftraege/${id}/disponieren`, { mitarbeiterId, geplanterTermin })
    .then((r) => r.data)

export const ausfuehren = (id, rapportText) =>
  client.patch(`/auftraege/${id}/ausfuehren`, { rapportText }).then((r) => r.data)

export const freigeben = (id) =>
  client.patch(`/auftraege/${id}/freigeben`).then((r) => r.data)

export const verrechnen = (id) =>
  client.patch(`/auftraege/${id}/verrechnen`).then((r) => r.data)

// Mitarbeiter fürs Disponieren (GET /api/benutzer?rolle=MITARBEITER).
export const ladeMitarbeiter = () =>
  client.get('/benutzer', { params: { rolle: 'MITARBEITER' } }).then((r) => r.data)

// PDF herunterladen: als Blob holen und einen Download anstossen.
export async function ladeAuftragPdf(id) {
  const res = await client.get(`/auftraege/${id}/pdf`, { responseType: 'blob' })
  const url = URL.createObjectURL(res.data)
  const a = document.createElement('a')
  a.href = url
  a.download = `auftrag-${id}.pdf`
  document.body.appendChild(a)
  a.click()
  a.remove()
  URL.revokeObjectURL(url)
}
