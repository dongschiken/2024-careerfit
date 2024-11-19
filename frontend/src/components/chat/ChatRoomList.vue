<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-content">
      <h3>{{ location.name }} 정보</h3>
      <p>유형: {{ location.type }}</p>
      <h4>채팅방 리스트</h4>
      <ul>
        <li v-for="(room, index) in chatRooms" :key="index" @click="joinChatRoom(room)">
          {{ room.name }}
        </li>
      </ul>
      <button @click="$emit('close')">닫기</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';

const props = defineProps({
  location: {
    type: Object,
    required: true,
  },
});

const chatRooms = ref([]);
const router = useRouter();

onMounted(async () => {
  try {
    const response = await fetch(`/api/chatrooms?locationId=${props.location.locationId}`);
    chatRooms.value = await response.json();
  } catch (error) {
    console.error('채팅방 데이터를 가져오는 중 오류 발생:', error);
  }
});

function joinChatRoom(room) {
  router.push(`/chat/${room.id}`);
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
}
.modal-content {
  width: 400px;
  max-height: 500px;
  overflow-y: auto;
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.15);
}
</style>