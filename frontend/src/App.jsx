import { BrowserRouter, Routes, Route, Link, Navigate, useLocation } from 'react-router-dom'
import { AuthProvider } from './auth/AuthContext'
import { useAuth } from './auth/useAuth'
import Home from './pages/Home'
import Login from './pages/Login'
import AuftragListe from './pages/AuftragListe'
import AuftragErfassen from './pages/AuftragErfassen'
import AuftragDetail from './pages/AuftragDetail'
import './App.css'

// Navigationsleiste: Links je nach Rolle, rechts angemeldeter Benutzer + Abmelden.
function Nav() {
  const { user, logout } = useAuth()
  return (
    <nav>
      <div className="nav-links">
        <Link to="/">Start</Link>
        {user && <Link to="/auftraege">Aufträge</Link>}
        {user?.rolle === 'ADMIN' && <Link to="/auftraege/neu">Erfassen</Link>}
      </div>
      <div className="nav-user">
        {user ? (
          <>
            <span>
              {user.name || user.benutzername} ({user.rolle})
            </span>
            <button className="sekundaer" onClick={logout}>
              Abmelden
            </button>
          </>
        ) : (
          <Link to="/login">Login</Link>
        )}
      </div>
    </nav>
  )
}

// Schützt Seiten: ohne Anmeldung -> Weiterleitung auf /login.
function RequireAuth({ children }) {
  const { user, loading } = useAuth()
  const location = useLocation()
  if (loading) return <p>Lädt…</p>
  if (!user) return <Navigate to="/login" replace state={{ from: location }} />
  return children
}

// Schützt Seiten, die nur eine bestimmte Rolle sehen darf (z. B. Erfassen = ADMIN).
function RequireRole({ rolle, children }) {
  const { user } = useAuth()
  if (user?.rolle !== rolle) return <p className="fehler">Keine Berechtigung für diese Seite.</p>
  return children
}

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Nav />
        <main>
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/login" element={<Login />} />
            <Route
              path="/auftraege"
              element={
                <RequireAuth>
                  <AuftragListe />
                </RequireAuth>
              }
            />
            <Route
              path="/auftraege/neu"
              element={
                <RequireAuth>
                  <RequireRole rolle="ADMIN">
                    <AuftragErfassen />
                  </RequireRole>
                </RequireAuth>
              }
            />
            <Route
              path="/auftraege/:id"
              element={
                <RequireAuth>
                  <AuftragDetail />
                </RequireAuth>
              }
            />
          </Routes>
        </main>
      </BrowserRouter>
    </AuthProvider>
  )
}
