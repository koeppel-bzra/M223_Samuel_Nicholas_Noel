import { BrowserRouter, Routes, Route, Link } from 'react-router-dom'
import Home from './pages/Home'
import Login from './pages/Login'
import AuftragListe from './pages/AuftragListe'
import AuftragErfassen from './pages/AuftragErfassen'
import AuftragDetail from './pages/AuftragDetail'
import './App.css'

// Geruest-Routing. Die Seiten sind aktuell nur Platzhalter und werden
// Schritt fuer Schritt selbst mit Logik gefuellt.
function App() {
  return (
    <BrowserRouter>
      <nav style={{ display: 'flex', gap: '1rem', padding: '1rem', borderBottom: '1px solid #ccc' }}>
        <Link to="/">Start</Link>
        <Link to="/auftraege">Auftraege</Link>
        <Link to="/auftraege/neu">Erfassen</Link>
        <Link to="/login">Login</Link>
      </nav>
      <main style={{ padding: '1rem' }}>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/auftraege" element={<AuftragListe />} />
          <Route path="/auftraege/neu" element={<AuftragErfassen />} />
          <Route path="/auftraege/:id" element={<AuftragDetail />} />
        </Routes>
      </main>
    </BrowserRouter>
  )
}

export default App
