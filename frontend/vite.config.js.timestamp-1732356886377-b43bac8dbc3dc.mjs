// vite.config.js
import { fileURLToPath, URL } from "node:url";
import { defineConfig } from "file:///C:/2024-careerfit/frontend/node_modules/vite/dist/node/index.js";
import vue from "file:///C:/2024-careerfit/frontend/node_modules/@vitejs/plugin-vue/dist/index.mjs";
import vueDevTools from "file:///C:/2024-careerfit/frontend/node_modules/vite-plugin-vue-devtools/dist/vite.mjs";
var __vite_injected_original_import_meta_url =
  "file:///C:/2024-careerfit/frontend/vite.config.js";
var vite_config_default = defineConfig({
  plugins: [vue(), vueDevTools()],
  resolve: {
    alias: {
      "@": fileURLToPath(
        new URL("./src", __vite_injected_original_import_meta_url)
      ),
    },
  },
  server: {
    port: 3e3,
    // 프론트엔드 개발 서버 포트 번호
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        // Spring Boot 서버 URL
        changeOrigin: true,
        // CORS 문제 해결
        rewrite: (path) => path.replace(/^\/api/, ""),
        // /api 제거
      },
    },
  },
  define: {
    global: {},
  },
});
export { vite_config_default as default };
//# sourceMappingURL=data:application/json;base64,ewogICJ2ZXJzaW9uIjogMywKICAic291cmNlcyI6IFsidml0ZS5jb25maWcuanMiXSwKICAic291cmNlc0NvbnRlbnQiOiBbImNvbnN0IF9fdml0ZV9pbmplY3RlZF9vcmlnaW5hbF9kaXJuYW1lID0gXCJDOlxcXFwyMDI0LWNhcmVlcmZpdFxcXFxmcm9udGVuZFwiO2NvbnN0IF9fdml0ZV9pbmplY3RlZF9vcmlnaW5hbF9maWxlbmFtZSA9IFwiQzpcXFxcMjAyNC1jYXJlZXJmaXRcXFxcZnJvbnRlbmRcXFxcdml0ZS5jb25maWcuanNcIjtjb25zdCBfX3ZpdGVfaW5qZWN0ZWRfb3JpZ2luYWxfaW1wb3J0X21ldGFfdXJsID0gXCJmaWxlOi8vL0M6LzIwMjQtY2FyZWVyZml0L2Zyb250ZW5kL3ZpdGUuY29uZmlnLmpzXCI7aW1wb3J0IHsgZmlsZVVSTFRvUGF0aCwgVVJMIH0gZnJvbSBcIm5vZGU6dXJsXCI7XHJcblxyXG5pbXBvcnQgeyBkZWZpbmVDb25maWcgfSBmcm9tIFwidml0ZVwiO1xyXG5pbXBvcnQgdnVlIGZyb20gXCJAdml0ZWpzL3BsdWdpbi12dWVcIjtcclxuaW1wb3J0IHZ1ZURldlRvb2xzIGZyb20gXCJ2aXRlLXBsdWdpbi12dWUtZGV2dG9vbHNcIjtcclxuXHJcbi8vIGh0dHBzOi8vdml0ZS5kZXYvY29uZmlnL1xyXG5leHBvcnQgZGVmYXVsdCBkZWZpbmVDb25maWcoe1xyXG4gIHBsdWdpbnM6IFt2dWUoKSwgdnVlRGV2VG9vbHMoKV0sXHJcblxyXG4gIHJlc29sdmU6IHtcclxuICAgIGFsaWFzOiB7XHJcbiAgICAgIFwiQFwiOiBmaWxlVVJMVG9QYXRoKG5ldyBVUkwoXCIuL3NyY1wiLCBpbXBvcnQubWV0YS51cmwpKSxcclxuICAgIH0sXHJcbiAgfSxcclxuICBzZXJ2ZXI6IHtcclxuICAgIHBvcnQ6IDMwMDAsIC8vIFx1RDUwNFx1Qjg2MFx1RDJCOFx1QzVENFx1QjREQyBcdUFDMUNcdUJDMUMgXHVDMTFDXHVCQzg0IFx1RDNFQ1x1RDJCOCBcdUJDODhcdUQ2MzhcclxuICAgIHByb3h5OiB7XHJcbiAgICAgIFwiL2FwaVwiOiB7XHJcbiAgICAgICAgdGFyZ2V0OiBcImh0dHA6Ly9sb2NhbGhvc3Q6ODA4MFwiLCAvLyBTcHJpbmcgQm9vdCBcdUMxMUNcdUJDODQgVVJMXHJcbiAgICAgICAgY2hhbmdlT3JpZ2luOiB0cnVlLCAvLyBDT1JTIFx1QkIzOFx1QzgxQyBcdUQ1NzRcdUFDQjBcclxuICAgICAgICByZXdyaXRlOiAocGF0aCkgPT4gcGF0aC5yZXBsYWNlKC9eXFwvYXBpLywgXCJcIiksIC8vIC9hcGkgXHVDODFDXHVBQzcwXHJcbiAgICAgIH0sXHJcbiAgICB9LFxyXG4gIH0sXHJcbiAgZGVmaW5lOiB7XHJcbiAgICBnbG9iYWw6IHt9LFxyXG4gIH0sXHJcbn0pO1xyXG4iXSwKICAibWFwcGluZ3MiOiAiO0FBQXNRLFNBQVMsZUFBZSxXQUFXO0FBRXpTLFNBQVMsb0JBQW9CO0FBQzdCLE9BQU8sU0FBUztBQUNoQixPQUFPLGlCQUFpQjtBQUp5SSxJQUFNLDJDQUEyQztBQU9sTixJQUFPLHNCQUFRLGFBQWE7QUFBQSxFQUMxQixTQUFTLENBQUMsSUFBSSxHQUFHLFlBQVksQ0FBQztBQUFBLEVBRTlCLFNBQVM7QUFBQSxJQUNQLE9BQU87QUFBQSxNQUNMLEtBQUssY0FBYyxJQUFJLElBQUksU0FBUyx3Q0FBZSxDQUFDO0FBQUEsSUFDdEQ7QUFBQSxFQUNGO0FBQUEsRUFDQSxRQUFRO0FBQUEsSUFDTixNQUFNO0FBQUE7QUFBQSxJQUNOLE9BQU87QUFBQSxNQUNMLFFBQVE7QUFBQSxRQUNOLFFBQVE7QUFBQTtBQUFBLFFBQ1IsY0FBYztBQUFBO0FBQUEsUUFDZCxTQUFTLENBQUMsU0FBUyxLQUFLLFFBQVEsVUFBVSxFQUFFO0FBQUE7QUFBQSxNQUM5QztBQUFBLElBQ0Y7QUFBQSxFQUNGO0FBQUEsRUFDQSxRQUFRO0FBQUEsSUFDTixRQUFRLENBQUM7QUFBQSxFQUNYO0FBQ0YsQ0FBQzsiLAogICJuYW1lcyI6IFtdCn0K
