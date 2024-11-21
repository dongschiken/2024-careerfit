<template>
  <div class="chat-room-list">
    <h2>채팅 목록</h2>

    <!-- 채팅방 생성 -->
    <div class="create-room">
      <input
        v-model="newRoomTitle"
        type="text"
        placeholder="채팅방 제목 입력"
        class="room-input"
      />
      <button @click="createChatRoom" class="create-button">생성</button>
    </div>

    <!-- 채팅방 목록 -->
    <ul class="rooms">
      <li v-for="room in chatRooms" :key="room.chatRoomId" class="room-item">
        <div class="room-info">
          <h3>{{ room.title }}</h3>
          <p>마지막 메시지: {{ formatDate(room.lastAt) }}</p>
        </div>
      </li>
    </ul>
  </div>
</template>

<script>
import api from "@/api/axiosInstance"; // axiosInstance 파일 경로 맞게 수정

export default {
  data() {
    return {
      chatRooms: [], // 채팅방 목록
      newRoomTitle: "", // 새로운 채팅방 제목
    };
  },
  methods: {
    // 채팅방 목록 가져오기
    async fetchChatRooms() {
      try {
        const response = await api.get("/api/chat-room");
        this.chatRooms = response.data;
      } catch (error) {
        console.error("채팅방 목록 불러오기 실패:", error);
        alert("채팅방 목록을 불러오는데 실패했습니다.");
      }
    },
    // 채팅방 생성
    async createChatRoom() {
      if (!this.newRoomTitle.trim()) {
        alert("채팅방 제목을 입력하세요.");
        return;
      }
      try {
        const response = await api.post("/api/chat-room", {
          title: this.newRoomTitle,
        });
        this.chatRooms.unshift(response.data); // 생성된 채팅방을 목록에 추가
        this.newRoomTitle = ""; // 입력 필드 초기화
        alert("채팅방이 성공적으로 생성되었습니다!");
      } catch (error) {
        console.error("채팅방 생성 실패:", error);
        alert("채팅방을 생성하는데 실패했습니다.");
      }
    },
    // 날짜 포맷 변환
    formatDate(date) {
      return new Date(date).toLocaleString();
    },
  },
  mounted() {
    this.fetchChatRooms(); // 컴포넌트가 마운트되었을 때 채팅방 목록을 가져옴
  },
};
</script>

<style>
.chat-room-list {
  padding: 20px;
  max-width: 600px;
  margin: 0 auto;
  font-family: Arial, sans-serif;
}

h2 {
  text-align: center;
  margin-bottom: 20px;
  font-size: 24px;
}

.create-room {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 20px;
}

.room-input {
  padding: 10px;
  margin-right: 10px;
  flex: 1;
  max-width: 70%;
  border: 1px solid #ccc;
  border-radius: 5px;
  font-size: 14px;
}

.create-button {
  padding: 10px 20px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  font-size: 14px;
  cursor: pointer;
}

.create-button:hover {
  background-color: #0056b3;
}

.rooms {
  list-style: none;
  padding: 0;
}

.room-item {
  padding: 15px;
  margin-bottom: 10px;
  background-color: #f9f9f9;
  border: 1px solid #ddd;
  border-radius: 5px;
}

.room-info h3 {
  margin: 0;
  font-size: 18px;
  font-weight: bold;
}

.room-info p {
  margin: 5px 0 0;
  font-size: 14px;
  color: #666;
}
</style>
