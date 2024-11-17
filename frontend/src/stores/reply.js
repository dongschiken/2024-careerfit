import { defineStore } from "pinia";

export const useReplyStore = defineStore(
  "reply",
  () => {
    const REST_API_URL = `http://localhost:8080/api/reply`;
    return {};
  },
  {
    persist: true,
  }
);
