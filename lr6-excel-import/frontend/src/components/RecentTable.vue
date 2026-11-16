<template>
  <div class="card">
    <div class="card-body">
      <h6 class="card-title">Останні імпортовані записи</h6>
      <div class="table-responsive">
        <table class="table table-sm table-hover align-middle mb-0">
          <thead class="table-light">
            <tr>
              <th>Код</th><th>Пасажир</th><th>Рейс</th><th>Напрямок</th><th>Дата</th>
              <th>Клас</th><th>Статус</th><th class="text-end">Ціна, грн</th><th>Файл</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="s in rows" :key="s.id">
              <td><code>{{ s.bookingCode }}</code></td>
              <td>{{ s.passengerName }}</td>
              <td>{{ s.flightNumber }}</td>
              <td>{{ s.direction }}</td>
              <td>{{ formatDate(s.saleDate) }}</td>
              <td>{{ s.fareClass }}</td>
              <td><span class="badge" :class="badge(s.status)">{{ s.status }}</span></td>
              <td class="text-end">{{ money(s.price) }}</td>
              <td class="small-muted">{{ s.importFile }}</td>
            </tr>
            <tr v-if="!rows.length"><td colspan="9" class="text-center text-secondary py-3">Записів ще немає</td></tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({ rows: { type: Array, default: () => [] } })

const formatDate = (d) => d.split('-').reverse().join('.')
const money = (v) => Number(v).toLocaleString('uk-UA', { minimumFractionDigits: 2 })
const badge = (s) => ({ 'Оплачено': 'text-bg-success', 'Повернено': 'text-bg-warning', 'Скасовано': 'text-bg-secondary' }[s])
</script>
