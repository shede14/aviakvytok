<script setup>
import AppHeader from './components/AppHeader.vue'
import AppFooter from './components/AppFooter.vue'
import ToastNotice from './components/ToastNotice.vue'
</script>

<template>
  <AppHeader />
  <main class="py-4">
    <RouterView />
  </main>
  <AppFooter />
  <ToastNotice />
</template>
