<template>
  <div class="main-container">
    <div class="main-layout-container">
      <div class="main-intro-group">
        <div class="main-intro">
          <div><h2>성공과 건강을 위한 스마트한 선택</h2></div>
          <div class="main-intro-small">
            직장인들을 위한 헬스케어<br />
            오직 <span class="highlight-orange">CAREER FIT</span> 에서
          </div>
        </div>
        <!-- AI 버튼을 클릭하면 모달을 여는 이벤트 연결 -->
        <div class="main-ai-chatbot" id="chatbot" @click="openChatbot">
          <svg
            style="width: 40px; height: 43px; margin-left: 5px"
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 24 24"
            fill="currentColor"
            stroke-width="0.5"
          >
            <path
              d="M10 3H14C18.4183 3 22 6.58172 22 11C22 15.4183 18.4183 19 14 19V22.5C9 20.5 2 17.5 2 11C2 6.58172 5.58172 3 10 3ZM12 17H14C17.3137 17 20 14.3137 20 11C20 7.68629 17.3137 5 14 5H10C6.68629 5 4 7.68629 4 11C4 14.61 6.46208 16.9656 12 19.4798V17Z"
            ></path>
            <text x="12" y="14" text-anchor="middle" fill="white" font-size="8">
              AI
            </text>
          </svg>
        </div>
      </div>
      <!-- 모달창 표시 여부에 따라 모달을 띄운다 -->
      <div v-if="showModal" class="modal-overlay" @click="closeChatbot">
        <div class="modal-content" @click.stop>
          <div class="chat-header">
            <h3>careerfit 식단관리사 마이구민입니다!</h3>
            <button @click="closeChatbot" class="close-btn">X</button>
          </div>
          <div class="chat-body">
            <div
              v-for="(message, index) in messages"
              :key="index"
              :class="['chat-message', message.role]"
            >
              <p v-html="message.content" class="message"></p>
            </div>
          </div>
          <div class="chat-input">
            <input
              v-model="userMessage"
              @keyup.enter="sendMessage"
              type="text"
              placeholder="메시지를 입력하세요..."
            />
            <button @click="sendMessage">전송</button>
          </div>
        </div>
      </div>
      <div class="main-board-group">
        <div class="comunity-group">
          <div class="comunity-header">
            <div class="comunity-header-text">
              <router-link to="/board">커뮤니티</router-link>
            </div>
          </div>
          <div v-for="n in 5" :key="n" class="main-board-content-group">
            <div class="main-board-content-layout">
              <div class="main-board-content-left">
                <div class="profile-img">
                  <img src="@/assets/img/snoopy.png" alt="" />
                </div>
                <div class="nickname">초대리</div>
                <div class="material-icon">
                  <img
                    src="@/assets/img/edit_square_24dp_5F6368_FILL0_wght400_GRAD0_opsz24.png"
                    alt=""
                  />
                </div>
                <div class="date-time">6분 전</div>
              </div>
              <div class="main-board-content-right">
                <div class="material-icon">
                  <img
                    src="@/assets/img/thumb_up_24dp_5F6368_FILL0_wght400_GRAD0_opsz24.png"
                    alt=""
                  />
                  <div>24</div>
                </div>
                <div class="material-icon">
                  <img
                    src="@/assets/img/chat_24dp_5F6368_FILL0_wght400_GRAD0_opsz24.png"
                    alt=""
                  />
                  <div>24</div>
                </div>
              </div>
            </div>
            <div class="contents">데이터 솔루션에 대해 들어보신분??</div>
          </div>
        </div>

        <div class="comunity-group">
          <div class="comunity-header">
            <div class="comunity-header-text">
              <router-link to="/board">커뮤니티</router-link>
            </div>
          </div>
          <div v-for="n in 5" :key="n" class="main-board-content-group">
            <div class="main-board-content-layout">
              <div class="main-board-content-left">
                <div class="profile-img">
                  <img src="@/assets/img/snoopy.png" alt="" />
                </div>
                <div class="nickname">초대리</div>
                <div class="material-icon">
                  <img
                    src="@/assets/img/edit_square_24dp_5F6368_FILL0_wght400_GRAD0_opsz24.png"
                    alt=""
                  />
                </div>
                <div class="date-time">6분 전</div>
              </div>
              <div class="main-board-content-right">
                <div class="material-icon">
                  <img
                    src="@/assets/img/thumb_up_24dp_5F6368_FILL0_wght400_GRAD0_opsz24.png"
                    alt=""
                  />
                  <div>24</div>
                </div>
                <div class="material-icon">
                  <img
                    src="@/assets/img/chat_24dp_5F6368_FILL0_wght400_GRAD0_opsz24.png"
                    alt=""
                  />
                  <div>24</div>
                </div>
              </div>
            </div>
            <div class="contents">데이터 솔루션에 대해 들어보신분??</div>
          </div>
        </div>
      </div>
      <!-- <div class="main-health-ranking-group">헬스장 랭킹</div> -->
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import api from "@/api/axiosInstance";
import { marked } from "marked";

const showModal = ref(false);
const userMessage = ref("");
const messages = ref([]);
const isFirst = ref("true");

