<template>
  <div>
    <MainHeaderVue />
    <div class="page-wrapper">
      <div class="page-container">
        <div v-if="!loading">
          <div class="user-profile-container">
            <div class="profile-header">
              <p class="profile-title">{{ userStore.nickname }}'s CAREER</p>
            </div>

            <div class="profile-content">
              <div class="profile-image-section">
                <img :src="profileImage" class="profile-image" alt="Profile" />
                <button
                  @click="showProfileImageModal = true"
                  class="edit-info-button"
                >
                  <i class="fas fa-camera"></i>
                </button>
              </div>

              <p class="user-email">{{ userStore.email }}</p>

              <div class="button-group">
                <button
                  @click="showEditUserInfoModal = true"
                  class="edit-button"
                >
                  회원정보 변경
                </button>
                <button @click="showPasswordModal = true" class="edit-button">
                  비밀번호 변경
                </button>
              </div>

              <div class="menu-grid">
                <button class="menu-button">
                  <i class="fas fa-heart"></i>
                  좋아요한 게시물
                </button>
                <button class="menu-button">
                  <i class="fas fa-edit"></i>
                  내가 쓴 게시물
                </button>
                <button class="menu-button">
                  <i class="fas fa-utensils"></i>
                  나의 식단
                </button>
                <button class="menu-button" @click="handleMyChats">
                  <i class="fas fa-comment"></i>
                  내 채팅
                </button>
              </div>
            </div>
          </div>

          <!-- 모달 컴포넌트들 -->
          <div
            v-if="showEditUserInfoModal"
            class="modal-backdrop"
            @click="closeModals"
          >
            <div class="modal-content" @click.stop>
              <EditUserInfoModal
                @close="showEditUserInfoModal = false"
                @update="handleUserUpdate"
              />
            </div>
          </div>

          <div
            v-if="showPasswordModal"
            class="modal-backdrop"
            @click="closeModals"
          >
            <div class="modal-content" @click.stop>
              <PasswordModal @close="showPasswordModal = false" />
            </div>
          </div>

          <div
            v-if="showProfileImageModal"
            class="modal-backdrop"
            @click="closeModals"
          >
            <div class="modal-content" @click.stop>
              <ProfileImageModal
                @close="showProfileImageModal = false"
                @update="handleProfileUpdate"
              />
            </div>
          </div>

          <!-- 내 채팅방 모달 -->
          <div
            v-if="showMyChatsModal"
            class="modal-backdrop"
            @click="closeModals"
          >
            <div class="modal-content" @click.stop>
              <h2>내 채팅방 목록</h2>
              <ul v-if="myChatRooms.length">
                <li
                  v-for="room in myChatRooms"
                  :key="room.chatRoomId"
                  @click="enterChatRoom(room.chatRoomId)"
                >
                  <div>
                    <p>{{ room.title }}</p>
                    <p>{{ room.lastMessage || "최근 메시지가 없습니다." }}</p>
                    <p>
                      {{
                        room.lastAt
                          ? formatChatListDate(room.lastAt)
                          : "시간 정보 없음"
                      }}
                    </p>
                  </div>
                </li>
              </ul>
              <p v-else>참여 중인 채팅방이 없습니다.</p>
            </div>
          </div>

          <!-- 채팅 모달 -->
          <div
            v-if="showChatRoomModal"
            class="chat-room-modal-overlay"
            @click.self="closeChatRoomModal"
          >
            <div class="chat-room-modal">
              <div class="chat-room-header">
                <div class="chat-room-info">
                  <h2 class="chat-room-title">{{ selectedChatRoom?.title }}</h2>
                </div>
              </div>

              <div class="chat-messages" ref="messageContainer">
                <div
                  v-for="(message, index) in getMessagesForChatRoom(
                    selectedChatRoomId
                  )"
                  :key="index"
                  :class="[
                    'message-wrapper',
                    message.userId === currentUser.userId ? 'self' : 'other',
                  ]"
                >
                  <div class="message-group">
                    <template v-if="message.userId !== currentUser.userId">
                      <div class="profile">
                        <img
                          :src="message.profileUrl || defaultProfile"
                          alt="프로필 이미지"
                          class="profile-img"
                        />
                        <span class="nickname">{{
                          message.nickname || "익명"
                        }}</span>
                      </div>
                    </template>
                    <div class="message-content">
                      <div class="message-bubble">
                        {{ message.message }}
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <div class="chat-input">
                <input
                  v-model="newChatMessage"
                  placeholder="메시지를 입력하세요"
                  @keydown.enter="sendMessage"
                />
                <button @click="sendMessage">전송</button>
              </div>
            </div>
          </div>
        </div>

        <div v-else class="loading-container">
          <p>로딩 중...</p>
        </div>
      </div>
    </div>
    <MainFooterVue />
  </div>
  <MainFooter />
