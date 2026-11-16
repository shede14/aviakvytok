<template>
  <div class="card h-100">
    <div class="card-body">
      <h5 class="card-title"><i class="bi bi-file-earmark-excel text-success me-1"></i>Імпорт продажів з Excel</h5>
      <p class="small-muted mb-3">
        Файл .xlsx із колонками: Код бронювання, Пасажир, Рейс, Напрямок, Дата продажу, Клас, Статус, Ціна, грн.
        <a href="/template.xlsx" download><i class="bi bi-download"></i> Завантажити шаблон</a>
      </p>

      <div class="file-drop p-3 mb-3 d-flex align-items-center gap-3">
        <!-- стандартне поле вибору файлу; приховане, щоб підпис кнопки був українською -->
        <input ref="input" id="excel-file" type="file" class="d-none" accept=".xlsx"
               @change="onSelect" :disabled="busy">
        <label for="excel-file" class="btn btn-outline-primary mb-0 text-nowrap flex-shrink-0" :class="{ disabled: busy }">
          <i class="bi bi-folder2-open me-1"></i>Обрати файл…
        </label>
        <div class="small text-truncate">
          <template v-if="file">
            <i class="bi bi-file-earmark-spreadsheet text-success"></i> <b>{{ file.name }}</b>
            <span class="small-muted">· {{ (file.size / 1024).toFixed(1) }} КБ</span>
          </template>
          <span v-else class="small-muted">Файл не вибрано</span>
        </div>
      </div>

      <div class="d-flex gap-2 align-items-center">
        <button class="btn btn-success" :disabled="!file || busy" @click="upload">
          <span v-if="busy" class="spinner-border spinner-border-sm me-1"></span>
          <i v-else class="bi bi-cloud-upload me-1"></i>Імпортувати
        </button>
        <span v-if="busy" class="small-muted">Завантаження {{ progress }}%…</span>
      </div>

      <!-- підсумок імпорту -->
      <div v-if="result" class="alert mt-3 mb-0" :class="result.imported ? 'alert-success' : 'alert-warning'">
        <div class="fw-semibold mb-1">
          <i class="bi" :class="result.imported ? 'bi-check-circle' : 'bi-exclamation-triangle'"></i>
          {{ result.fileName }}: імпортовано {{ result.imported }} з {{ result.totalRows }} рядків
          <span class="fw-normal">({{ result.durationMs }} мс)</span>
        </div>
        <div v-if="result.skipped" class="small">
          Пропущено {{ result.skipped }}:
          <ul class="mb-0 ps-3">
            <li v-for="e in result.errors.slice(0, 6)" :key="e.row + e.message">рядок {{ e.row }} — {{ e.message }}</li>
            <li v-if="result.errors.length > 6">… та ще {{ result.errors.length - 6 }}</li>
          </ul>
        </div>
      </div>
      <div v-if="error" class="alert alert-danger mt-3 mb-0"><i class="bi bi-x-octagon me-1"></i>{{ error }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { api, errorMessage } from '../api/http'

const emit = defineEmits(['imported'])

const input = ref(null)
const file = ref(null)
const busy = ref(false)
const progress = ref(0)
const result = ref(null)
const error = ref('')

function onSelect(e) {
  file.value = e.target.files[0] || null
  result.value = null
  error.value = ''
}

async function upload() {
  busy.value = true
  progress.value = 0
  error.value = ''
  result.value = null
  try {
    result.value = await api.importExcel(file.value, (p) => (progress.value = p))
    emit('imported', result.value)        // батьківський компонент одразу оновлює графіки
    file.value = null
    input.value.value = ''
  } catch (e) {
    error.value = errorMessage(e)
  } finally {
    busy.value = false
  }
}
</script>
