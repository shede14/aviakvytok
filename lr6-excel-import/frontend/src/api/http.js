import axios from 'axios'

// Застосунок віддає сам Spring Boot (порт 8082), тому достатньо відносного шляху /api.
// У режимі npm run dev запити /api проксіює Vite (див. vite.config.js).
export const http = axios.create({ baseURL: '/api', timeout: 30000 })

export const api = {
  /** Надсилання файлу через FormData (multipart/form-data, поле «file»). */
  importExcel(file, onProgress) {
    const formData = new FormData()
    formData.append('file', file)
    return http.post('/import/excel', formData, {
      onUploadProgress: (e) => onProgress && e.total && onProgress(Math.round((e.loaded * 100) / e.total))
    }).then((r) => r.data)
  },
  stats: () => http.get('/stats').then((r) => r.data),
  recent: () => http.get('/sales/recent').then((r) => r.data),
  clear: () => http.delete('/sales').then((r) => r.data)
}

/** Текст помилки для користувача. */
export function errorMessage(error) {
  if (!error.response) {
    return 'Немає зв’язку з сервером. Перевірте, що Spring Boot запущено на порту 8082.'
  }
  return error.response.data?.message || `Помилка сервера (HTTP ${error.response.status}).`
}
