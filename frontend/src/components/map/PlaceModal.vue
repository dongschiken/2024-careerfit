<template>
  <div v-if="isVisible" class="modal">
    <h3>{{ place.name }}</h3>
    <p>{{ place.address }}</p>

    <!-- 채팅방 리스트 -->
    <div>
      <h4>채팅방 리스트</h4>
      <ul>
        <li v-for="(room, index) in chatRooms" :key="index">
          <button @click="joinChatRoom(room.id)">
            {{ room.name }}
          </button>
        </li>
      </ul>
    </div>

    <button @click="closeModal" class="btn btn-secondary">닫기</button>
  </div>
</template>

<script>
export default {
  props: ["place", "isVisible"],
  data() {
    return {
      chatRooms: [
        // 더미 데이터. 백엔드 연동 시 대체
        { id: 1, name: "헬스장 친구 구해요" },
        { id: 2, name: "같이 운동하실 분" },
        { id: 3, name: "공원 산책 모임" },
      ],
    };
  },
  methods: {
    closeModal() {
      this.$emit("close");
    },
    joinChatRoom(roomId) {
      alert(`채팅방 ${roomId}에 입장합니다.`);
      // 실제로 채팅방으로 이동하는 로직 추가 (Vue Router 연동)
    },
  },
};
</script>

<style scoped>
.modal {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: white;
  padding: 20px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
  z-index: 1000;
}

.modal h3 {
  margin: 0;
  font-size: 1.5rem;
}

.modal p {
  margin: 1rem 0;
}

.modal button {
  margin-top: 10px;
}
</style>
