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
                <RouterLink to="/meal">
                  <button class="menu-button">
                    <i class="fas fa-utensils"></i>
                    나의 식단
                  </button>
                </RouterLink>
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

html, 
body {
  background: #fff5ec;
  margin: 0;
  padding: 0;
}

.page-wrapper {
  min-height: calc(90vh - 160px);
  background: #fff5ec; /* 배경색 통일 */
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 30px 20px; /* 패딩 줄임 */
  margin: 20px 0; /* 마진 줄임 */
}

.page-container {
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
}

.user-profile-container {
  background: white;
  border-radius: 24px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  animation: fadeIn 0.3s ease;
  margin: -60px auto 0; /* 상단 여백을 음수값으로 주어 위로 올림 */
}


.profile-header {
  background: linear-gradient(45deg, #FF7D29, #FFBF78);
  padding: 25px;
  text-align: center;
}

.profile-title {
  color: white;
  font-size: 1.6rem;
  font-weight: 700;
  margin: 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
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
  border: 4px solid #fff;
  box-shadow: 0 8px 20px rgba(255, 125, 41, 0.2);
  transition: transform 0.3s ease;
}

.profile-image:hover {
  transform: scale(1.05);
}

.edit-info-button {
  position: absolute;
  bottom: 5px;
  right: 5px;
  background: linear-gradient(45deg, #FF7D29, #FFBF78);
  color: white;
  border: none;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(255, 125, 41, 0.3);
}

.edit-info-button:hover {
  transform: rotate(15deg) scale(1.1);
}

.user-email {
  color: #4a5568;
  font-size: 1.1rem;
  margin: 20px 0;
  padding: 12px 24px;
  background: #f8fafc;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.button-group {
  margin: 25px 0;
  display: flex;
  gap: 15px;
}

.edit-button {
  padding: 12px 24px;
  background: linear-gradient(45deg, #FF7D29, #FFBF78);
  color: white;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 600;
  font-size: 0.95rem;
  box-shadow: 0 4px 12px rgba(255, 125, 41, 0.2);
}

.edit-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(255, 125, 41, 0.3);
}

.menu-grid {
  width: 100%;
  display: grid;
  grid-template-columns: repeat(2, 1fr); /* 2열로 균등하게 나누기 */
  grid-template-rows: repeat(2, 1fr);    /* 2행으로 균등하게 나누기 */
  gap: 20px;
  margin-top: 30px;
  padding: 0 20px;
}

.menu-button {
  position: relative;
  width: 100%; /* 너비를 100%로 설정 */
  padding: 25px 20px;
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 12px;
  transition: all 0.3s ease;
  color: #2d3748;
  font-weight: 600;
  font-size: 1.1rem;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  min-height: 80px; /* 최소 높이 설정으로 모든 버튼의 높이를 통일 */
}

.menu-grid a {
  display: block;
  width: 100%; /* RouterLink도 전체 너비를 차지하도록 설정 */
  text-decoration: none; /* 밑줄 제거 */
}

.menu-grid a button {
  width: 100%; /* RouterLink 안의 버튼도 전체 너비를 차지하도록 설정 */
}

.menu-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(45deg, rgba(255, 125, 41, 0.1), rgba(255, 191, 120, 0.1));
  opacity: 0;
  transition: opacity 0.3s ease;
}

.menu-button:hover {
  transform: translateY(-3px);
  border-color: #FFBF78;
  box-shadow: 0 8px 20px rgba(255, 125, 41, 0.15);
}

.menu-button:hover::before {
  opacity: 1;
}

.menu-button i {
  color: #FF7D29;
  font-size: 1.4rem;
  transition: transform 0.3s ease;
}

.menu-button:hover i {
  transform: scale(1.2);
}

/* 채팅 관련 스타일 업데이트 */
.chat-room-modal {
  width: 60%;
  max-width: 600px;
  height: 70vh;
  background: white;
  border-radius: 24px;
  display: flex;
  flex-direction: column;
  position: relative;
  animation: modalSlideUp 0.2s ease;
  padding: 0;
}

.chat-room-header {
  padding: 20px 25px;
  border-bottom: 1px solid #edf2f7;
  background: white;
  border-radius: 25px;
}

.chat-room-title {
  font-size: 1.3rem;
  font-weight: 600;
  color: #2d3748;
  margin: 0;
  text-align: center;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #f8fafc;
  display: flex;
  flex-direction: column-reverse;
  margin-bottom: 80px;
}

.chat-messages::-webkit-scrollbar {
  width: 5px;
}

.chat-messages::-webkit-scrollbar-track {
  background: #f1f1f1;
}


.chat-messages::-webkit-scrollbar-thumb {
  background: #FF7D29;
  border-radius: 10px;
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: #999;
}

.message-wrapper {
  display: flex;
  margin-bottom: 2px;
  max-width: 70%;
  animation: fadeIn 0.2s ease;
}

.message-wrapper.self {
  margin-left: auto;
  flex-direction: row-reverse;
}

.message-wrapper.other {
  align-items: flex-start;
}

.message-group {
  display: flex;
  align-items: flex-end;
}

.message-wrapper.self .message-group {
  flex-direction: row-reverse;
}

.profile {
  margin: 0 10px;
}

.message-wrapper.self .profile {
  margin-right: 0;
  margin-left: 8px;
}

.profile-img {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
}

.nickname {
  font-size: 0.8rem;
  color: #718096;
  margin-bottom: 2px;
}

.message-content {
  display: flex;
  flex-direction: column;
}

.message-bubble {
  padding: 8px 12px;
  border-radius: 12px;
  font-size: 0.95rem;
  line-height: 1.4;
  max-width: 100%;
  word-break: break-word;
}

.message-wrapper.self .message-bubble {
  background: linear-gradient(45deg, #FF7D29, #FFBF78);
  color: white;
  border-top-right-radius: 0;
}

.message-wrapper.other .message-bubble {
  background: white;
  color: #2d3748;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  border-top-left-radius: 0;
}

.chat-input {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20px 30px;
  background: white;
  border-top: 1px solid #edf2f7;
  display: flex;
  gap: 12px;
  align-items: center;
  height: 80px;
  box-sizing: border-box;
  border-radius: 25px;
}

.chat-input input {
  flex: 1;
  padding: 12px 20px;
  border: 1px solid #e2e8f0;
  border-radius: 20px;
  font-size: 0.95rem;
  transition: all 0.2s ease;
  background: #f8fafc;
  height: 25px;
}

.chat-input input:focus {
  outline: none;
  border-color: #FF7D29;
  background: white;
  box-shadow: 0 0 0 3px rgba(255, 125, 41, 0.1);
}

.chat-input button {
  padding: 12px 24px;
  background: linear-gradient(45deg, #FF7D29, #FFBF78);
  color: white;
  border: none;
  border-radius: 20px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  min-width: 80px;
  height: 45px;
}

.chat-input button:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(255, 125, 41, 0.2);
}

.chat-input button:disabled {
  background: #e2e8f0;
  cursor: not-allowed;
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
  border-radius: 24px;
  width: 400px;
  max-height: calc(100vh - 40px);
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
  animation: modalFadeIn 0.3s ease;
  padding: 30px;
}

.modal-content h2 {
  font-size: 1.8rem;
  font-weight: 700;
  color: #2d3748;
  margin-bottom: 20px;
  text-align: center;
  border-bottom: 2px solid #FF7D29;
  padding-bottom: 15px;
}

.modal-content ul {
  list-style: none;
  padding: 0;
  margin: 0;
  overflow-y: auto;
  max-height: 450px;
}

.modal-content ul::-webkit-scrollbar {
  width: 6px;
}

.modal-content ul::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 10px;
}

.modal-content ul::-webkit-scrollbar-thumb {
  background: #FF7D29;
  border-radius: 10px;
}

.modal-content li {
  padding: 10px;
  margin-bottom: 12px;
  margin-right: 10px;
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  cursor: pointer;
  transition: all 0.2s ease;
}

.modal-content li:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(255, 125, 41, 0.15);
  border-color: #FFBF78;
}

