import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

// Маршрути застосунку: перехід між сторінками без перезавантаження браузера
const routes = [
  { path: '/', name: 'home', component: HomeView, meta: { title: 'Головна' } },
  {
    path: '/flights',
    name: 'flights',
    component: () => import('../views/FlightsView.vue'),
    meta: { title: 'Рейси' },
  },
  {
    path: '/flights/new',
    name: 'flight-new',
    component: () => import('../views/FlightFormView.vue'),
    meta: { title: 'Новий рейс' },
  },
  {
    path: '/flights/:id/edit',
    name: 'flight-edit',
    component: () => import('../views/FlightFormView.vue'),
    props: true,
    meta: { title: 'Редагування рейсу' },
  },
  {
    path: '/airlines',
    name: 'airlines',
    component: () => import('../views/AirlinesView.vue'),
    meta: { title: 'Авіакомпанії' },
  },
  {
    path: '/about',
    name: 'about',
    component: () => import('../views/AboutView.vue'),
    meta: { title: 'Про проєкт' },
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    component: () => import('../views/NotFoundView.vue'),
    meta: { title: 'Сторінку не знайдено' },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  linkActiveClass: 'active',
  scrollBehavior: () => ({ top: 0 }),
})

router.afterEach((to) => {
  document.title = `${to.meta.title} · АвіаКвиток`
})

export default router
