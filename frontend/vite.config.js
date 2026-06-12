import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    // Anfragen an /api werden ans Spring-Boot-Backend weitergeleitet.
    // So braucht das Frontend keine CORS-Konfiguration im Dev-Betrieb.
    proxy: {
      '/api': 'http://localhost:8080',
    },
  },
})
