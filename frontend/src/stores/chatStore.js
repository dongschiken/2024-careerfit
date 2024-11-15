import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useChatStore = defineStore('chat', {
  state: () => ({
    messages: ref([]),  // 메시지 배열을 ref로 래핑
  }),

  actions: {
    addMessage(message) {
      this.messages.push(message);
    },

    clearMessages() {
      this.messages = [];
    },
  },

  persist: true,  // 상태를 로컬스토리지에 저장하도록 설정
});
