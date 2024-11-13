<template>
  <div>
    <div
      v-for="board in store.boardList"
      :key="board.boardId"
      class="board-content-list-group"
    >
      <div class="board-content-left">
        <div class="board-content-title">{{ board.title }}</div>
        <div class="board-content-content">
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
        <div class="board-content-right-img">
          <img
            v-if="mainImage(board)"
            :src="`http://localhost:8080/uploads/${mainImage(board).path}${
              mainImage(board).systemName
            }`"
            alt="Board Image"
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
import { useBoardStore } from "@/stores/board";
import { onMounted } from "vue";
const store = useBoardStore();

function mainImage(board) {
  return board.boardImgs.find((img) => img.mainWhether === "M");
}
</script>

<style lang="css" scoped>
@import url(@/assets/css/board-category.css);
</style>
