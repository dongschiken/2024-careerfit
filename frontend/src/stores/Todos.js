import { ref, computed } from "vue";
import { defineStore } from "pinia";

export const useTodosStore = defineStore("todos", () => {
  let id = 0;
  const todos = ref([
    {
      id: id++,
      text: "잠자기",
      isDone: false,
    },
    {
      id: id++,
      text: "밥먹기",
      isDone: false,
    },
    {
      id: id++,
      text: "뷰복습",
      isDone: false,
    },
  ]);
  return { todos };
});
