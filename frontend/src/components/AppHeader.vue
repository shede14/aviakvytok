<script setup>
// Верхнє навігаційне меню; RouterLink перемикає сторінки без перезавантаження
const links = [
  { to: '/', label: 'Головна', icon: 'bi-house' },
  { to: '/flights', label: 'Рейси', icon: 'bi-airplane' },
  { to: '/airlines', label: 'Авіакомпанії', icon: 'bi-buildings' },
  { to: '/about', label: 'Про проєкт', icon: 'bi-info-circle' },
]
</script>

<template>
  <header>
    <nav class="navbar navbar-expand-md navbar-dark bg-brand shadow-sm">
      <div class="container">
        <RouterLink class="navbar-brand d-flex align-items-center gap-2 fw-semibold" to="/">
          <span class="navbar-brand-mark"><i class="bi bi-airplane-fill"></i></span>
          АвіаКвиток
        </RouterLink>
        <button
          class="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#mainNav"
          aria-controls="mainNav"
          aria-expanded="false"
          aria-label="Меню"
        >
          <span class="navbar-toggler-icon"></span>
        </button>
        <div id="mainNav" class="collapse navbar-collapse">
          <ul class="navbar-nav ms-auto">
            <li v-for="link in links" :key="link.to" class="nav-item">
              <RouterLink
                class="nav-link px-3"
                :to="link.to"
                :exact-active-class="link.to === '/' ? 'active' : ''"
                :active-class="link.to === '/' ? '' : 'active'"
              >
                <i :class="['bi', link.icon, 'me-1']"></i>{{ link.label }}
              </RouterLink>
            </li>
          </ul>
        </div>
      </div>
    </nav>
  </header>
</template>
