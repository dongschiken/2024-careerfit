<template>
  <div>
    <div>
      <h2>{{ chatRoom.title }}</h2>
      <p>{{ chatRoom.description }}</p>
    </div>
    <div class="chat-history">
      <ul>
        <li v-for="(message, index) in messages" :key="index">
          <strong>{{ message.senderNickname }}</strong
          >: {{ message.message }}
        </li>
      </ul>
    </div>
    <div class="chat-input">
      <input
        v-model="newMessage"
        placeholder="메시지를 입력하세요..."
        @keyup.enter="sendMessage"
      />
      <button @click="sendMessage">전송</button>
    </div>
  </div>
</template>

<script>
import SockJS from "sockjs-client";
import Stomp from "stompjs";

export default {
  props: ["chatRoomId"],
  data() {
    return {
      messages: [], // 채팅 메시지 목록
      newMessage: "", // 입력한 메시지
      stompClient: null, // STOMP 클라이언트
      chatRoom: {}, // 채팅방 정보
    };
  },
  mounted() {
    this.connectToChat(); // WebSocket 연결
    this.loadChatRoomInfo(); // 채팅방 정보 로드
    this.loadChatHistory(); // 채팅 기록 로드
  },
  methods: {
    connectToChat() {
      const socket = new SockJS("http://localhost:8080/chat");
      this.stompClient = Stomp.over(socket);
      const self = this;

      this.stompClient.connect({}, function () {
        self.stompClient.subscribe(
          `/topic/chatRoom/${self.chatRoomId}`,
          function (message) {
            const parsedMessage = JSON.parse(message.body);
            self.messages.push(parsedMessage); // 수신 메시지 추가
          }
        );
      });
    },
    sendMessage() {
      if (!this.newMessage.trim()) return;

      // 메시지 객체 생성
      const message = {
        message: this.newMessage,
        senderNickname:
          sessionStorage.getItem("nickname") || "알 수 없는 사용자", // 닉네임 가져오기
      };

      // 메시지 전송
      this.stompClient.send(
        `/app/sendMessage/${this.chatRoomId}`,
        {},
        JSON.stringify(message)
      );
      this.newMessage = ""; // 입력창 초기화
    },
    loadChatRoomInfo() {
      fetch(`/api/chat-room/${this.chatRoomId}`)
        .then((response) => response.json())
        .then((data) => {
          this.chatRoom = data;
        });
    },
    loadChatHistory() {
      fetch(`/api/chat-room/${this.chatRoomId}/history`)
        .then((response) => response.json())
        .then((data) => {
          this.messages = data;
        });
    },
  },
};
</script>
