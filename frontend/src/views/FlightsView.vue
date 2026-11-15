<script setup>
import { ref, computed, onMounted } from 'vue'
import { flightsApi } from '../api/flights'
import { errorMessage } from '../api/http'
import { notify } from '../components/toast'
import { formatDate, formatTime, formatPrice, STATUSES } from '../components/format'
import ErrorAlert from '../components/ErrorAlert.vue'
import LoadingSpinner from '../components/LoadingSpinner.vue'
import FlightCard from '../components/FlightCard.vue'
import ConfirmDialog from '../components/ConfirmDialog.vue'

const flights = ref([])
const loading = ref(true)
const error = ref('')
const search = ref('')
const view = ref('table') // 'table' | 'cards'

const toDelete = ref(null)
const deleting = ref(false)

const filtered = computed(() => {
  const q = search.value.trim().toLowerCase()
  const list = [...flights.value].sort((a, b) => new Date(a.departureTime) - new Date(b.departureTime))
  if (!q) return list
  return list.filter((f) =>
    [f.flightNumber, f.airlineName, f.departureAirportCode, f.arrivalAirportCode]
      .join(' ')
      .toLowerCase()
      .includes(q),
  )
})

// READ: отримання списку рейсів
function load() {
  loading.value = true
  error.value = ''
  flightsApi
    .getAll()
    .then((data) => (flights.value = data))
    .catch((e) => (error.value = errorMessage(e)))
    .finally(() => (loading.value = false))
}

// DELETE: видалення після підтвердження
function confirmDelete() {
  deleting.value = true
  const flight = toDelete.value
  flightsApi
    .remove(flight.id)
    .then(() => {
      flights.value = flights.value.filter((f) => f.id !== flight.id)
      notify(`Рейс ${flight.flightNumber} видалено`)
      toDelete.value = null
    })
    .catch((e) => {
      notify(errorMessage(e), 'danger')
      toDelete.value = null
    })
    .finally(() => (deleting.value = false))
}

onMounted(load)
</script>

<template>
  <div class="container">
    <div class="d-flex flex-wrap justify-content-between align-items-center gap-3 mb-4">
      <div>
        <h1 class="h3 mb-1">Рейси</h1>
        <div class="text-secondary">Перегляд, додавання, редагування та видалення рейсів</div>
      </div>
      <RouterLink to="/flights/new" class="btn btn-brand">
        <i class="bi bi-plus-lg me-1"></i>Додати рейс
      </RouterLink>
    </div>

    <div class="card mb-3">
      <div class="card-body row g-2 align-items-center">
        <div class="col-md-6">
          <div class="input-group">
            <span class="input-group-text"><i class="bi bi-search"></i></span>
            <input v-model="search" class="form-control" placeholder="Пошук за номером, авіакомпанією або кодом аеропорту" />
          </div>
        </div>
        <div class="col-md-6 d-flex justify-content-md-end gap-2">
          <span class="align-self-center text-secondary small me-2">Знайдено: {{ filtered.length }}</span>
          <div class="btn-group" role="group" aria-label="Вигляд">
            <button :class="['btn btn-outline-secondary', { active: view === 'table' }]" @click="view = 'table'">
              <i class="bi bi-table"></i>
            </button>
            <button :class="['btn btn-outline-secondary', { active: view === 'cards' }]" @click="view = 'cards'">
              <i class="bi bi-grid-3x2-gap"></i>
            </button>
          </div>
          <button class="btn btn-outline-secondary" title="Оновити" @click="load"><i class="bi bi-arrow-clockwise"></i></button>
        </div>
      </div>
    </div>

    <ErrorAlert v-if="error" :message="error" :retry="load" />
    <LoadingSpinner v-else-if="loading" text="Завантаження рейсів…" />

    <template v-else>
      <div v-if="view === 'table'" class="card">
        <div class="table-responsive">
          <table class="table table-hover mb-0">
            <thead class="table-light">
              <tr>
                <th>Рейс</th>
                <th>Авіакомпанія</th>
                <th>Маршрут</th>
                <th>Виліт</th>
                <th>Прибуття</th>
                <th class="text-end">Ціна</th>
                <th class="text-end">Місця</th>
                <th>Статус</th>
                <th class="text-end">Дії</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="f in filtered" :key="f.id">
                <td class="fw-semibold">{{ f.flightNumber }}</td>
                <td>{{ f.airlineName }}</td>
                <td>{{ f.departureAirportCode }} <i class="bi bi-arrow-right text-secondary"></i> {{ f.arrivalAirportCode }}</td>
                <td>{{ formatDate(f.departureTime) }}, {{ formatTime(f.departureTime) }}</td>
                <td>{{ formatTime(f.arrivalTime) }}</td>
                <td class="text-end">{{ formatPrice(f.basePrice) }}</td>
                <td class="text-end">{{ f.availableSeats }}</td>
                <td><span :class="['badge', STATUSES[f.status]?.badge]">{{ STATUSES[f.status]?.label }}</span></td>
                <td class="text-end text-nowrap">
                  <RouterLink :to="`/flights/${f.id}/edit`" class="btn btn-outline-primary btn-sm me-1" title="Редагувати">
                    <i class="bi bi-pencil"></i>
                  </RouterLink>
                  <button class="btn btn-outline-danger btn-sm" title="Видалити" @click="toDelete = f">
                    <i class="bi bi-trash"></i>
                  </button>
                </td>
              </tr>
              <tr v-if="!filtered.length">
                <td colspan="9" class="text-center text-secondary py-4">Рейсів не знайдено</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div v-else class="row g-3">
        <div v-for="f in filtered" :key="f.id" class="col-md-6 col-lg-4">
          <FlightCard :flight="f" @delete="toDelete = $event" />
        </div>
      </div>
    </template>

    <ConfirmDialog
      :show="!!toDelete"
      title="Видалення рейсу"
      :busy="deleting"
      @cancel="toDelete = null"
      @confirm="confirmDelete"
    >
      <template v-if="toDelete">
        Видалити рейс <strong>{{ toDelete.flightNumber }}</strong>
        ({{ toDelete.departureAirportCode }} → {{ toDelete.arrivalAirportCode }})?
        <div class="text-secondary small mt-2">Рейс, на який уже є бронювання або квитки, сервер не дозволить видалити.</div>
      </template>
    </ConfirmDialog>
  </div>
</template>
