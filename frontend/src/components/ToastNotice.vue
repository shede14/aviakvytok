<script setup>
import { toastState } from './toast'

const icons = { success: 'bi-check-circle-fill', danger: 'bi-x-circle-fill', warning: 'bi-exclamation-triangle-fill' }
</script>

<template>
  <div class="toast-container position-fixed top-0 end-0 p-3">
    <div
      v-for="t in toastState.items"
      :key="t.id"
      :class="['toast show align-items-center border-0 text-bg-' + t.type]"
      role="alert"
    >
      <div class="d-flex">
        <div class="toast-body">
          <i :class="['bi', icons[t.type], 'me-2']"></i>{{ t.message }}
        </div>
      </div>
    </div>
  </div>
</template>
