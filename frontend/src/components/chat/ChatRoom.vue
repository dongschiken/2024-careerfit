<template>
  <div class="chat-container">
    <!-- 채팅 메시지 목록 -->
    <div class="chat-messages">
      <ul>
        <li
          v-for="(message, index) in messages"
          :key="index"
          class="message-item"
        >
          <div>{{ message.senderNickname }}: {{ message.message }}</div>
          <span class="timestamp">{{ formatTime(message.sendDate) }}</span>
        </li>
      </ul>
    </div>

    <!-- 임시 버튼을 클릭하면 모달 창을 확인할 수 있도록 설정 -->
    <button @click="toggleModal">채팅창 열기</button>

    <!-- 채팅 입력 -->
    <div class="chat-input">
      <input
        v-model="messageContent"
        placeholder="메시지를 입력해주세요"
        maxlength="255"
      />
      <button @click="sendMessage">전송</button>
      <span>{{ messageContent.length }}/255</span>
    </div>

    <!-- 모달 창 -->
    <div v-if="isModalVisible" class="chat-modal">
      <div class="modal-content">
        <h3>채팅방</h3>
        <div class="chat-messages">
          <ul>
            <li
              v-for="(message, index) in messages"
              :key="index"
              class="message-item"
            >
              <div>{{ message.senderNickname }}: {{ message.message }}</div>
              <span class="timestamp">{{ formatTime(message.sendDate) }}</span>
            </li>
          </ul>
        </div>
        <button @click="closeModal">닫기</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      stompClient: null,
      messageContent: "",
      messages: [],
      userName: "스노우볼",
      userId: 123,
      isModalVisible: false, // 모달 표시 여부
    };
  },
  methods: {
    formatTime(time) {
      const date = new Date(time);
      return date.toLocaleTimeString("ko-KR", {
        hour: "2-digit",
        minute: "2-digit",
      });
    },
    sendMessage() {
      if (this.messageContent.trim()) {
        const message = {
          senderId: this.userId,
          senderNickname: this.userName,
          message: this.messageContent,
          sendDate: new Date().toISOString(),
        };
        this.messages.push(message); // 메시지를 보내고 리스트에 추가
        this.messageContent = ""; // 입력창 초기화
      }
    },
    toggleModal() {
      this.isModalVisible = !this.isModalVisible; // 모달 열기/닫기
    },
    closeModal() {
      this.isModalVisible = false; // 모달 닫기
    },
  },
};
</script>

<style scoped>
.chat-container {
  width: 100%;
}

.chat-messages {
  height: 300px;
  overflow-y: scroll;
  margin-bottom: 10px;
}

.message-item {
  display: flex;
  flex-direction: column;
  margin-bottom: 10px;
}

.chat-input {
  display: flex;
  align-items: center;
}

input {
  flex-grow: 1;
  padding: 10px;
  border-radius: 5px;
  border: 1px solid #ddd;
}

button {
  background-color: #ff7f50;
  color: white;
  padding: 10px;
  border-radius: 5px;
  cursor: pointer;
}

.chat-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-content {
  background-color: white;
  padding: 20px;
  border-radius: 10px;
  width: 80%;
  max-width: 600px;
}

.timestamp {
  font-size: 0.8em;
  color: gray;
}
</style>
