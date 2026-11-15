<script setup>
defineProps({
  message: { type: String, required: true },
  retry: { type: Function, default: null },
})
</script>

<template>
  <div class="alert alert-danger d-flex align-items-start gap-3" role="alert">
    <i class="bi bi-wifi-off fs-4"></i>
    <div class="flex-grow-1">
      <div class="fw-semibold">Не вдалося завантажити дані</div>
      <div>{{ message }}</div>
    </div>
    <button v-if="retry" class="btn btn-outline-danger btn-sm" @click="retry">
      <i class="bi bi-arrow-clockwise me-1"></i>Спробувати ще
    </button>
  </div>
</template>
