import { fileURLToPath, URL } from "node:url";

import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import vueDevTools from "vite-plugin-vue-devtools";

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue(), vueDevTools()],

  resolve: {
    alias: {
      "@": fileURLToPath(new URL("./src", import.meta.url)),
    },
  },
  server: {
    port: 3000, // 프론트엔드 개발 서버 포트 번호
    proxy: {
      "/api": {
        target: "http://192.168.210.52:8080", // Spring Boot 서버 URL
        changeOrigin: true, // CORS 문제 해결
        rewrite: (path) => path.replace(/^\/api/, ""), // /api 제거
      },
    },
  },
  define: {
    global: {},
    "process.env": {},
  },
});
