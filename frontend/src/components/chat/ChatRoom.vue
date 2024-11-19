<template>
  <div>
    <div>
      <h2>{{ chatRoom.title }}</h2>
      <p>{{ chatRoom.description }}</p>
    </div>
    <div class="chat-history">
      <ul>
        <li v-for="message in messages" :key="message.id">
          <strong>{{ message.senderNickname }}</strong>: {{ message.message }}
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
      messages: [],
      newMessage: "",
      stompClient: null,
      chatRoom: {}, // 채팅방 정보
    };
  },
  mounted() {
    this.connectToChat();
    this.loadChatRoomInfo();
    this.loadChatHistory();
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
            self.messages.push(JSON.parse(message.body));
          }
        );
      });
    },
    sendMessage() {
      if (!this.newMessage.trim()) return;
      const message = {
        message: this.newMessage,
        senderNickname: "사용자 닉네임", // 실제 닉네임으로 변경 필요
      };
      this.stompClient.send(
        `/app/sendMessage/${this.chatRoomId}`,
        {},
        JSON.stringify(message)
      );
      this.newMessage = "";
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