// 모달 창 열기
const openChatbot = async () => {
  showModal.value = true;
  if (isFirst.value) {
    try {
      console.log(isFirst.value);
      const response = await api.post("/api/gpt/first");
      const botMessage = response.data.choices[0].message.content;
      console.log(botMessage);
      messages.value.push({ role: "assistance", content: botMessage });
    } catch (error) {
      console.error("Error sending message:", error);
      ``;
    }
    isFirst.value = false;
  }
};

// 모달 창 닫기
const closeChatbot = () => {
  showModal.value = false;
};

// 메시지 전송
const sendMessage = async () => {
  if (userMessage.value.trim() === "") return;
  const userMessageData = { role: "user", content: userMessage.value };
  userMessage.value = "";
  messages.value.push(userMessageData);
  try {
    const response = await api.post("/api/gpt", {
      message: userMessageData.content,
    });
    const botMessage = {
      role: "assistance",
      content: response.data.choices[0].message.content,
    };
    // GPT 응답 처리
    const botMessageContent = response.data.choices[0].message.content; // content 가져오기
    const formattedBotMessageContent = marked(botMessageContent);

    messages.value.push({
      role: "assistant",
      content: formattedBotMessageContent,
    });
  } catch (error) {
    console.error("Error sending message:", error);
  }
};
</script>

<style lang="css" scoped>
@import url(@/assets/css/main-content.css);
.comunity-header-text > a {
  text-decoration: none;
  color: black;
}
/* 모달 스타일 */
.modal-overlay {
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
  display: flex;
  background: white;
  flex-direction: column; /* 세로 방향 정렬 */
  justify-content: space-between; /* 위아래 공간 분배 */
  padding: 20px;
  width: 700px;
  min-height: 900px;
  max-height: 900px;
  border-radius: 8px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.close-btn {
  background: #e0e0e0; /* 밝은 회색 배경 */
  border: 1px solid #bdbdbd; /* 테두리를 약간 더 진한 회색으로 */
  border-radius: 50%; /* 원형 모양 */
  font-size: 16px;
  font-weight: bold;
  width: 36px; /* 버튼 크기 */
  height: 36px;
  display: flex;
  justify-content: center; /* 텍스트 중앙 정렬 */
  align-items: center;
  cursor: pointer;
  color: #757575; /* 텍스트 색상을 진한 회색으로 */
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); /* 은은한 그림자 */
  transition: background 0.3s ease, transform 0.2s ease, box-shadow 0.3s ease; /* 부드러운 효과 */
}

.close-btn:hover {
  background: #bdbdbd; /* 호버 시 더 진한 회색 */
  color: #ffffff; /* 텍스트를 흰색으로 */
  box-shadow: 0 6px 10px rgba(0, 0, 0, 0.2); /* 그림자 강조 */
  transform: scale(1.05); /* 살짝 확대 */
}

.close-btn:active {
  background: #9e9e9e; /* 클릭 시 어두운 회색 */
  box-shadow: 0 3px 5px rgba(0, 0, 0, 0.15); /* 그림자 줄임 */
  transform: scale(0.95); /* 클릭감 */
}
.chat-body {
  flex-grow: 1; /* 중간 영역 확장 */
  max-height: 800px;
  overflow-y: auto;
  margin-bottom: 20px;
}

.chat-message {
  padding: 8px;
  margin-bottom: 5px;
}

.chat-message .message {
  font-size: 14px;
}

.chat-input {
  display: flex;
  justify-content: space-between; /* 입력창과 버튼 간격 유지 */
  align-items: center; /* 수직 정렬 */
  margin-top: auto; /* 위쪽 여백 자동 */
  padding-top: 10px;
  border-top: 1px solid #ccc; /* 상단 경계선 */
}

.chat-input input {
  width: 80%;
  padding: 10px;
  font-size: 14px;
  border-radius: 4px;
  border: 1px solid #ccc;
}

.chat-input button {
  padding: 10px 15px;
  font-size: 14px;
  cursor: pointer;
  border: none;
  background-color: #ff7f32;
  color: white;
  border-radius: 4px;
}

.chat-message {
  margin-bottom: 10px;
  padding: 10px;
  border-radius: 8px;
  max-width: 70%;
  font-size: 14px;
}

.chat-message.user {
  background-color: #d1f7c4; /* 사용자 메시지의 배경색 */
  align-self: flex-end; /* 오른쪽 정렬 */
  text-align: right;
}

.chat-message.assistance {
  background-color: #f1f0f0; /* 봇 메시지의 배경색 */
  align-self: flex-start; /* 왼쪽 정렬 */
  text-align: left;
}
.chat-body {
  display: flex;
  flex-direction: column;
  gap: 10px; /* 메시지 간 간격 */
}

.chat-message {
  white-space: pre-wrap; /* 줄바꿈을 유지 */
  margin-bottom: 10px;
  padding: 10px;
  border-radius: 8px;
  font-size: 14px;
  background-color: #f1f0f0; /* 봇 메시지 배경색 */
}

.chat-message.user {
  background-color: #d1f7c4; /* 사용자 메시지 배경색 */
  text-align: right;
}

.chat-message.assistance {
  background-color: #f1f0f0;
  align-self: flex-start;
  color: #000000;
}
</style>
