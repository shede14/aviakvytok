<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { flightsApi, airlinesApi, airportsApi } from '../api/flights'
import { errorMessage, fieldErrors } from '../api/http'
import { notify } from '../components/toast'
import { STATUSES } from '../components/format'
import ErrorAlert from '../components/ErrorAlert.vue'
import LoadingSpinner from '../components/LoadingSpinner.vue'

// id є лише в режимі редагування (/flights/:id/edit)
const props = defineProps({ id: { type: String, default: null } })
const router = useRouter()
const isEdit = computed(() => !!props.id)

const form = reactive({
  flightNumber: '',
  airlineId: '',
  departureAirportId: '',
  arrivalAirportId: '',
  departureTime: '',
  arrivalTime: '',
  basePrice: '',
  availableSeats: '',
  status: 'SCHEDULED',
  description: '',
})

const airlines = ref([])
const airports = ref([])
const loading = ref(true)
const loadError = ref('')
const saving = ref(false)
const errors = ref({})
const submitError = ref('')

// '2026-12-01T07:30:00' -> '2026-12-01T07:30' для <input type="datetime-local">
const toInput = (iso) => (iso ? iso.slice(0, 16) : '')

function load() {
  loading.value = true
  loadError.value = ''
  const requests = [airlinesApi.getAll(), airportsApi.getAll()]
  if (isEdit.value) requests.push(flightsApi.getById(props.id))
  Promise.all(requests)
    .then(([al, ap, flight]) => {
      airlines.value = al
      airports.value = ap
      if (flight) {
        Object.assign(form, {
          flightNumber: flight.flightNumber,
          airlineId: flight.airlineId,
          departureAirportId: flight.departureAirportId,
          arrivalAirportId: flight.arrivalAirportId,
          departureTime: toInput(flight.departureTime),
          arrivalTime: toInput(flight.arrivalTime),
          basePrice: flight.basePrice,
          availableSeats: flight.availableSeats,
          status: flight.status,
          description: flight.description || '',
        })
      }
    })
    .catch((e) => (loadError.value = errorMessage(e)))
    .finally(() => (loading.value = false))
}

// CREATE / UPDATE
function submit() {
  saving.value = true
  errors.value = {}
  submitError.value = ''
  const payload = {
    ...form,
    flightNumber: form.flightNumber.trim(),
    airlineId: form.airlineId || null,
    departureAirportId: form.departureAirportId || null,
    arrivalAirportId: form.arrivalAirportId || null,
    departureTime: form.departureTime ? `${form.departureTime}:00` : null,
    arrivalTime: form.arrivalTime ? `${form.arrivalTime}:00` : null,
    basePrice: form.basePrice === '' ? null : Number(form.basePrice),
    availableSeats: form.availableSeats === '' ? null : Number(form.availableSeats),
  }
  const request = isEdit.value ? flightsApi.update(props.id, payload) : flightsApi.create(payload)
  request
    .then((saved) => {
      notify(isEdit.value ? `Рейс ${saved.flightNumber} оновлено` : `Рейс ${saved.flightNumber} додано`)
      router.push('/flights')
    })
    .catch((e) => {
      errors.value = fieldErrors(e)
      submitError.value = errorMessage(e)
    })
    .finally(() => (saving.value = false))
}

const cls = (field) => ['form-control', { 'is-invalid': errors.value[field] }]
const selCls = (field) => ['form-select', { 'is-invalid': errors.value[field] }]

onMounted(load)
</script>

