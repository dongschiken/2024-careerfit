<template>
  <div>
    <router-view />
    <!-- <button @click="increment()">증가</button>
    <div>{{ store.count }}</div> -->
    <button @click="refresh()">리프레시 토큰</button>
  </div>
</template>

<script setup>
import { useCounterStore } from "@/stores/counter";
import { ref } from "vue";
import api2 from "./api/axiosInstance";
const store = useCounterStore();
const increment = () => {
  store.increment();
};

const refresh = async () => {
  try {
    alert("버튼");
    const response = await api2.post("/api/refresh-token");
    console.log("리프레시 토큰 성공:", response.data);
    // 응답 데이터를 활용한 추가 작업
  } catch (error) {
    console.error("리프레시 토큰 요청 실패:", error);
  }
};
console.log(store.count);
</script>

<style scoped>
nav a {
  font-weight: bold;
  text-decoration: none;
  color: black;
}
nav a.router-link-exact-active {
  color: #43b983;
}
</style>
