import { createApp } from "vue";
import { createPinia } from "pinia";
import App from "./App.vue";
import router from "./router";
import piniaPersistedstate from "pinia-plugin-persistedstate";
const app = createApp(App);
const pinia = createPinia();

app.use(pinia);
app.use(router);
pinia.use(piniaPersistedstate);

app.mount("#app");
