<template>
  <div class="chat-container">
    <h1>GPT Chatbot</h1>
    <div class="chat-window">
      <div v-for="(chat, index) in chats" :key="index" class="chat">
        <div :class="chat.role">{{ chat.content }}</div>
      </div>
    </div>
    <div class="input-area">
      <input
        v-model="message"
        @keyup.enter="sendMessage"
        placeholder="Type your message..."
      />
      <button @click="sendMessage">Send</button>
    </div>
  </div>
</template>

<script>
import api from "@/api/axiosInstance";
export default {
  data() {
    return {
      message: "",
      chats: [],
    };
  },
  methods: {
    async sendMessage() {
      if (this.message.trim() === "") return;
      const userMessage = { role: "user", content: this.message };
      this.chats.push(userMessage);
      try {
        const response = await api.post("http://localhost:8080/api/gpt", {
          message: this.message,
        });
        this.chats.push({ role: "assistant", content: response.data });
      } catch (error) {
        console.error(error);
        this.chats.push({
          role: "assistant",
          content: "Error fetching response",
        });
      }
      this.message = "";
    },
  },
};
</script>

<style>
.chat-container {
  max-width: 600px;
  margin: auto;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 10px;
}
.chat-window {
  height: 400px;
  overflow-y: auto;
  margin-bottom: 20px;
  border: 1px solid #ddd;
  padding: 10px;
  border-radius: 10px;
}
.chat {
  margin: 5px 0;
}
.user {
  text-align: right;
}
.assistant {
  text-align: left;
  background-color: #f1f1f1;
  padding: 10px;
  border-radius: 10px;
}
.input-area {
  display: flex;
  gap: 10px;
}
</style>
