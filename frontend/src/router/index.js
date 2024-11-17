import { createRouter, createWebHistory } from "vue-router";
import MainView from "@/views/MainView.vue";
import LoginView from "@/components/user/LoginView.vue";
import JoinView from "@/components/user/JoinView.vue";
import BoardWrite from "@/components/board/BoardWrite.vue";
import MealPlan from "@/components/meal/MealPlan.vue";
import BoardContentView from "@/views/BoardContentView.vue";
import ChatView from "@/views/ChatView.vue";
import BoardDetailView from "@/views/BoardDetailView.vue";
import { useToast } from "vue-toastification";

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
    name: "writeRegist",
    component: BoardWrite,
  },
  {
    path: "/write/:boardId",
    name: "writeEdit",
    component: BoardWrite,
    meta: { requiresAuth: true },
  },
  {
    path: "/meal",
    name: "meal",
    component: MealPlan,
    meta: { requiresAuth: true },
  },
  {
    path: "/chat",
    component: ChatView,
    meta: { requiresAuth: true },
  },
  {
    path: "/board/detail/:boardId",
    name: "boardDetail",
    component: BoardDetailView,
    props: true,
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// 네비게이션 가드 설정
router.beforeEach((to, from, next) => {
  const toast = useToast(); // toast를 사용하기 위한 선언
  const isLoggedIn = !!sessionStorage.getItem("accessToken"); // 로그인 상태 확인

  if (to.matched.some((record) => record.meta.requiresAuth) && !isLoggedIn) {
    // 로그인하지 않은 상태에서 접근하려면
    toast.info("로그인해야 이용할 수 있습니다."); // 로그인 필요 알림
    next("/user/login"); // 로그인 페이지로 리다이렉트
  } else if (to.name === "login" && isLoggedIn) {
    next("/"); // 이미 로그인 상태라면 메인 페이지로 이동
  } else {
    next(); // 그 외에는 접근 허용
  }
});

export default router;