.modal-content li > div {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

/* 채팅방 제목 스타일 */
.modal-content li p:first-child {
  font-size: 1.1rem;
  font-weight: 600;
  color: #2d3748;
  margin: 0;
  padding-bottom: 8px;
  border-bottom: 1px solid #edf2f7;
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 400px;
  color: #666;
}

.modal-content li p:nth-child(2) {
  font-size: 0.95rem;
  color: #718096;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 시간 정보 스타일 */
.modal-content li p:last-child {
  font-size: 0.85rem;
  color: #a0aec0;
  margin: 0;
  text-align: right;
  padding-top: 4px;
}

/* 스크롤바 스타일 */
.modal-content ul::-webkit-scrollbar {
  width: 6px;
}

.modal-content ul::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 10px;
}

.modal-content ul::-webkit-scrollbar-thumb {
  background: #FF7D29;
  border-radius: 10px;
}

/* 빈 목록 메시지 스타일 */
.modal-content p:only-child {
  text-align: center;
  color: #718096;
  padding: 30px;
  background: #f8fafc;
  border-radius: 12px;
  margin: 20px 0;
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
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}


@media (max-width: 768px) {
  .page-wrapper {
    min-height: calc(100vh - 160px);
    padding: 15px;
    margin: 15px 0;
  }
}

@media (min-height: 900px) {
  .page-wrapper {
    margin: 0px 0; /* 큰 화면에서도 여백을 적당히 */
  }
}
</style>
2번째