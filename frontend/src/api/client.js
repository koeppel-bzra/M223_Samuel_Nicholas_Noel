import axios from 'axios'

// Zentrale axios-Instanz fuer alle Backend-Aufrufe.
// baseURL "/api" geht ueber den Vite-Dev-Proxy ans Spring-Boot-Backend.
// withCredentials: true -> Session-Cookie wird mitgeschickt (spaeter fuers Login).
const client = axios.create({
  baseURL: '/api',
  withCredentials: true,
})

export default client
