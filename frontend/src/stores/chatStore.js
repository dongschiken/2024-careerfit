import { defineStore } from "pinia";

export const useChatStore = defineStore("chat", {
  state: () => ({
    selectedChatRoom: null, // 선택된 채팅방
    messages: {}, // 채팅방별 메시지 (key: chatRoomId, value: message array)
  }),

  actions: {
    // 선택된 채팅방 설정
    setSelectedChatRoom(chatRoom) {
      this.selectedChatRoom = chatRoom;
    },

    // 채팅방 메시지 추가
    addMessageToChatRoom(chatRoomId, message) {
      if (!this.messages[chatRoomId]) {
        this.messages[chatRoomId] = [];
      }
      this.messages[chatRoomId].push(message);
    },

    // 채팅방 메시지 초기화
    clearMessagesForChatRoom(chatRoomId) {
      if (this.messages[chatRoomId]) {
        this.messages[chatRoomId] = [];
      }
    },

    // 모든 메시지 초기화
    clearAllMessages() {
      this.messages = {};
    },
  },

  persist: true, // 상태를 로컬스토리지에 저장
});