</template>

<script setup>
import { ref, onMounted, computed, nextTick, onUnmounted } from "vue";
import { useUserStore } from "@/stores/userStore";
import { useChatStore } from "@/stores/chatStore";
import EditUserInfoModal from "./EditUserInfoModal.vue";
import PasswordModal from "./PasswordModal.vue";
import ProfileImageModal from "./ProfileImageModal.vue";
import api from "@/api/axiosInstance";
import WebSocketService from "@/services/WebSocketService";
import MainHeaderVue from "../module/MainHeader.vue";
import MainFooterVue from "../module/MainFooter.vue";

const userStore = useUserStore();
const chatStore = useChatStore();
const showEditUserInfoModal = ref(false);
const showPasswordModal = ref(false);
const showProfileImageModal = ref(false);
const showMyChatsModal = ref(false);
const myChatRooms = ref([]);
const loading = ref(true);
const selectedChatRoomId = ref(null);
const selectedChatRoom = ref(null);
const showChatRoomModal = ref(false);
const newChatMessage = ref("");
const defaultProfile = "/img/default-profile.png";
const currentUser = ref(null);
const messageContainer = ref(null);
const isScrolledToBottom = ref(true);

const profileImage = computed(() => {
  return userStore.profileUrl || "/default-profile.png";
});

const initializeProfile = async () => {
  try {
    loading.value = true;
    const tokenResponse = await api.get("/api/token-user");

    if (tokenResponse.data) {
      userStore.setUser(tokenResponse.data);
      currentUser.value = tokenResponse.data;
    }

    await userStore.fetchCurrentUser();
  } catch (error) {
    console.error("사용자 정보 로드 실패:", error);
    if (error.response?.status === 401) {
      await userStore.clearUser();
      router.push("/login");
    }
  } finally {
    loading.value = false;
  }
};

const loadMyChatRooms = async () => {
  try {
    const response = await api.get("/api/chat-room/my");
    myChatRooms.value = response.data || [];
  } catch (error) {
    console.error("내 채팅방 목록 로드 실패:", error);
    alert("내 채팅방 목록을 불러오지 못했습니다.");
  }
};

const enterChatRoom = async (chatRoomId) => {
  try {
    // 이전 채팅방 구독 해제 및 메시지 초기화
    if (selectedChatRoomId.value) {
      WebSocketService.unsubscribe(
        `/topic/chatRoom/${selectedChatRoomId.value}`
      );
      chatStore.clearMessagesForChatRoom(selectedChatRoomId.value);
    }

    // 채팅방 정보 설정
    selectedChatRoomId.value = chatRoomId;
    const room = myChatRooms.value.find(
      (room) => room.chatRoomId === chatRoomId
    );
    if (!room) throw new Error("채팅방을 찾을 수 없습니다.");
    selectedChatRoom.value = room;

    // 모달 상태 변경
    showMyChatsModal.value = false;
    showChatRoomModal.value = true;

    // WebSocket 연결 및 구독
    if (!WebSocketService.isConnected()) {
      await WebSocketService.connect(() => {
        subscribeToMessages(chatRoomId);
      }, onError);
    } else {
      subscribeToMessages(chatRoomId);
    }

    // 채팅 메시지 로드
    await loadChatRoomMessages(chatRoomId);

    // UI 업데이트 후 스크롤 이벤트 리스너 추가 및 스크롤
    await nextTick();
    if (messageContainer.value) {
      messageContainer.value.addEventListener("scroll", handleScroll);
      messageContainer.value.scrollTop = messageContainer.value.scrollHeight;
      isScrolledToBottom.value = true;
    }
  } catch (error) {
    console.error("채팅방 입장 실패:", error);
    alert("채팅방 입장에 실패했습니다.");
  }
};

const onConnected = () => {
  console.log("WebSocket 연결 성공");
};

const onError = (error) => {
  console.error("WebSocket 연결 오류:", error);
};

const subscribeToMessages = (chatRoomId) => {
  if (WebSocketService.isConnected()) {
    WebSocketService.subscribe(`/topic/chatRoom/${chatRoomId}`, (message) => {
      const receivedMessage = JSON.parse(message.body);
      chatStore.addMessageToChatRoom(chatRoomId, receivedMessage);

      // 내 메시지거나 스크롤이 맨 아래에 있을 때만 스크롤
      if (
        receivedMessage.userId === currentUser.value.userId ||
        isScrolledToBottom.value
      ) {
        scrollToBottom();
      }
    });
  }
};

