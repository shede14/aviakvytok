// Форматування дат, цін і статусів для відображення
const dateFmt = new Intl.DateTimeFormat('uk-UA', { day: '2-digit', month: 'short', year: 'numeric' })
const timeFmt = new Intl.DateTimeFormat('uk-UA', { hour: '2-digit', minute: '2-digit' })
const priceFmt = new Intl.NumberFormat('uk-UA', { style: 'currency', currency: 'UAH', maximumFractionDigits: 0 })

export const formatDate = (iso) => (iso ? dateFmt.format(new Date(iso)) : '—')
export const formatTime = (iso) => (iso ? timeFmt.format(new Date(iso)) : '—')
export const formatPrice = (v) => (v == null ? '—' : priceFmt.format(v))

export function duration(from, to) {
  const min = Math.round((new Date(to) - new Date(from)) / 60000)
  return `${Math.floor(min / 60)} год ${String(min % 60).padStart(2, '0')} хв`
}

export const STATUSES = {
  SCHEDULED: { label: 'За розкладом', badge: 'text-bg-success' },
  DELAYED: { label: 'Затримується', badge: 'text-bg-warning' },
  DEPARTED: { label: 'Вилетів', badge: 'text-bg-info' },
  ARRIVED: { label: 'Прибув', badge: 'text-bg-secondary' },
  CANCELLED: { label: 'Скасовано', badge: 'text-bg-danger' },
}
