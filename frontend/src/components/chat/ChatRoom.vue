<template>
  <div>
    <ul>
      <li v-for="message in messages" :key="message.id">
        <span class="sender">{{ message.senderNickname }}:</span>
        <span class="text">{{ message.message }}</span>
      </li>
    </ul>
    <div>
      <input v-model="newMessage" placeholder="메시지를 입력하세요" />
      <button @click="sendMessage(currentChatRoomId, newMessage)">전송</button>
    </div>
  </div>
</template>

<script>
import SockJS from "sockjs-client";
import Stomp from "stompjs";

export default {
  data() {
    return {
      messages: [], // 수신된 메시지
      newMessage: "", // 새 메시지 입력
      stompClient: null, // STOMP 클라이언트
      currentChatRoomId: 1, // 현재 채팅방 ID (임시 값)
    };
  },
  mounted() {
    const socket = new SockJS("http://localhost:8080/chat");
    this.stompClient = Stomp.over(socket);
    this.stompClient.connect({}, this.onConnected, this.onError);
  },
  methods: {
    onConnected() {
  if (!this.currentChatRoomId) {
    console.error("currentChatRoomId가 설정되지 않았습니다.");
    return;
  }
  this.stompClient.subscribe(`/topic/chatRoom/${this.currentChatRoomId}`, this.onMessageReceived);
  console.log("WebSocket 연결 성공");
},

sendMessage(chatRoomId, message) {
    const chatMessage = {
      chatRoomId: chatRoomId,
      userId: sessionStorage.getItem("userId"), // 보낸 사용자 ID
      message: message,
      userNickname: sessionStorage.getItem("userNickname"), // 닉네임
      userProfile: sessionStorage.getItem("userProfile"), // 프로필 사진
    };

    // WebSocket 메시지 전송
    this.stompClient.send(`/app/sendMessage/${chatRoomId}`, {}, JSON.stringify(chatMessage));

    // 바로 클라이언트 상태에 추가
    this.messages.push(chatMessage); // 브로드캐스트 수신 전에 즉각 반영
    this.newMessage = ""; // 입력창 초기화
  },

  onMessageReceived(payload) {
    const message = JSON.parse(payload.body);
    console.log("WebSocket 메시지 수신:", message);

    // **로컬 상태에 수신된 메시지 추가**
    this.chatRoomMessages.unshift(message);
  },
    onError(error) {
      console.error("WebSocket 연결 오류:", error);
    },
  },
};
</script>
