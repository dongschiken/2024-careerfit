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
import axios from "axios";
import api from "./api/axiosInstance";
const store = useCounterStore();
const increment = () => {
  store.increment();
};

const refresh = async () => {
  try {
    const refreshToken = sessionStorage.getItem("refreshToken");
    if (!refreshToken) {
      throw new Error("리프레시 토큰이 없습니다. 로그인이 필요합니다.");
    }

    // Axios 요청 보내기
    const response = await axios.post(
      "http://localhost:8080/api/refresh-token",
      { refreshToken } // 요청 본문에 토큰 포함
    );
    // 응답 데이터를 활용한 추가 작업
    console.log(response.data);
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