const handleScroll = () => {
  if (messageContainer.value) {
    const { scrollTop, scrollHeight, clientHeight } = messageContainer.value;
    isScrolledToBottom.value =
      Math.abs(scrollHeight - clientHeight - scrollTop) < 10;
  }
};

const scrollToBottom = () => {
  if (messageContainer.value && isScrolledToBottom.value) {
    setTimeout(() => {
      messageContainer.value.scrollTop = messageContainer.value.scrollHeight;
    }, 100);
  }
};

const loadChatRoomMessages = async (chatRoomId) => {
  try {
    const response = await api.get(`/api/chat-room/${chatRoomId}/history`);
    chatStore.clearMessagesForChatRoom(chatRoomId);
    if (response.data) {
      response.data.forEach((message) => {
        chatStore.addMessageToChatRoom(chatRoomId, message);
      });
      await nextTick();
      scrollToBottom();
    }
  } catch (error) {
    console.error("채팅 메시지 로드 실패:", error);
  }
};

const formatChatListDate = (date) => {
  if (!date) return "시간 정보 없음";

  // UTC → -9시간 변환
  const utcDate = new Date(date); // 입력값을 UTC 시간으로 생성
  const MINUS_NINE_HOURS = -9 * 60 * 60 * 1000; // -9시간 (밀리초)
  const adjustedDate = new Date(utcDate.getTime() + MINUS_NINE_HOURS); // UTC - 9시간

  // 시간 포맷팅
  const ampm = adjustedDate.getHours() >= 12 ? "오후" : "오전";
  let hours = adjustedDate.getHours() % 12;
  hours = hours || 12; // 0시는 12시로 표시
  const minutes = String(adjustedDate.getMinutes()).padStart(2, "0");

  return `${ampm} ${hours}:${minutes}`;
};


const sendMessage = async () => {
  if (!newChatMessage.value.trim() || !selectedChatRoomId.value) return;

  const messageData = {
    chatRoomId: selectedChatRoomId.value,
    userId: currentUser.value.userId,
    message: newChatMessage.value.trim(),
    userNickname: currentUser.value.nickname,
    userProfile: currentUser.value.profileUrl || defaultProfile,
  };

  try {
    WebSocketService.send(
      `/app/sendMessage/${selectedChatRoomId.value}`,
      messageData
    );
    newChatMessage.value = "";
    await nextTick();
    scrollToBottom(); // 메시지 전송 후 스크롤
  } catch (error) {
    console.error("메시지 전송 실패:", error);
    alert("메시지 전송에 실패했습니다.");
  }
};

const closeChatRoomModal = () => {
  showChatRoomModal.value = false;
  selectedChatRoom.value = null;
  WebSocketService.unsubscribe(`/topic/chatRoom/${selectedChatRoomId.value}`);
  chatStore.clearMessagesForChatRoom(selectedChatRoomId.value);
};

const getMessagesForChatRoom = (chatRoomId) => {
  return chatStore.messages[chatRoomId] || [];
};

const getMaxWidth = (message) => {
  const length = message.length;
  if (length <= 10) return "150px";
  if (length <= 30) return "300px";
  return "450px";
};

const handleMyChats = async () => {
  await loadMyChatRooms();
  showMyChatsModal.value = true;
};

const handleUserUpdate = async () => {
  await initializeProfile();
  showEditUserInfoModal.value = false;
};

const handleProfileUpdate = async () => {
  await initializeProfile();
  showProfileImageModal.value = false;
};

const closeModals = () => {
  showEditUserInfoModal.value = false;
  showPasswordModal.value = false;
  showProfileImageModal.value = false;
  showMyChatsModal.value = false;
};

onMounted(() => {
  initializeProfile();
  if (messageContainer.value) {
    messageContainer.value.addEventListener("scroll", handleScroll);
  }
});

onUnmounted(() => {
  if (messageContainer.value) {
    messageContainer.value.removeEventListener("scroll", handleScroll);
  }
});
</script>

<style scoped>
/* 기존 스타일 유지 */
.page-wrapper {
  min-height: 100vh;
  background-color: #f8f9fa;
  padding: 40px 20px;
}

.page-container {
  max-width: 800px;
  margin: 0 auto;
}

