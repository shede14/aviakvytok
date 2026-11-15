<script setup>
import { ref, onMounted } from 'vue'
import { airlinesApi } from '../api/flights'
import { errorMessage } from '../api/http'
import ErrorAlert from '../components/ErrorAlert.vue'
import LoadingSpinner from '../components/LoadingSpinner.vue'

const airlines = ref([])
const loading = ref(true)
const error = ref('')

function load() {
  loading.value = true
  error.value = ''
  airlinesApi
    .getAll()
    .then((data) => (airlines.value = data))
    .catch((e) => (error.value = errorMessage(e)))
    .finally(() => (loading.value = false))
}

onMounted(load)
</script>

<template>
  <div class="container">
    <h1 class="h3 mb-1">Авіакомпанії</h1>
    <p class="text-secondary mb-4">Перевізники, рейси яких продає агентство</p>

    <ErrorAlert v-if="error" :message="error" :retry="load" />
    <LoadingSpinner v-else-if="loading" />

    <div v-else class="row g-3">
      <div v-for="a in airlines" :key="a.id" class="col-md-6 col-lg-4">
        <div class="card h-100">
          <div class="card-body">
            <div class="d-flex align-items-center gap-3 mb-3">
              <span class="badge bg-brand fs-5 px-3 py-2">{{ a.iataCode }}</span>
              <div>
                <div class="fw-semibold">{{ a.name }}</div>
                <div class="text-secondary small"><i class="bi bi-geo-alt me-1"></i>{{ a.country || '—' }}</div>
              </div>
            </div>
            <p class="small mb-3">{{ a.description || 'Опис відсутній.' }}</p>
            <span class="badge text-bg-light border"><i class="bi bi-airplane me-1"></i>Рейсів: {{ a.flightsCount }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
