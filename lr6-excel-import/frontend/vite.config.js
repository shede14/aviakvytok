import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// Dev-сервер на 5174 проксіює /api на Spring Boot (порт 8082).
// npm run build:spring збирає застосунок у src/main/resources/static — тоді його віддає сам Spring Boot.
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5174,
    proxy: { '/api': 'http://localhost:8082' }
  },
  build: {
    rollupOptions: {
      output: {
        entryFileNames: 'assets/[name].js',
        chunkFileNames: 'assets/[name].js',
        assetFileNames: 'assets/[name][extname]'
      }
    }
  }
})
