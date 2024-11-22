<template>
  <div>
    <ul>
      <li
        v-for="(message, index) in messages"
        :key="index"
        :class="['message', message.userId === currentUserId ? 'self' : '']"
      >
        <img
          v-if="message.userProfile"
          :src="message.userProfile"
          alt="프로필"
          class="message-profile"
        />
        <span class="sender">{{ message.userNickname }}:</span>
        <span class="text">{{ message.message }}</span>
      </li>
    </ul>
    <div class="chat-input">
      <input
        v-model="newMessage"
        placeholder="메시지를 입력하세요"
        @keydown.enter="sendMessage"
        :disabled="!isConnected"
      />
      <button @click="sendMessage" :disabled="!isConnected">전송</button>
    </div>
    <p v-if="!isConnected" class="connection-warning">WebSocket 연결 중...</p>
  </div>
</template>

<script>
import WebSocketService from "@/services/WebSocketService";
export default {
  props: {
    currentUser: {
      // 부모 컴포넌트로부터 currentUser 전달
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      messages: [], // 수신된 메시지
      newMessage: "", // 입력 중인 메시지
      currentChatRoomId: null, // 현재 채팅방 ID
      isConnected: false, // WebSocket 연결 상태
    };
  },
  mounted() {
    WebSocketService.connect(this.onConnected, this.onError);
  },
  methods: {
    onConnected() {
      console.log("WebSocket 연결 성공");

      this.isConnected = true; // 연결 상태 업데이트
      WebSocketService.subscribe(
        `/topic/chatRoom/${this.currentChatRoomId}`,
        this.onMessageReceived
      );
    },
    onError(error) {
      console.error("WebSocket 연결 오류:", error);
      this.isConnected = false;
    },
    sendMessage() {
      if (!this.newMessage.trim()) {
        alert("메시지를 입력하세요.");
        return;
      }

      if (!this.currentUser || !this.currentUser.userId) {
        alert("사용자 정보가 없습니다. 다시 로그인 해주세요.");
        return;
      }

      const chatMessage = {
        chatRoomId: this.currentChatRoomId,
        userId: this.currentUser.userId, // currentUser 사용
        message: this.newMessage,
        userNickname: this.currentUser.userNickname || "익명", // currentUser 사용
        userProfile: this.currentUser.userProfile || "/img/default-profile.png", // currentUser 사용
      };

      WebSocketService.send(
        `/app/sendMessage/${this.currentChatRoomId}`,
        chatMessage
      );
      this.messages.unshift(chatMessage); // 전송한 메시지 바로 추가
      this.newMessage = ""; // 입력 필드 초기화
    },
    onMessageReceived(payload) {
      const message = JSON.parse(payload.body);
      console.log("수신한 메시지:", message);
      this.messages.unshift(message); // 수신된 메시지 추가
    },
  },
};
</script>

<style>
.connection-warning {
  color: red;
  font-weight: bold;
  margin-top: 10px;
}
</style>
