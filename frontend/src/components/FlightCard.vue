<script setup>
// Картка рейсу (ItemCard): маршрут, час, ціна, кнопки дій
import { formatDate, formatTime, formatPrice, duration, STATUSES } from './format'

defineProps({ flight: { type: Object, required: true } })
defineEmits(['delete'])
</script>

<template>
  <div class="card h-100">
    <div class="card-body">
      <div class="d-flex justify-content-between align-items-start mb-3">
        <div>
          <div class="fw-bold fs-5">{{ flight.flightNumber }}</div>
          <div class="text-secondary small">{{ flight.airlineName }}</div>
        </div>
        <span :class="['badge', STATUSES[flight.status]?.badge]">{{ STATUSES[flight.status]?.label }}</span>
      </div>

      <div class="d-flex align-items-center mb-2">
        <div class="text-center">
          <div class="iata">{{ flight.departureAirportCode }}</div>
          <div class="small">{{ formatTime(flight.departureTime) }}</div>
        </div>
        <div class="route-line"><i class="bi bi-airplane-fill"></i></div>
        <div class="text-center">
          <div class="iata">{{ flight.arrivalAirportCode }}</div>
          <div class="small">{{ formatTime(flight.arrivalTime) }}</div>
        </div>
      </div>
      <div class="text-center text-secondary small mb-3">
        {{ formatDate(flight.departureTime) }} · {{ duration(flight.departureTime, flight.arrivalTime) }}
      </div>

      <div class="d-flex justify-content-between align-items-center">
        <div>
          <div class="fs-5 fw-semibold">{{ formatPrice(flight.basePrice) }}</div>
          <div class="small text-secondary">{{ flight.availableSeats }} вільних місць</div>
        </div>
        <div class="btn-group">
          <RouterLink :to="`/flights/${flight.id}/edit`" class="btn btn-outline-primary btn-sm" title="Редагувати">
            <i class="bi bi-pencil"></i>
          </RouterLink>
          <button class="btn btn-outline-danger btn-sm" title="Видалити" @click="$emit('delete', flight)">
            <i class="bi bi-trash"></i>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
