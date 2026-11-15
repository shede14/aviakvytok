import { reactive } from 'vue'

// Простий глобальний стан сповіщень (ref/reactive замість Vuex)
export const toastState = reactive({ items: [] })

let nextId = 1

export function notify(message, type = 'success') {
  const id = nextId++
  toastState.items.push({ id, message, type })
  setTimeout(() => {
    const i = toastState.items.findIndex((t) => t.id === id)
    if (i !== -1) toastState.items.splice(i, 1)
  }, 4000)
}
