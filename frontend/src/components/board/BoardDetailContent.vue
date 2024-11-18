<template>
  <div v-if="!isLoading" class="board-container">
    <div class="board-community-header">
      <span class="board-category">{{ board.category.name }}</span>
      <span class="board-category">{{ board.address }}</span>
    </div>
    <div class="board-header">
      <h2>{{ board.title }}</h2>
      <div class="board-author">
        <div
          class="author-avatar"
          :class="{ 'gradient-animation': !profileUrl }"
          :style="profileUrl ? backgroundImage : null"
        ></div>
        <span class="author-name">{{ board.user.nickname }}</span>
      </div>
      <div class="board-info">
        <span class="icon-group">{{ board.timeAgo }}</span>
        <span class="icon-group"
          ><img src="@/assets/img/view.png" />
          <span>{{ board.viewCount }}</span></span
        >
        <span class="icon-group"
          ><img
            src="@/assets/img/chat_24dp_5F6368_FILL0_wght400_GRAD0_opsz24.png"
          />
          <span>{{ board.viewCount }}</span></span
        >
        <div v-if="board.user.email == user.email">
          <span class="board-detail-user-btn"
            ><button @click="updateBoard(boardId)">수정</button></span
          >
          <span class="board-detail-user-btn"
            ><button @click="deleteBoard(boardId)">삭제</button></span
          >
        </div>
      </div>
    </div>
    <div class="board-body">
      <p class="board-content">
        {{ board.content }}
      </p>
    </div>
    <div
      v-for="image in board.boardImgs"
      :key="board.boardImgs.boardImgsId"
      class="image-container"
    >
      <img
        class="board-image"
        :src="`http://localhost:8080/uploads/${image.path}${image.systemName}`"
      />
    </div>
  </div>
</template>

<script setup>
import api from "@/api/axiosInstance";
import { defineProps, computed, onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useBoardStore } from "@/stores/board";
import axios from "axios";
const route = useRoute();
const router = useRouter();
const isLoading = ref(true);
const boardStore = useBoardStore();
// user 이메일이 같을 경우 수정버튼을 보이게 한다.
const user = ref({
  id: 0,
  email: "test@example.com",
  role: "",
});
const token = sessionStorage.getItem("accessToken");
defineProps({
  boardId: {
    type: Number,
    required: true,
  },
});

const board = ref({
  boardImgs: [],
});
const boardId = ref(Number(route.params.boardId)); // 명시적 변환
const getBoard = async (boardId) => {
  try {
    const response = await api.get(`/api/board/${boardId}`);
    board.value = response.data;
    console.log("게시글 데이터:", board.value);
  } catch (error) {
    console.log(error);
    alert("게시글 데이터를 가져오는 중 문제가 발생했습니다.");
  } finally {
    isLoading.value = false;
  }
};

// 프로필 이미지 경로
const profileUrl = board.profileUrl
  ? `http://localhost:8080/uploads/${board.profileUrl}`
  : null;

// 이미지 배경 스타일
const backgroundImage = computed(() => ({
  backgroundImage: `url(${profileUrl})`,
}));

const gradientBackground = computed(() => ({
  backgroundImage: "linear-gradient(135deg, #FF7F50, #FFB6C1, #87CEFA)",
  backgroundSize: "cover",
  backgroundPosition: "center",
}));

const deleteBoard = async (boardId) => {
  try {
    const isDelete = confirm("정말 삭제하시겠습니까?");
    if (!isDelete) return;
    const response = await axios.put(boardStore.REST_API_URL + `/${boardId}`);
    router.replace({
      name: "board",
    });
  } catch (error) {
    alert("게시글 삭제 처리중 오류발생");
  }
};

const updateBoard = (boardId) => {
  router.push({
    name: "writeEdit",
    value: boardId,
  });
};

onMounted(() => {
  getBoard(boardId.value);
});
</script>

<style scoped>
.board-container {
  min-width: 800px;
  max-width: 800px;
  margin: 0 auto;
  padding: 16px;
  background-color: #fff;
  border-radius: 8px;
  border: 1px solid #ddd;
  margin-top: 50px;
}

.board-community-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
  font-size: 14px;
  color: #666;
}

.board-category {
  font-weight: bold;
}

.board-header {
  margin-bottom: 16px;
}

.board-header h2 {
  font-size: 24px;
  margin-bottom: 8px;
}

.board-author {
  display: flex;
  align-items: center;
  margin-top: 20px;
  margin-bottom: 8px;
}

.author-avatar {
  width: 35px;
  height: 35px;
  border-radius: 50%;
}

.author-name {
  font-size: 14px;
  font-weight: bold;
  margin-left: 10px;
  font-size: 15px;
}

.board-info {
  font-size: 12px;
  color: #888;
  display: flex;
  align-items: start;
  gap: 8px;
}

.board-body {
  margin-top: 16px;
}

.board-image {
  margin-top: 10px;
  min-width: 500px;
  max-width: 500px;
  min-height: 300px;
  max-height: 300px;
  height: auto;
  border-radius: 4px;
}

.board-content {
  font-size: 14px;
  line-height: 1.6;
  color: #333;
  white-space: pre-wrap;
  margin-top: 45px;
}
.image-container {
  display: flex;
  justify-content: center;
}
.board-info > span > img {
  width: 20px;
  margin-right: 5px;
}
.icon-group {
  display: flex;
  margin-left: 3px;
  font-size: 14px;
}

.gradient-animation {
  animation: gradientShift 5s infinite;
  background: linear-gradient(135deg, #ff7f50, #ffb6c1, #87cefa);
  background-size: 200% 200%;
}

@keyframes gradientShift {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}
.board-detail-user-btn {
  margin-left: 10px;
}
.board-detail-user-btn > button {
  border: 0;
  background-color: transparent;
}
.board-detail-user-btn > button:hover {
  cursor: pointer;
}
</style>