<template>
  <div class="container" style="max-width: 860px">
    <nav aria-label="breadcrumb">
      <ol class="breadcrumb">
        <li class="breadcrumb-item"><RouterLink to="/flights">Рейси</RouterLink></li>
        <li class="breadcrumb-item active">{{ isEdit ? 'Редагування' : 'Новий рейс' }}</li>
      </ol>
    </nav>
    <h1 class="h3 mb-4">{{ isEdit ? `Редагування рейсу ${form.flightNumber}` : 'Новий рейс' }}</h1>

    <ErrorAlert v-if="loadError" :message="loadError" :retry="load" />
    <LoadingSpinner v-else-if="loading" />

    <form v-else class="card" novalidate @submit.prevent="submit">
      <div class="card-body p-4">
        <div v-if="submitError" class="alert alert-danger"><i class="bi bi-exclamation-circle me-2"></i>{{ submitError }}</div>

        <div class="row g-3">
          <div class="col-md-4">
            <label class="form-label">Номер рейсу</label>
            <input v-model="form.flightNumber" :class="cls('flightNumber')" placeholder="PS101" maxlength="10" />
            <div class="invalid-feedback">{{ errors.flightNumber }}</div>
          </div>
          <div class="col-md-8">
            <label class="form-label">Авіакомпанія</label>
            <select v-model="form.airlineId" :class="selCls('airlineId')">
              <option value="" disabled>Оберіть авіакомпанію</option>
              <option v-for="a in airlines" :key="a.id" :value="a.id">{{ a.iataCode }} — {{ a.name }}</option>
            </select>
            <div class="invalid-feedback">{{ errors.airlineId }}</div>
          </div>

          <div class="col-md-6">
            <label class="form-label">Аеропорт вильоту</label>
            <select v-model="form.departureAirportId" :class="selCls('departureAirportId')">
              <option value="" disabled>Оберіть аеропорт</option>
              <option v-for="a in airports" :key="a.id" :value="a.id">{{ a.iataCode }} — {{ a.city }}</option>
            </select>
            <div class="invalid-feedback">{{ errors.departureAirportId }}</div>
          </div>
          <div class="col-md-6">
            <label class="form-label">Аеропорт прибуття</label>
            <select v-model="form.arrivalAirportId" :class="selCls('arrivalAirportId')">
              <option value="" disabled>Оберіть аеропорт</option>
              <option v-for="a in airports" :key="a.id" :value="a.id">{{ a.iataCode }} — {{ a.city }}</option>
            </select>
            <div class="invalid-feedback">{{ errors.arrivalAirportId }}</div>
          </div>

          <div class="col-md-6">
            <label class="form-label">Виліт</label>
            <input v-model="form.departureTime" type="datetime-local" :class="cls('departureTime')" />
            <div class="invalid-feedback">{{ errors.departureTime }}</div>
          </div>
          <div class="col-md-6">
            <label class="form-label">Прибуття</label>
            <input v-model="form.arrivalTime" type="datetime-local" :class="cls('arrivalTime')" />
            <div class="invalid-feedback">{{ errors.arrivalTime }}</div>
          </div>

          <div class="col-md-4">
            <label class="form-label">Базова ціна, грн</label>
            <input v-model="form.basePrice" type="number" min="0" step="0.01" :class="cls('basePrice')" />
            <div class="invalid-feedback">{{ errors.basePrice }}</div>
          </div>
          <div class="col-md-4">
            <label class="form-label">Вільних місць</label>
            <input v-model="form.availableSeats" type="number" min="0" :class="cls('availableSeats')" />
            <div class="invalid-feedback">{{ errors.availableSeats }}</div>
          </div>
          <div class="col-md-4">
            <label class="form-label">Статус</label>
            <select v-model="form.status" class="form-select">
              <option v-for="(s, key) in STATUSES" :key="key" :value="key">{{ s.label }}</option>
            </select>
          </div>

          <div class="col-12">
            <label class="form-label">Опис</label>
            <textarea v-model="form.description" class="form-control" rows="3" placeholder="Додаткова інформація про рейс"></textarea>
          </div>
        </div>
      </div>
      <div class="card-footer bg-white d-flex justify-content-end gap-2 p-3">
        <RouterLink to="/flights" class="btn btn-outline-secondary">Скасувати</RouterLink>
        <button type="submit" class="btn btn-brand" :disabled="saving">
          <span v-if="saving" class="spinner-border spinner-border-sm me-1"></span>
          <i v-else class="bi bi-check-lg me-1"></i>{{ isEdit ? 'Зберегти зміни' : 'Додати рейс' }}
        </button>
      </div>
    </form>
  </div>
</template>
