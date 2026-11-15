import http from './http'

// Запити до REST API Spring Boot для сутності «Рейс»
export const flightsApi = {
  getAll: () => http.get('/flights').then((r) => r.data),
  getById: (id) => http.get(`/flights/${id}`).then((r) => r.data),
  create: (flight) => http.post('/flights', flight).then((r) => r.data),
  update: (id, flight) => http.put(`/flights/${id}`, flight).then((r) => r.data),
  remove: (id) => http.delete(`/flights/${id}`),
}

// Довідники для форми рейсу та інших сторінок
export const airlinesApi = {
  getAll: () => http.get('/airlines').then((r) => r.data),
}

export const airportsApi = {
  getAll: () => http.get('/airports').then((r) => r.data),
}

export const bookingsApi = {
  getAll: () => http.get('/bookings').then((r) => r.data),
}
