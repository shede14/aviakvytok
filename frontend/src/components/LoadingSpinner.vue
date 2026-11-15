<script setup>
defineProps({ text: { type: String, default: 'Завантаження…' } })
</script>

<template>
  <div class="d-flex align-items-center justify-content-center gap-2 py-5 text-secondary">
    <div class="spinner-border spinner-border-sm" role="status"></div>
    <span>{{ text }}</span>
  </div>
</template>
