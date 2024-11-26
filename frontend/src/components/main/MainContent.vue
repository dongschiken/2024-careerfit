<template>
  <div class="main-container" @mousemove="trackMouse">
    <div class="main-layout-container">
      <div class="main-intro-group">
        <div class="main-intro-container">
          <div class="main-intro">
            <div><h2>성공과 건강을 위한 스마트한 선택</h2></div>
            <div class="main-intro-small">
              직장인들을 위한 헬스케어<br />
              오직 <span class="highlight-orange">CAREER FIT</span> 에서
            </div>
          </div>
          <div class="mascot-container">
            <div class="mascot">
              <div class="eye left-eye">
                <div class="pupil" ref="leftPupil"></div>
              </div>
              <img src="@/assets/img/마이구민.png" />
              <div class="eye right-eye">
                <div class="pupil" ref="rightPupil"></div>
              </div>
            </div>
          </div>
        </div>
        <div class="ranking-container">
          <div
            class="ranking-layout"
            v-for="mealStreak in mealStrakRank"
            :key="mealStreak.rank"
          >
            <div class="ranking-box">
              <img v-if="mealStreak.rank === 1" src="@/assets/img/gold.png" />
              <img
                v-else-if="mealStreak.rank === 2"
                src="@/assets/img/silver.png"
              />
              <img v-else src="@/assets/img/bronze.png" />
              <div>
                <div class="user-nickname">
                  <b>{{ mealStreak.nickname }}</b
                  >&nbsp;님
                </div>
                <div class="user-meal-streak">
                  총 {{ mealStreak.recordCount }} 일 식단 기록 진행중 🔥
                </div>
              </div>
            </div>
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
          <div class="chat-body" ref="chatBody">
            <div
              v-for="(message, index) in messages"
              :key="index"
              :class="['chat-message', message.role]"
              :ref="setMessageRef(index)"
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
        <MainBoardLeft />
        <MainBoardRight />
      </div>
      <!-- <div class="main-health-ranking-group">헬스장 랭킹</div> -->
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from "vue";
import api from "@/api/axiosInstance";
import { marked } from "marked";
import router from "@/router";
const showModal = ref(false);
const userMessage = ref("");
const messages = ref([]);
const messageRefs = ref([]);
const chatBody = ref(null);
const isFirst = ref("true");
import MainBoardLeft from "./MainBoardLeft.vue";
import MainBoardRight from "./MainBoardRight.vue";
import ncapi from "@/api/noTokenAxiosInstance";

const mealStrakRank = ref([]);

const openChatbot = async () => {
  showModal.value = true;
  if (isFirst.value) {
    try {
      const response = await api.post("/api/gpt/first");
      const botMessage = response.data.choices[0].message.content;
      messages.value.push({ role: "assistance", content: botMessage });
    } catch (error) {
      console.error("Error sending message:", error);
    }
    isFirst.value = false;
  }
};

// 현재 메시지의 상단으로 이동
const scrollToMessageTop = (index) => {
  if (messageRefs.value[index]) {
    const targetMessage = messageRefs.value[index];
    const chatBodyElement = chatBody.value;

    if (chatBodyElement) {
      chatBodyElement.scrollTop = targetMessage.offsetTop; // 현재 메시지 상단으로 이동
    }
  }
};

const setMessageRef = (index) => (el) => {
  if (el) {
    messageRefs.value[index] = el;
  }
};

const trackMouse = (event) => {
  // 눈의 좌표 계산
  const leftEye = document.querySelector(".left-eye").getBoundingClientRect();
  const rightEye = document.querySelector(".right-eye").getBoundingClientRect();

  // 눈동자 움직임 계산 함수
  const movePupil = (eye, pupil, mouseX, mouseY) => {
    const eyeCenterX = eye.left + eye.width / 2;
    const eyeCenterY = eye.top + eye.height / 2;

    const deltaX = mouseX - eyeCenterX;
    const deltaY = mouseY - eyeCenterY;

    const distance = Math.min(Math.sqrt(deltaX ** 2 + deltaY ** 2), 10); // 최대 이동 거리 제한 (10px)
    const angle = Math.atan2(deltaY, deltaX);

    // 눈동자의 이동 거리 계산
    const offsetX = Math.cos(angle) * distance;
    const offsetY = Math.sin(angle) * distance;

    // 눈동자 이동 스타일 적용
    pupil.style.transform = `translate(${offsetX}px, ${offsetY}px)`;
  };

  // 마우스 위치
  const mouseX = event.clientX;
  const mouseY = event.clientY;

  // 왼쪽, 오른쪽 눈동자 이동
  movePupil(
    leftEye,
    document.querySelector(".left-eye .pupil"),
    mouseX,
    mouseY
  );
  movePupil(
    rightEye,
    document.querySelector(".right-eye .pupil"),
    mouseX,
    mouseY
  );
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
    // 메시지 추가 후 스크롤 이동
    await nextTick();
    scrollToMessage(messages.value.length - 1); // 마지막 메시지
    // GPT 응답 처리
    const botMessageContent = response.data.choices[0].message.content; // content 가져오기
    const formattedBotMessageContent = marked(botMessageContent);
    messages.value.push({
      role: "assistant",
      content: formattedBotMessageContent,
    });
    // 메시지 추가 후 스크롤 이동
    await nextTick();
    scrollToMessage(messages.value.length - 1); // 마지막 메시지
    if (response.status === 201) {
      setTimeout(() => {
        const check = confirm("마이구민이 등록한 식단을 보러갈까요?");
        if (check) {
          router.push("/meal"); // Vue Router 경로 사용 시
        }
      }, 1000); // 15초 딜레이
    }
  } catch (error) {
    console.error("Error sending message:", error);
  }
};

