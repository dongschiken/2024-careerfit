<template>
  <div class="chat-container">
    <!-- 유저 정보 -->
    <div class="chat-header">
      <img :src="userProfileUrl" alt="프로필 이미지" />
      <div>
        <h3>{{ userName }}</h3>
        <p>{{ userStatus }}</p> <!-- 유저 상태 표시 (예: "온라인", "오프라인") -->
      </div>
    </div>

    <!-- 채팅 메시지 목록 -->
    <div class="chat-messages">
      <ul>
        <li v-for="(message, index) in messages" :key="index" class="message-item">
          {{ message.senderNickname }}: {{ message.message }}
          <span class="timestamp">{{ message.sendDate }}</span>
        </li>
      </ul>
    </div>

    <!-- 메시지 입력 폼 -->
    <div class="chat-input">
      <input v-model="messageContent" placeholder="메시지를 입력해주세요" />
      <button @click="sendMessage">전송</button>
    </div>
  </div>
</template>

<script>
import SockJS from "sockjs-client";
import Stomp from "stompjs";
import axios from "axios"; // 서버에서 데이터 가져오기 위해 axios 사용

export default {
  data() {
    return {
      stompClient: null,
      messageContent: "",
      messages: [],
      userName: "",          // 서버에서 받아올 유저 이름
      userProfileUrl: "",    // 서버에서 받아올 유저 프로필 이미지 URL
      userStatus: "",        // 서버에서 받아올 유저 상태 (예: 온라인, 오프라인)
      chatRoomId: 1,         // 채팅방 ID (기본값을 설정하거나 동적으로 설정 가능)
      userId: 123,           // 유저 ID (Vuex나 로그인 정보로 설정 가능)
    };
  },
  methods: {
    connect() {
      const socket = new SockJS("http://localhost:8080/chat"); // 서버와 WebSocket 연결
      this.stompClient = Stomp.over(socket);
      this.stompClient.connect({}, (frame) => {
        console.log("Connected: " + frame);
        this.stompClient.subscribe("/topic/chatRoom/" + this.chatRoomId, (message) => {
          this.showMessage(JSON.parse(message.body)); // 서버로부터 받은 메시지만 화면에 표시
        });
      });
    },
    sendMessage() {
      if (this.messageContent.trim() !== "") {
        const message = {
          chatRoomId: this.chatRoomId,
          senderId: this.userId,
          message: this.messageContent,
          sendDate: new Date().toISOString(),
          senderNickname: this.userName,
        };
        
        // 서버로 메시지 전송
        this.stompClient.send(`/app/sendMessage/${this.chatRoomId}`, {}, JSON.stringify(message));
        
        // 메시지 전송 후 입력 필드 초기화
        this.messageContent = ""; 
      }
    },
    showMessage(message) {
      this.messages.push(message); // 서버로부터 받은 메시지를 화면에 추가
    },

    fetchUserInfo() {
      // 예시 API 호출 (실제 API 경로는 변경 필요)
      axios.get("/api/user-info")
        .then((response) => {
          const userData = response.data;
          this.userName = userData.userName;
          this.userProfileUrl = userData.userProfileUrl;
          this.userStatus = userData.userStatus; // 예: 온라인, 오프라인 상태
        })
        .catch((error) => {
          console.error("유저 정보를 불러오는 중 오류 발생:", error);
        });
    }
  },
  mounted() {
    this.connect();  // WebSocket 연결
    this.fetchUserInfo();  // 컴포넌트가 마운트될 때 유저 정보 로드
  },
};
</script>

<style scoped>
.chat-container {
  width: 100%;
}
.chat-header {
  display: flex;
  align-items: center;
}
.chat-messages {
  height: 300px;
  overflow-y: auto;
}
.chat-input {
  display: flex;
}
input {
  flex-grow: 1;
}
button {
  width: 50px;
}
.timestamp {
  font-size: 0.8em;
  color: gray;
}
.message-item {
  background-color: #ffa500; /* 메시지 배경색 */
  margin-bottom: 10px; /* 메시지 간격 */
  padding: 10px; /* 메시지 내부 여백 */
  border-radius: 20px; /* 메시지 모서리 둥글게 */
  color: white; /* 글자 색상 */
}
</style>
