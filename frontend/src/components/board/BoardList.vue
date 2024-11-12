<template>
  <div>
    <BoardCategory v-for="todo in store.todos" :key="todo.is" />
  </div>
</template>

<script setup>
import BoardCategory from "./BoardCategory.vue";
import { useTodosStore } from "@/stores/Todos";
const store = useTodosStore();
</script>

<style lang="scss" scoped></style>
