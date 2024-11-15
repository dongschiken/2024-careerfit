import { createApp } from "vue";
import { createPinia } from "pinia";
import piniaPluginPersistedstate from "pinia-plugin-persistedstate";
import App from "./App.vue";
import router from "./router";
import piniaPersistedstate from "pinia-plugin-persistedstate";
import api from "./api/axiosInstance";
const pinia = createPinia();
pinia.use(piniaPluginPersistedstate);

const app = createApp(App);
app.use(createPinia());
app.use(router);
app.config.globalProperties.$api = api;
pinia.use(piniaPersistedstate);

app.mount("#app");
