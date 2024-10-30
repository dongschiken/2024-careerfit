import { createRouter, createWebHashHistory } from "vue-router";
import MainView from "@/views/MainView.vue";
import BoardListView from "@/components/board/BoardContent.vue";
import LoginView from "@/components/user/LoginView.vue";
import JoinView from "@/components/user/JoinView.vue";

const routes = [
  {
    path: "/",
    name: "main",
    component: MainView,
  },
  {
    path: "/board",
    name: "board",
    component: BoardListView,
  },
  {
    path: "/user/login",
    name: "login",
    component: LoginView,
  },
  {
    path: "/user/join",
    name: "join",
    component: JoinView,
  },
];

const router = createRouter({
  history: createWebHashHistory(),
  routes,
});

export default router;
