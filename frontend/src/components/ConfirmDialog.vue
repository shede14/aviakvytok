<script setup>
// Модальне вікно підтвердження (Bootstrap-розмітка, керується з Vue)
defineProps({
  show: Boolean,
  title: { type: String, default: 'Підтвердження' },
  busy: Boolean,
})
defineEmits(['confirm', 'cancel'])
</script>

<template>
  <template v-if="show">
    <div class="modal d-block" tabindex="-1" role="dialog">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ title }}</h5>
            <button type="button" class="btn-close" aria-label="Закрити" @click="$emit('cancel')"></button>
          </div>
          <div class="modal-body"><slot /></div>
          <div class="modal-footer">
            <button class="btn btn-outline-secondary" :disabled="busy" @click="$emit('cancel')">Скасувати</button>
            <button class="btn btn-danger" :disabled="busy" @click="$emit('confirm')">
              <span v-if="busy" class="spinner-border spinner-border-sm me-1"></span>
              <i v-else class="bi bi-trash me-1"></i>Видалити
            </button>
          </div>
        </div>
      </div>
    </div>
    <div class="modal-backdrop show"></div>
  </template>
</template>
