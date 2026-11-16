<template>
  <nav class="navbar navbar-dark bg-primary mb-4">
    <div class="container">
      <span class="navbar-brand"><i class="bi bi-airplane-fill me-2"></i>АвіаКвиток <span class="fw-normal opacity-75">· Аналітика продажів</span></span>
      <span class="navbar-text small">Spring Boot 3 · Apache POI · Vue 3 · Chart.js</span>
    </div>
  </nav>

  <main class="container pb-4">
    <div v-if="loadError" class="alert alert-danger">
      <i class="bi bi-wifi-off me-1"></i>{{ loadError }}
      <button class="btn btn-sm btn-outline-danger ms-2" @click="load">Спробувати ще</button>
    </div>

    <div class="row g-3 mb-3">
      <div class="col-lg-5">
        <ImportPanel @imported="load" />
      </div>
      <div class="col-lg-7">
        <div class="row g-3 h-100">
          <div class="col-6" v-for="k in kpis" :key="k.label">
            <div class="card kpi h-100">
              <div class="card-body">
                <div class="label"><i class="bi me-1" :class="k.icon"></i>{{ k.label }}</div>
                <div class="value">{{ k.value }}</div>
                <div class="small-muted">{{ k.hint }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="row g-3 mb-3">
      <div class="col-lg-7">
        <AnalyticsChart type="bar" title="Продажі за датами" subtitle="Кількість доданих квитків за датою продажу"
                        :items="stats.byDate" color="#0d6efd" />
      </div>
      <div class="col-lg-5">
        <AnalyticsChart type="pie" title="Класи обслуговування" subtitle="Відсоткове співвідношення класів"
                        :items="stats.byFareClass" />
      </div>
      <div class="col-lg-7">
        <AnalyticsChart type="bar" title="Продажі за напрямками" subtitle="Кількість квитків за категорією «напрямок»"
                        :items="stats.byDirection" color="#20c997" horizontal />
      </div>
      <div class="col-lg-5">
        <AnalyticsChart type="pie" title="Статуси продажів" subtitle="Оплачено / повернено / скасовано"
                        :items="stats.byStatus" />
      </div>
    </div>

    <RecentTable :rows="recent" />

    <div class="text-end mt-3">
      <template v-if="confirmClear">
        <span class="small-muted me-2">Видалити всі імпортовані продажі?</span>
        <button class="btn btn-sm btn-danger me-1" @click="clearAll">Так, видалити</button>
        <button class="btn btn-sm btn-outline-secondary" @click="confirmClear = false">Скасувати</button>
      </template>
      <button v-else class="btn btn-sm btn-outline-secondary" :disabled="!stats.total" @click="confirmClear = true">
        <i class="bi bi-trash3 me-1"></i>Очистити дані
      </button>
    </div>
  </main>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import ImportPanel from './components/ImportPanel.vue'
import AnalyticsChart from './components/AnalyticsChart.vue'
import RecentTable from './components/RecentTable.vue'
import { api, errorMessage } from './api/http'

const empty = { total: 0, paidRevenue: 0, directions: 0, byDate: [], byDirection: [], byFareClass: [], byStatus: [] }
const stats = ref({ ...empty })
const recent = ref([])
const loadError = ref('')

/** GET-запити статистики та останніх записів; викликається при старті і після кожного імпорту. */
async function load() {
  try {
    const [s, r] = await Promise.all([api.stats(), api.recent()])
    stats.value = s
    recent.value = r
    loadError.value = ''
  } catch (e) {
    loadError.value = errorMessage(e)
  }
}

const confirmClear = ref(false)
async function clearAll() {
  confirmClear.value = false
  await api.clear().catch((e) => (loadError.value = errorMessage(e)))
  await load()
}

const money = (v) => Number(v).toLocaleString('uk-UA', { maximumFractionDigits: 0 })
const kpis = computed(() => [
  { label: 'Продано квитків', value: stats.value.total, hint: 'усього записів у базі', icon: 'bi-ticket-perforated' },
  { label: 'Виручка, грн', value: money(stats.value.paidRevenue), hint: 'лише оплачені квитки', icon: 'bi-cash-coin' },
  { label: 'Напрямків', value: stats.value.directions, hint: 'унікальних маршрутів', icon: 'bi-signpost-split' },
  {
    label: 'Останній імпорт',
    value: stats.value.lastImportAt ? new Date(stats.value.lastImportAt).toLocaleTimeString('uk-UA') : '—',
    hint: stats.value.lastImportFile || 'ще не було',
    icon: 'bi-clock-history'
  }
])

onMounted(load)
</script>
