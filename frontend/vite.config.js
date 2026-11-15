import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// Dev-сервер Vite працює на порту 5173 і звертається до Spring Boot (порт 8080)
// напряму; дозвіл на перехресні запити дає CorsConfig у бекенді.
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    strictPort: true,
  },
  build: {
    // Сталі імена файлів: збірка в src/main/resources/static не накопичує старих версій
    rollupOptions: {
      output: {
        entryFileNames: 'assets/[name].js',
        chunkFileNames: 'assets/[name].js',
        assetFileNames: 'assets/[name][extname]',
      },
    },
  },
})
