import { createApp } from "vue";
import { createPinia } from "pinia";
import piniaPluginPersistedstate from "pinia-plugin-persistedstate";
import App from "./App.vue";
import router from "./router";
import piniaPersistedstate from "pinia-plugin-persistedstate";
import api from "./api/axiosInstance";
import Toast from "vue-toastification";
import "vue-toastification/dist/index.css";

const pinia = createPinia();
pinia.use(piniaPluginPersistedstate);
pinia.use(piniaPersistedstate);
const app = createApp(App);
app.use(pinia);
app.use(router);
app.use(Toast);
app.config.globalProperties.$api = api;
app.mount("#app");

// 로그인 후 토큰 저장 함수
function storeTokens(accessToken, refreshToken) {
  sessionStorage.setItem("accessToken", accessToken); // 세션 스토리지에 Access Token 저장
  document.cookie = `refreshToken=${refreshToken}; path=/; HttpOnly`; // 쿠키에 Refresh Token 저장
}
