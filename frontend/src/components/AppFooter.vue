<script setup>
const year = new Date().getFullYear()
</script>

<template>
  <footer class="border-top bg-white py-3 mt-auto">
    <div class="container d-flex flex-column flex-md-row justify-content-between gap-2 small text-secondary">
      <span>© {{ year }} АвіаКвиток · ЛР4, Коць Ярослав, ІУС-601</span>
      <span>Vue 3 · Spring Boot 4 · PostgreSQL</span>
    </div>
  </footer>
</template>
