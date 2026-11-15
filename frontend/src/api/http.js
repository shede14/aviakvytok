import axios from 'axios'

// Єдиний екземпляр axios для всього застосунку
// Під час розробки (npm run dev, порт 5173) звертаємось до Spring Boot на порту 8080
// через CORS; у зібраній версії застосунок віддає сам Spring Boot, тому API на тому ж домені.
const API_URL = import.meta.env.DEV ? 'http://localhost:8080/api' : '/api'

const http = axios.create({
  baseURL: API_URL,
  timeout: 8000,
  headers: { 'Content-Type': 'application/json' },
})

/**
 * Перетворює помилку axios на зрозуміле повідомлення українською.
 * Окремо обробляється відсутність зв'язку з сервером (немає response).
 */
export function errorMessage(error) {
  if (!error.response) {
    return 'Немає зв’язку з сервером. Перевірте, що Spring Boot запущено на порту 8080.'
  }
  const data = error.response.data || {}
  if (error.response.status === 400 && data.fields) {
    return 'Перевірте правильність заповнення полів.'
  }
  return data.message || `Помилка сервера (${error.response.status})`
}

/** Помилки валідації окремих полів з відповіді Spring Boot (код 400). */
export function fieldErrors(error) {
  return error.response?.data?.fields || {}
}

export default http
