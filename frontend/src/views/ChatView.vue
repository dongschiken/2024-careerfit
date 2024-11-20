<template>
  <div class="chat-view">
    <h1>채팅방</h1>
    <!-- ChatRoom 컴포넌트에 chatRoomId를 동적으로 전달 -->
    <ChatRoom :chatRoomId="chatRoomId" />
  </div>
</template>

<script>
// ChatRoom 컴포넌트를 임포트합니다.
import ChatRoom from "@/components/chat/ChatRoom.vue";

export default {
  methods: {
    getCookie(name) {
      const value = `; ${document.cookie}`;
      const parts = value.split(`; ${name}=`);
      if (parts.length === 2) return parts.pop().split(";").shift();
      return null;
    },
    checkRefreshToken() {
      const refreshToken = this.getCookie("refreshToken");
      if (refreshToken) {
        console.log("Refresh Token:", refreshToken);
      } else {
        console.error("Refresh Token이 존재하지 않습니다.");
      }
    },
  },
  mounted() {
    this.checkRefreshToken();
  },
  components: {
    ChatRoom,
  },
  data() {
    return {
      // 예시로 chatRoomId를 1로 설정합니다. 이 값은 동적으로 변경될 수 있습니다.
      chatRoomId: 1,
    };
  },
};
</script>

<style scoped>
.chat-view {
  padding: 20px;
}

h1 {
  font-size: 24px;
  margin-bottom: 20px;
}
</style>