const scrollToMessage = (index) => {
  const chatBodyElement = chatBody.value; // 채팅 컨테이너
  if (messageRefs.value[index] && chatBodyElement) {
    const targetMessage = messageRefs.value[index];
    chatBodyElement.scrollTop = targetMessage.offsetTop; // 메시지의 상단으로 스크롤 이동
  }
};

const getMealStreakRank = async () => {
  try {
    const response = await ncapi.get("/api/meal/record/streak/rank");
    mealStrakRank.value = response.data;
    console.log(mealStrakRank.value);
  } catch (error) {
    console.log(error);
  }
};
onMounted(() => {
  getMealStreakRank();
});
</script>

<style lang="css" scoped>
@import url(@/assets/css/main-content.css);
.ranking-container {
  margin-top: 130px;
}
li {
  margin-left: 30px;
}
.ranking-box > img {
  min-width: 70px;
  max-width: 70px;
  min-height: 70px;
  max-height: 70px;
}
.ranking-box {
  display: flex;
  flex-direction: column;
  margin-top: 10px;
}
.user-meal-streak {
  display: flex;
  justify-content: center;
  min-width: 300px;
  max-width: 300px;
}
.user-nickname {
  display: flex;
  justify-content: center;
  min-width: 300px;
  max-width: 300px;
}

.ranking-container {
  margin-top: 50px; /* 전체 상단 여백 */
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px; /* 랭킹 박스 간 간격 */
}

.ranking-box {
  display: flex;
  flex-direction: row; /* 수평 레이아웃 */
  align-items: center;
  border-radius: 15px; /* 둥근 모서리 */
  box-shadow: 0 8px 15px rgba(0, 0, 0, 0.1); /* 부드러운 그림자 */
  padding: 20px 30px;
  width: 90%; /* 화면 폭에 맞춤 */
  max-width: 600px; /* 최대 크기 제한 */
  transition: transform 0.3s, box-shadow 0.3s; /* 호버 효과 */
}

.ranking-box:hover {
  transform: translateY(-5px); /* 호버 시 박스 위로 살짝 이동 */
  box-shadow: 0 12px 20px rgba(0, 0, 0, 0.2); /* 호버 시 그림자 강조 */
}

.ranking-box img {
  width: 80px;
  height: 80px;
  margin-right: 20px; /* 이미지와 텍스트 간격 */
  border-radius: 50%; /* 이미지 둥글게 */
  border: 3px solid #fff; /* 이미지 외곽 테두리 */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2); /* 이미지 그림자 */
}

.ranking-info {
  display: flex;
  flex-direction: column;
  justify-content: center;
  flex: 1; /* 텍스트 영역 확장 */
}

.ranking-info .user-nickname {
  font-size: 20px; /* 닉네임 폰트 크기 */
  font-weight: bold;
  color: #333; /* 진한 텍스트 색상 */
  margin-bottom: 5px;
}

.ranking-info .user-meal-streak {
  font-size: 16px; /* 기록 텍스트 크기 */
  color: #555; /* 중간 밝기의 색상 */
}

.ranking-rank {
  font-size: 36px; /* 랭킹 숫자 크기 */
  font-weight: bold;
  color: #ff9800; /* 강조 색상 */
  text-align: center;
  align-self: flex-end; /* 숫자를 박스 끝으로 정렬 */
  min-width: 60px; /* 최소 공간 확보 */
}
div.main-intro-container > div.main-intro > div:nth-child(1) > h2 {
  font-size: 30px;
}
.main-intro-small {
  font-size: 23px;
}
#chatbot > svg > text {
  padding-bottom: 5px;
}
.user-nickname {
  font-size: 25px;
}
.user-meal-streak {
  margin-top: 5px;
  margin-left: 8px;
  font-size: 20px;
}
</style>
