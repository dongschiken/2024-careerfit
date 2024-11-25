<template>
  <div>
    <div class="comunity-group">
      <div class="comunity-header">
        <div class="comunity-header-text">
          <router-link to="/board"
            >실시간 인기글 TOP 5<img
              class="fire"
              src="@/assets/img/fire-icon.png"
          /></router-link>
        </div>
      </div>
      <div
        v-for="board in boardViewRank"
        :key="board.boardId"
        class="main-board-content-group"
      >
        <div class="main-board-content-layout">
          <div class="main-board-content-left">
            <div class="profile-img">
              <div
                class="author-avatar"
                :class="{ 'gradient-animation': !profileUrl }"
                :style="
                  profileUrl ? { backgroundImage: `url(${profileUrl})` } : null
                "
              ></div>
            </div>
            <div class="nickname">{{ board.user.nickname }}</div>
            <div class="material-icon">
              <img
                src="@/assets/img/edit_square_24dp_5F6368_FILL0_wght400_GRAD0_opsz24.png"
                alt=""
              />
            </div>
            <div class="date-time">{{ board.timeAgo }}</div>
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
              <img src="@/assets/img/view.png" alt="" />
              <div>{{ board.viewCount }}</div>
            </div>
          </div>
        </div>
        <RouterLink :to="`/board/detail/${board.boardId}`">
          <div class="contents">{{ board.title }}</div>
        </RouterLink>
      </div>
    </div>
  </div>
</template>

<script setup>
import ncapi from "@/api/noTokenAxiosInstance";
import { onMounted } from "vue";
import { ref } from "vue";
const boardViewRank = ref({});

// 이미지 배경 스타일
function getBackgroundImage(profileUrl) {
  return profileUrl
    ? { backgroundImage: `url(${profileUrl})` }
    : { background: "linear-gradient(135deg, #FF7F50, #FFB6C1, #87CEFA)" };
}

const getBoard = async () => {
  const response = await ncapi.get("/api/board/view-rank");
  boardViewRank.value = response.data;
  console.log(response);
  console.log(boardViewRank.value);
};
onMounted(() => {
  getBoard();
});
</script>

<style scoped>
.fire {
  margin-left: 10px;
  min-width: 25px;
  max-width: 25px;
}
.main-board-group {
  margin-top: 400px;
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

.comunity-header {
  display: flex;
  align-items: center; /* 세로 중앙 정렬 */
  justify-content: center; /* 가로 중앙 정렬 */
  background-color: #d7e3e1;
  border-radius: 15px;
  width: 390px;
  height: 90px;
  background-image: linear-gradient(45deg, #c0d0ce 25%, transparent 25%),
    linear-gradient(-45deg, #c0d0ce 25%, transparent 25%),
    linear-gradient(45deg, transparent 75%, #c0d0ce 75%),
    linear-gradient(-45deg, transparent 75%, #c0d0ce 75%);
  background-size: 60px 60px; /* 체크 무늬 크기 설정 (더 크게) */
  background-position: 0 0, 0 30px, 30px -30px, -30px 0px; /* 패턴의 위치 조정 */
}
.comunity-header-text > a {
  display: flex; /* 내부 요소를 정렬하려면 flex 사용 */
  align-items: center; /* 세로 정렬 */
  justify-content: center; /* 가로 정렬 */
  width: 100%; /* 부모 크기에 맞춤 */
  text-align: center; /* 텍스트 가운데 정렬 */
  text-decoration: none;
  color: black;
  font-size: 20px;
  font-weight: bold;
}

.main-board-group {
  display: flex;
  justify-content: space-evenly;
}
.profile-img > img {
  width: 23px;
  height: 23px;
  border: 1px solid #d4d3d3;
  border-radius: 100%;
}
.profile-img {
  margin-left: 0;
}

div.main-container
  > div
  > div.main-board-group
  > div:nth-child(1)
  > div.main-board-content-group
  > div.main-board-content-layout
  > div.main-board-content-left
  > div.profile-img {
  margin-left: 0;
}

.main-board-content-left {
  display: flex;
}
.main-board-content-group {
  margin-top: 30px;
}
.main-board-content-left div {
  margin-left: 6px;
}
.main-board-content-left div {
  margin-left: 6px;
}
.main-board-content-left > .date-time {
  margin-left: 20px;
}
.main-board-content-group {
  border-bottom: 1.5px solid rgb(192, 191, 191);
}
.material-icon > img {
  width: 23px;
  height: 23px;
}
.main-board-content-group {
  display: flex;
  flex-direction: column;
}
.main-board-content-right {
  display: flex;
}
.main-board-content-right > .material-icon {
  display: flex;
}
.main-board-content-right > .material-icon > div {
  padding-top: 2px;
  margin-left: 6px;
}

div.main-container
  > div
  > div.main-board-group
  > div:nth-child(1)
  > div.main-board-content-group
  > div.main-board-content-right
  > div:nth-child(2) {
  margin-left: 13px;
}
.main-board-content-layout {
  display: flex;
  justify-content: space-between;
}

.main-board-content-right > .material-icon:nth-child(1) {
  margin-right: 15px;
}
.contents {
  padding-bottom: 20px;
  padding-top: 30px;
  text-align: start;
  text-decoration: none;
  color: black;
}
.comunity-group {
  border: 1px solid #ddd; /* 가벼운 테두리 */
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2); /* 약간의 그림자 */
  border-radius: 15px; /* 둥근 모서리 */
  padding: 25px;
}
a {
  text-decoration: none;
}
</style>
