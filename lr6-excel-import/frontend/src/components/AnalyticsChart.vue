<template>
  <div class="card h-100">
    <div class="card-body">
      <h6 class="card-title mb-0">{{ title }}</h6>
      <div class="small-muted mb-2">{{ subtitle }}</div>
      <div class="chart-box">
        <Bar v-if="loaded && type === 'bar'" :data="chartData" :options="barOptions" />
        <Pie v-else-if="loaded && type === 'pie'" :data="chartData" :options="pieOptions" />
        <div v-else class="h-100 d-flex align-items-center justify-content-center text-secondary">
          <span><i class="bi bi-bar-chart me-1"></i>Немає даних — імпортуйте файл Excel</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { Bar, Pie } from 'vue-chartjs'
import {
  Chart as ChartJS, Title, Tooltip, Legend, BarElement, ArcElement, CategoryScale, LinearScale
} from 'chart.js'

ChartJS.register(Title, Tooltip, Legend, BarElement, ArcElement, CategoryScale, LinearScale)

const props = defineProps({
  type: { type: String, default: 'bar' },          // 'bar' | 'pie'
  title: { type: String, required: true },
  subtitle: { type: String, default: '' },
  items: { type: Array, default: () => [] },        // [{ label, count }] з сервера
  color: { type: String, default: '#0d6efd' },
  horizontal: { type: Boolean, default: false }
})

const PALETTE = ['#0d6efd', '#20c997', '#fd7e14', '#6f42c1', '#dc3545', '#ffc107', '#0dcaf0', '#6c757d']

const loaded = computed(() => props.items.length > 0)
const total = computed(() => props.items.reduce((s, i) => s + i.count, 0))

const chartData = computed(() => ({
  labels: props.items.map((i) => i.label),
  datasets: [{
    label: 'Кількість продажів',
    data: props.items.map((i) => i.count),
    backgroundColor: props.type === 'pie' ? PALETTE : props.color,
    borderRadius: props.type === 'bar' ? 4 : 0,
    borderColor: props.type === 'pie' ? '#fff' : undefined,
    borderWidth: props.type === 'pie' ? 2 : 0
  }]
}))

const percent = (v) => (total.value ? ((v * 100) / total.value).toFixed(1).replace('.', ',') : '0') + '%'

const barOptions = computed(() => ({
  responsive: true,
  maintainAspectRatio: false,
  indexAxis: props.horizontal ? 'y' : 'x',
  plugins: { legend: { display: false } },
  scales: {
    x: { grid: { display: props.horizontal }, ticks: { precision: 0 } },
    y: { beginAtZero: true, ticks: { precision: 0 }, grid: { display: !props.horizontal } }
  }
}))

const pieOptions = computed(() => ({
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    // у легенді — частка у відсотках
    legend: {
      position: 'right',
      labels: {
        generateLabels: (chart) => ChartJS.overrides.pie.plugins.legend.labels.generateLabels(chart)
          .map((l) => ({ ...l, text: `${l.text} — ${percent(props.items[l.index].count)}` }))
      }
    },
    tooltip: {
      callbacks: { label: (ctx) => ` ${ctx.raw} шт. (${percent(ctx.raw)})` }
    }
  }
}))
</script>
