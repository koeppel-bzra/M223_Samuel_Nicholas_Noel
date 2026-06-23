import { createContext, useContext } from 'react'

// Context + Hook getrennt von der Provider-Komponente, damit jede Datei
// nur eine Sache exportiert (Vite Fast-Refresh / eslint react-refresh).
export const AuthContext = createContext(null)

export function useAuth() {
  return useContext(AuthContext)
}