.user-profile-container {
  background: white;
  border-radius: 20px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.profile-header {
  background: #ff7d29;
  padding: 20px;
  text-align: center;
}

.profile-title {
  color: white;
  font-size: 1.4rem;
  font-weight: 600;
  margin: 0;
}

.profile-content {
  padding: 40px 30px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.profile-image-section {
  position: relative;
  margin-bottom: 30px;
}

.profile-image {
  width: 140px;
  height: 140px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #ff7d29;
  box-shadow: 0 4px 12px rgba(255, 125, 41, 0.15);
}

.edit-info-button {
  position: absolute;
  bottom: 5px;
  right: 5px;
  background: #ff7d29;
  color: white;
  border: none;
  border-radius: 50%;
  width: 35px;
  height: 35px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 2px 6px rgba(255, 125, 41, 0.2);
}

.edit-info-button:hover {
  transform: scale(1.1);
  background: #ff6b10;
}

.user-email {
  color: #666;
  font-size: 1.1rem;
  margin: 15px 0;
}

.button-group {
  margin: 25px 0;
  display: flex;
  gap: 12px;
}

.edit-button {
  padding: 10px 20px;
  background: #ff7d29;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  font-weight: 500;
  font-size: 0.95rem;
}

.edit-button:hover {
  background: #ff6b10;
  transform: translateY(-1px);
}

.menu-grid {
  width: 100%;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-top: 30px;
}

.menu-button {
  padding: 20px;
  background: white;
  border: 2px solid #ffbf78;
  border-radius: 12px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  transition: all 0.2s ease;
  color: #333;
  font-weight: 500;
}

.menu-button:hover {
  background: #feffd2;
  border-color: #ff7d29;
  transform: translateY(-2px);
}

.menu-button i {
  color: #ff7d29;
  font-size: 1.2rem;
}

/* 채팅 관련 스타일 업데이트 */
.chat-room-modal {
  width: 70%;
  height: 80%;
  background: white;
  border-radius: 10px;
  display: flex;
  flex-direction: column;
}

.chat-room-header {
  padding: 15px;
  background: #fff;
  border-bottom: 1px solid #eee;
  text-align: center;
}

.chat-room-title {
  margin: 0;
  font-size: 1.2rem;
  font-weight: bold;
  color: #333;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #f5f5f5;
  display: flex;
  flex-direction: column;
  scroll-behavior: smooth;
}

.chat-messages::-webkit-scrollbar {
  width: 8px;
}

.chat-messages::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: #bbb;
  border-radius: 4px;
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: #999;
}

.message-wrapper {
  margin: 8px 0;
  display: flex;
  flex-direction: column;
  animation: fadeIn 0.3s ease-in-out;
}

.message-wrapper.self {
  align-items: flex-end;
}

.message-wrapper.other {
  align-items: flex-start;
}

.message-group {
  display: flex;
  align-items: flex-end;
  max-width: 70%;
}

.message-wrapper.self .message-group {
  flex-direction: row-reverse;
}

.profile {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-right: 8px;
}

.message-wrapper.self .profile {
  margin-right: 0;
  margin-left: 8px;
}

.profile-img {
  width: 35px;
  height: 35px;
  border-radius: 50%;
  margin-bottom: 4px;
}

.nickname {
  font-size: 11px;
  color: #666;
  margin-bottom: 3px;
}

.message-content {
  display: flex;
  flex-direction: column;
}

.message-bubble {
  padding: 8px 12px;
  border-radius: 15px;
  max-width: 450px;
  word-break: break-word;
}

.message-wrapper.other .message-bubble {
  background: white;
  border: 1px solid #ddd;
  border-top-left-radius: 0;
}

.message-wrapper.self .message-bubble {
  background: #fee500;
  border-top-right-radius: 0;
}

.chat-input {
  padding: 15px;
  background: white;
  border-top: 1px solid #eee;
  display: flex;
  gap: 10px;
}

.chat-input input {
  flex: 1;
  padding: 12px 15px;
  border: 1px solid #ddd;
  border-radius: 20px;
  outline: none;
  font-size: 14px;
}

.chat-input button {
  padding: 8px 20px;
  background: #fee500;
  color: black;
  border: none;
  border-radius: 20px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.2s;
}

.chat-input button:hover {
  background: #fada0a;
}

/* 모달 스타일 */
.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.chat-room-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 450px;
  max-height: calc(100vh - 40px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  animation: modalFadeIn 0.3s ease-out;
  padding: 20px;
}

.modal-content h2 {
  margin-top: 0;
  margin-bottom: 20px;
  text-align: center;
}

.modal-content ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.modal-content li {
  padding: 15px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: background-color 0.2s;
}

.modal-content li:hover {
  background-color: #f8f9fa;
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 400px;
  color: #666;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes modalFadeIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
