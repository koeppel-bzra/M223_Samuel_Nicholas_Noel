import { useEffect, useState } from 'react'
import client from '../api/client'
import { AuthContext } from './useAuth'

// Hält den aktuell angemeldeten Benutzer und stellt login/logout bereit.
// Beim Start wird über GET /api/auth/me geprüft, ob bereits eine Session besteht.
export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    let abbrechen = false
    client
      .get('/auth/me')
      .then((res) => {
        if (!abbrechen) setUser(res.data)
      })
      .catch(() => {
        if (!abbrechen) setUser(null)
      })
      .finally(() => {
        if (!abbrechen) setLoading(false)
      })
    return () => {
      abbrechen = true
    }
  }, [])

  async function login(benutzername, passwort) {
    const res = await client.post('/auth/login', { benutzername, passwort })
    setUser(res.data)
    return res.data
  }

  async function logout() {
    await client.post('/auth/logout')
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{ user, loading, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}
