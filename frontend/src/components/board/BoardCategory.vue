<template>
  <div>
    <div
      v-for="board in boardStore.boardList"
      :key="board.boardId"
      class="board-content-list-group"
    >
      <div class="board-content-left">
        <div @click="getBoardDetail(board.boardId)" class="board-content-title">
          {{ board.title }}
        </div>
        <div
          @click="getBoardDetail(board.boardId)"
          class="board-content-content"
        >
          <p class="ellipsis">
            {{ board.content }}
          </p>
        </div>
        <div class="user-profile-group">
          <div
            class="author-avatar"
            :class="{
              'gradient-animation':
                !board.user.profileUrl ||
                board.user.profileUrl === '' ||
                board.user.profileUrl == null,
            }"
          ></div>
          <div class="user-profile-name">{{ board.user.nickname }}</div>
        </div>
        <div class="board-content-left-category">
          <b> {{ board.category.name }}</b>
        </div>
      </div>
      <div class="board-content-right">
        <div class="board-content-right-img obj-div">
          <img
            @click="getBoardDetail(board.boardId)"
            class="obj-img"
            v-if="mainImage(board)"
            :src="`http://localhost:8080/uploads/${mainImage(board).path}${
              mainImage(board).systemName
            }`"
            alt="Board Image"
            style="border: none; background: none; padding: 0; cursor: pointer"
          />
        </div>
        <div class="board-content-right-footer">
          <span class="date-time">{{ board.timeAgo }}</span>
          <span class="like-count">
            <img
              src="@/assets/img/thumb_up_24dp_5F6368_FILL0_wght400_GRAD0_opsz24.png"
              alt=""
            />
            <span>{{ board.likeCount }}개</span>
          </span>
          <span class="reply-count">
            <img
              src="@/assets/img/chat_24dp_5F6368_FILL0_wght400_GRAD0_opsz24.png"
              alt=""
            />
            <span>{{ board.replyCount }}</span>
          </span>
          <span class="view-count">
            <img
              src="@/assets/img/visibility_24dp_5F6368_FILL0_wght400_GRAD0_opsz24.png"
              alt=""
            />
            <span>{{ board.viewCount }}개</span>
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from "vue-router";
import { useBoardStore } from "@/stores/board";
import axios from "axios";
import { onMounted, ref, computed } from "vue";
const boardStore = useBoardStore();
const router = useRouter();
function mainImage(board) {
  return board.boardImgs.find((img) => img.mainWhether === "M");
}

const getBoardDetail = (boardId) => {
  router.push({ name: "boardDetail", params: { boardId } });
};

// 이미지 배경 스타일
function getBackgroundImage(profileUrl) {
  return profileUrl
    ? { backgroundImage: `url(${profileUrl})` }
    : { background: "linear-gradient(135deg, #FF7F50, #FFB6C1, #87CEFA)" };
}

onMounted(() => {
  axios.get(boardStore.REST_API_URL).then((response) => {
    boardStore.getBoardList(response.data);
  });
});
</script>

<style lang="css" scoped>
@import url(@/assets/css/board-category.css);

.board-content-content,
.board-content-title {
  cursor: pointer;
}
div.board-content-right-img.obj-div > img {
  border-radius: 10px;
}
.gradient-animation {
  background: linear-gradient(135deg, #ff7f50, #ffb6c1, #87cefa);
  background-size: 200% 200%;
  animation: gradientShift 5s infinite;
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
.author-avatar {
  width: 35px;
  height: 35px;
  border-radius: 50%;
}
</style>
