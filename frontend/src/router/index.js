import { createRouter, createWebHistory } from "vue-router";
import MainView from "@/views/MainView.vue";
import LoginView from "@/components/user/LoginView.vue";
import JoinView from "@/components/user/JoinView.vue";
import BoardWrite from "@/components/board/BoardWrite.vue";
import MealPlan from "@/components/meal/MealPlan.vue";
import BoardContentView from "@/views/BoardContentView.vue";

const routes = [
  {
    path: "/",
    name: "main",
    component: MainView,
  },
  {
    path: "/board",
    name: "board",
    component: BoardContentView,
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
  {
    path: "/write",
    name: "write",
    component: BoardWrite,
  },
  {
    path: "/meal",
    name: "meal",
    component: MealPlan,
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
