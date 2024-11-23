<template>
  <div class="chat-container">
    <div class="chat-messages" ref="messageContainer">
      <div
        v-for="(message, index) in messages"
        :key="index"
        class="message-wrapper"
      >
        <!-- 상대방 메시지 -->
        <div
          v-if="message.userId !== currentUser.userId"
          class="message-group other"
        >
          <div class="profile">
            <img
              :src="message.userProfile || '/img/default-profile.png'"
              alt="프로필 이미지"
              class="profile-img"
            />
          </div>
          <div class="message-content">
            <span class="nickname">{{ message.userNickname }}</span>
            <div class="message-bubble">{{ message.message }}</div>
          </div>
        </div>

        <!-- 내 메시지 -->
        <div v-else class="message-group self">
          <div class="message-bubble">{{ message.message }}</div>
        </div>
      </div>
    </div>

    <div class="chat-input">
      <input
        v-model="newMessage"
        placeholder="메시지를 입력하세요"
        @keydown.enter="sendMessage"
        :disabled="!isConnected"
      />
      <button @click="sendMessage" :disabled="!isConnected">전송</button>
    </div>
  </div>
</template>

<script>
import WebSocketService from "@/services/WebSocketService";

export default {
  props: {
    currentUser: {
      type: Object,
      required: true,
    },
    currentChatRoomId: {
      type: Number,
      required: true,
    },
  },
  data() {
    return {
      messages: [],
      newMessage: "",
      isConnected: false,
    };
  },
  mounted() {
    WebSocketService.connect(this.onConnected, this.onError);
  },
  methods: {
    onConnected() {
      console.log("WebSocket 연결 성공");
      this.isConnected = true;

      // 구독 경로 수정
      const subscriptionPath = `/topic/chat/${this.currentChatRoomId}`;
      console.log("구독 경로:", subscriptionPath);

      WebSocketService.subscribe(subscriptionPath, this.onMessageReceived);
    },
    onError(error) {
      console.error("WebSocket 연결 실패:", error);
      this.isConnected = false;
    },
    sendMessage() {
      if (!this.newMessage.trim()) return;

      const chatMessage = {
        chatRoomId: this.currentChatRoomId,
        userId: this.currentUser.userId,
        message: this.newMessage,
        userNickname: this.currentUser.userNickname,
        userProfile: this.currentUser.userProfile || "/img/default-profile.png",
      };

      console.log("메시지 전송:", chatMessage);

      WebSocketService.send(
        `/app/sendMessage/${this.currentChatRoomId}`,
        chatMessage
      );

      this.newMessage = "";
    },
    onMessageReceived(payload) {
      const message = JSON.parse(payload.body);
      console.log("수신된 메시지:", message);

      this.messages.unshift(message);

      this.$nextTick(() => {
        const container = this.$refs.messageContainer;
        if (container) {
          container.scrollTop = container.scrollHeight;
        }
      });
    },
  },
  beforeDestroy() {
    // 컴포넌트가 제거될 때 WebSocket 연결 해제
    WebSocketService.disconnect();
  },
};
</script>

<style>
/* 채팅 메시지 리스트 */
.chat-messages {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 10px;
  max-height: 500px;
  overflow-y: auto;
  background-color: #f9f9f9;
}

/* 메시지 공통 스타일 */
.message {
  display: flex;
  align-items: center;
  margin: 5px 0;
}

.message.self {
  justify-content: flex-end; /* 내 메시지 오른쪽 정렬 */
}

.message.other {
  justify-content: flex-start; /* 상대방 메시지 왼쪽 정렬 */
}

/* 상대방 메시지 스타일 */
.message-other {
  display: flex;
  align-items: center;
  gap: 10px;
}

.message-other .profile-img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.message-other .message-content {
  background-color: #f1f1f1;
  padding: 10px;
  border-radius: 15px;
  max-width: 60%;
  word-wrap: break-word;
}

.message-other .nickname {
  font-size: 12px;
  color: #555;
  margin-bottom: 5px;
}

/* 내 메시지 스타일 */
.message-self .message-content {
  background-color: #d1eaff; /* 내 메시지 색상 */
  padding: 10px;
  border-radius: 15px;
  max-width: 60%;
  word-wrap: break-word;
}

/* 채팅 입력창 */
.chat-input {
  display: flex;
  gap: 10px;
  padding: 10px;
  border-top: 1px solid #ddd;
}

.chat-input input {
  flex: 1;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
}

.chat-input button {
  padding: 10px 20px;
  background-color: tomato;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.chat-input button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}
</style>
