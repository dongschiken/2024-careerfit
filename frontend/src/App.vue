<template>
  <div>
    <router-view />
    <!-- <button @click="increment()">증가</button>
    <div>{{ store.count }}</div> -->
  </div>
</template>

<script setup>
import { useCounterStore } from "@/stores/counter";
const store = useCounterStore();
const increment = () => {
  store.increment();
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
