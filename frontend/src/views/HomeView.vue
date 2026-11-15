<script setup>
import { ref, onMounted } from 'vue'
import { flightsApi, airlinesApi, bookingsApi } from '../api/flights'
import { errorMessage } from '../api/http'
import ErrorAlert from '../components/ErrorAlert.vue'
import FlightCard from '../components/FlightCard.vue'

const stats = ref({ flights: 0, airlines: 0, bookings: 0 })
const upcoming = ref([])
const loading = ref(true)
const error = ref('')

function load() {
  loading.value = true
  error.value = ''
  // Три запити паралельно; помилку зв'язку ловимо в .catch()
  Promise.all([flightsApi.getAll(), airlinesApi.getAll(), bookingsApi.getAll()])
    .then(([flights, airlines, bookings]) => {
      stats.value = { flights: flights.length, airlines: airlines.length, bookings: bookings.length }
      upcoming.value = [...flights]
        .sort((a, b) => new Date(a.departureTime) - new Date(b.departureTime))
        .slice(0, 3)
    })
    .catch((e) => (error.value = errorMessage(e)))
    .finally(() => (loading.value = false))
}

onMounted(load)
</script>

<template>
  <div class="container">
    <section class="hero p-4 p-md-5 mb-4">
      <div class="row align-items-center g-4">
        <div class="col-lg-8">
          <h1 class="display-6 fw-semibold mb-3">Онлайн-продаж авіаквитків</h1>
          <p class="lead mb-4 opacity-75">
            Клієнтська частина інформаційної системи «АвіаКвиток». Дані про рейси, авіакомпанії та бронювання
            надходять з REST API на Spring Boot і зберігаються в PostgreSQL.
          </p>
          <div class="d-flex flex-wrap gap-2">
            <RouterLink to="/flights" class="btn btn-light"><i class="bi bi-airplane me-1"></i>Переглянути рейси</RouterLink>
            <RouterLink to="/flights/new" class="btn btn-outline-light"><i class="bi bi-plus-lg me-1"></i>Додати рейс</RouterLink>
          </div>
        </div>
        <div class="col-lg-4 d-none d-lg-block text-center">
          <i class="bi bi-airplane-engines" style="font-size: 7rem; opacity: 0.85"></i>
        </div>
      </div>
    </section>

    <ErrorAlert v-if="error" :message="error" :retry="load" />

    <div class="row g-3 mb-4">
      <div v-for="s in [
        { key: 'flights', label: 'Рейсів у системі', icon: 'bi-airplane' },
        { key: 'airlines', label: 'Авіакомпаній', icon: 'bi-buildings' },
        { key: 'bookings', label: 'Бронювань', icon: 'bi-ticket-perforated' },
      ]" :key="s.key" class="col-md-4">
        <div class="card stat-card">
          <div class="card-body d-flex align-items-center gap-3">
            <span class="stat-icon"><i :class="['bi', s.icon]"></i></span>
            <div>
              <div class="fs-3 fw-semibold">
                <span v-if="loading" class="placeholder col-4"></span>
                <template v-else>{{ stats[s.key] }}</template>
              </div>
              <div class="text-secondary">{{ s.label }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="d-flex justify-content-between align-items-center mb-3">
      <h2 class="h5 mb-0">Найближчі рейси</h2>
      <RouterLink to="/flights" class="small">Усі рейси <i class="bi bi-arrow-right"></i></RouterLink>
    </div>
    <div class="row g-3">
      <div v-for="f in upcoming" :key="f.id" class="col-md-6 col-lg-4">
        <FlightCard :flight="f" @delete="$router.push('/flights')" />
      </div>
      <div v-if="!loading && !error && !upcoming.length" class="col-12 text-secondary">Рейсів поки немає.</div>
    </div>
  </div>
</template>
