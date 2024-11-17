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
          <div class="user-profile-img">
            <img src="@/assets/img/snoopy.png" alt="" />
          </div>
          <div class="user-profile-name">{{ board.user.nickname }}</div>
        </div>
        <div class="board-content-left-category">{{ board.category.name }}</div>
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
            <span>10개</span>
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
import { onMounted, ref } from "vue";
const boardStore = useBoardStore();
const router = useRouter();
const token = ref("");
function mainImage(board) {
  return board.boardImgs.find((img) => img.mainWhether === "M");
}

onMounted(() => {
  axios.get(boardStore.REST_API_URL).then((response) => {
    boardStore.getBoardList(response.data);
  });
});

const getBoardDetail = (boardId) => {
  token.value = sessionStorage.getItem("accessToken");
  router.push({ name: "boardDetail", params: { boardId } });
};
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
.board-content-content > p {
}
</style>
