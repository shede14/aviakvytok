<script setup>
const layers = [
  { icon: 'bi-window', title: 'Клієнт (Vue 3)', text: 'Сторінки, компоненти, vue-router, axios, Bootstrap 5. Працює в браузері, порт 5173.' },
  { icon: 'bi-hdd-network', title: 'Сервер (Spring Boot 4)', text: 'REST API: Controller → Service → Repository, валідація, CORS. Порт 8080.' },
  { icon: 'bi-database', title: 'База даних (PostgreSQL)', text: 'Таблиці генерує Hibernate з JPA-сутностей (ЛР2).' },
]
</script>

<template>
  <div class="container" style="max-width: 960px">
    <h1 class="h3 mb-3">Про проєкт</h1>
    <p class="lead">
      «АвіаКвиток» — навчальна інформаційна система онлайн-агентства з продажу авіаквитків, що розробляється в межах
      дисципліни «Технології Індустрії 4.0, 5.0 в інформаційних управляючих системах».
    </p>

    <div class="row g-3 my-3">
      <div v-for="l in layers" :key="l.title" class="col-md-4">
        <div class="card h-100">
          <div class="card-body">
            <i :class="['bi', l.icon, 'fs-2 text-primary']"></i>
            <h2 class="h6 mt-2">{{ l.title }}</h2>
            <p class="small text-secondary mb-0">{{ l.text }}</p>
          </div>
        </div>
      </div>
    </div>

    <div class="card">
      <div class="card-body">
        <h2 class="h6">Як працює застосунок</h2>
        <ol class="mb-0">
          <li>Користувач відкриває Vue-інтерфейс у браузері.</li>
          <li>Клієнт надсилає HTTP-запит через axios до Spring Boot.</li>
          <li>Spring Boot звертається до PostgreSQL через Spring Data JPA.</li>
          <li>Дані повертаються у форматі JSON і динамічно відображаються на сторінці.</li>
        </ol>
      </div>
    </div>

    <p class="text-secondary small mt-4 mb-0">
      Автор: Коць Ярослав, група ІУС-601, КНЕУ. Код:
      <a href="https://github.com/shede14/aviakvytok" target="_blank" rel="noopener">github.com/shede14/aviakvytok</a>
    </p>
  </div>
</template>
