<template>
  <div v-if="showModal" class="modal">
    <div class="modal-content">
      <h3>AI 식단 관리</h3>
      <div class="chat-container">
        <div v-for="(message, index) in messages" :key="index" class="message">
          <p :class="message.role">{{ message.content }}</p>
        </div>
      </div>
      <input
        type="text"
        v-model="userInput"
        placeholder="메시지를 입력하세요"
        @keyup.enter="sendMessage"
      />
      <button @click="sendMessage">전송</button>
      <button @click="closeModal">닫기</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import api from "@/api/axiosInstance";
</script>

<style lang="css" scoped>
.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}
.modal-content {
  background: white;
  padding: 20px;
  border-radius: 8px;
  width: 400px;
}
.chat-container {
  max-height: 300px;
  overflow-y: auto;
  margin-bottom: 10px;
}
.message {
  margin: 5px 0;
}
.user {
  text-align: right;
}
.bot {
  text-align: left;
}
</style>
