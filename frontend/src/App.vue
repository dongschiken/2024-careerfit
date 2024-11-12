<template>
  <div>
    <router-view />
<<<<<<< HEAD
    <!-- <button @click="increment()">증가</button>
    <div>{{ store.count }}</div> -->
=======
    <nav>
      <RouterLink to="/">Home</RouterLink>
      <RouterLink :to="{ name: 'board' }">Board</RouterLink>
      <RouterLink :to-="{ name : 'join' }">Join</RouterLink>
    </nav>
>>>>>>> a8efb3e35bd89558cf6856d730a3cf7cbaa7970c
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
