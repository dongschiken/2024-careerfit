<template>
  <HeaderView />
  <div class="board-content-layout">
    <header class="board-header-group">
      <div class="board-content-header">
        <div class="board-content-header-text">
          <router-link to="/board">커뮤니티</router-link>
          <p>다양한 사람들과 이야기를 나눠 보세요</p>
        </div>
      </div>
      <div class="board-content-header-menu">
        <div class="board-content-header-menu-write">
          <router-link to="/write">
            <button id="write-btn">
              <div class="wirte-btn-group">
                <img
                  src="../../assets/img/edit_square_24dp_5F6368_FILL0_wght400_GRAD0_opsz24.png"
                  alt=""
                />
                글작성
              </div>
            </button>
          </router-link>
        </div>
        <div class="board-content-header-menu-category">
          <div><button>스터디 & 모임</button></div>
          <div><button>헬스이야기</button></div>
          <div><button>회사생활</button></div>
        </div>
        <div class="board-content-header-menu-search">
          <div class="search-group">
            <input
              class="search-input"
              type="text"
              placeholder="검색"
              style="border: 0px"
            />
            <img
              src="../../assets/img/search_24dp_5F6368_FILL0_wght400_GRAD0_opsz24.png"
              alt=""
            />
          </div>
        </div>
        <div class="board-content-header-menu-sort">
          <div>
            <img
              src="../../assets/img/swap_vert_24dp_5F6368_FILL0_wght400_GRAD0_opsz24.png"
              alt=""
            />
          </div>
          <div class="custom-select">
            <select>
              <option value="option1">최신순</option>
              <option value="option2">과거순</option>
              <option value="option3">인기순</option>
              <option value="option4">댓글순</option>
              <option value="option4">조회순</option>
            </select>
          </div>
        </div>
      </div>
    </header>
    <main class="board-main-group">
      <BoardCategory />
    </main>
    <footer class="board-footer-group">
      <BoardPage />
    </footer>
  </div>
  <FooterView />
</template>

<script>
import HeaderView from "@/components/module/MainHeader.vue";
import FooterView from "@/components/module/MainFooter.vue";
import BoardCategory from "@/components/board/BoardCategory.vue";
import axios from "axios";
import BoardPage from "@/components/board/BoardPage.vue";

export default {
  name: "BoardListView",
  data() {
    return {
      boardList: [],
    };
  },
  created() {
    this.fetchBoardList();
  },
  methods: {
    async fetchBoardList() {
      this.loading = true;
      try {
        const response = await axios.get("https://api.example.com/boards");
        this.boardList = response.data;
      } catch (error) {
        this.errorMessage = "데이터를 불러오는 중 오류가 발생했습니다.";
        console.error(error);
      } finally {
        this.loading = false;
      }
    },
  },
  components: {
    HeaderView,
    FooterView,
    BoardCategory,
    BoardPage,
  },
};
</script>

<style lang="css">
.board-content-layout {
  max-width: 100%;
  max-height: 100%;
  min-width: 1200px;
  min-height: 1200px;
  margin: 0 auto; /* 좌우 마진을 auto로 설정하여 중앙 정렬 */
  padding: 0 20px; /* 선택적: 양쪽에 약간의 패딩 추가 */
}

.board-content-header {
  margin-top: 65px;
  background-color: #d7e3e1; /* 기본 배경색 */
  border-radius: 15px; /* 둥근 모서리 */
  max-width: 1000px;
  min-width: 1000px;
  height: 120px; /* 박스 높이 */
  display: flex;
  align-items: center; /* 세로 중앙 정렬 */
  justify-content: center; /* 가로 중앙 정렬 */
  background-image: linear-gradient(45deg, #c0d0ce 25%, transparent 25%),
    linear-gradient(-45deg, #c0d0ce 25%, transparent 25%),
    linear-gradient(45deg, transparent 75%, #c0d0ce 75%),
    linear-gradient(-45deg, transparent 75%, #c0d0ce 75%);
  background-size: 120px 120px;
  background-position: 0 0, 0 60px, 60px -60px, -60px 0px;
  display: flex;
  justify-content: start;
}
.board-content-header-text {
  font-size: 24px;
  font-weight: bold;
  color: #000000;
  margin-left: 90px;
}
.board-header-group {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.board-content-header-text > a {
  display: flex;
  text-decoration: none;
  color: black;
}
.board-content-header-text > p {
  margin-top: 10px;
  font-size: 16px;
  font-weight: 500;
}
.board-content-header-menu {
  display: flex;
  justify-content: space-between;
  max-width: 1000px;
  min-width: 1000px;
  margin-top: 30px;
}
#write-btn {
  width: 110px;
  height: 50px;
  background-color: #ff7f32;
  color: white;
  border: none;
  border-radius: 25px;
  font-weight: bold;
  cursor: pointer;
  box-shadow: 0px 2px 5px rgba(0, 0, 0, 0.1);
}
#write-btn > div > a {
  text-decoration: none;
  color: #ffffff;
}
#write-btn > div > img {
  filter: brightness(0) invert(1);
  width: 20px;
  height: 20px;
  margin-right: 8px;
}
.wirte-btn-group {
  display: flex;
  justify-content: center;
  align-items: center;
}
.board-content-header-menu-category {
  display: flex;
  justify-content: center;
  align-items: center;
}
.board-content-header-menu-category button {
  width: 110px;
  height: 50px;
  background-color: #ffffff;
  color: #000;
  border: none;
  border-radius: 25px;
  font-weight: bold;
  cursor: pointer;
  box-shadow: 0px 2px 5px rgba(0, 0, 0, 0.1);
  margin-right: 10px;
}
.search-group {
  border: none;
  border-radius: 25px;
  background-color: #ffffff;
  box-shadow: 0px 2px 5px rgba(0, 0, 0, 0.1);
  min-height: 50px;
  max-height: 50px;
}
.search-group > img {
  filter: brightness(0);
  width: 25px;
  height: 25px;
}
.search-input {
  width: 120px;
  height: 30px;
  margin-left: 10px;
  font-size: 14px;
}
.search-group {
  display: flex;
  align-items: center;
  justify-content: space-evenly;
  min-width: 170px;
  max-width: 170px;
}
.search-group > img:hover {
  cursor: pointer;
}
.board-header-group-menu-search {
  min-width: 170px;
  max-width: 170px;
}
.board-content-header-menu-sort {
  display: flex;
  align-items: center;
}
.board-content-header-menu-sort > div > img {
  width: 28px;
  height: 28px;
}
.board-main-group {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  flex-wrap: nowrap;
  width: 100%;
  margin-top: 30px;
  overflow: hidden; /* 자식 요소가 부모를 넘어가지 않도록 설정 */
}
.board-footer-group {
  display: flex;
  justify-content: center;
  margin-top: 60px;
  margin-bottom: 80px;
}
.search-input:focus {
  outline: none;
}

.custom-select {
  position: relative;
  display: inline-block;
  width: 100px;
}

.custom-select select {
  width: 100px;
  height: 50px;
  padding: 10px;
  font-size: 14px;
  border-radius: 5px;
  border: none;
  /* border: 1px solid #d2d0d0; */
  background-color: #ffffff;
  box-shadow: 0px 2px 5px rgba(0, 0, 0, 0.1);
  color: #333;
  appearance: none;
  -webkit-appearance: none;
  -moz-appearance: none;
  cursor: pointer;
}

.custom-select::after {
  content: "▼";
  font-size: 14px;
  color: #4caf50;
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  pointer-events: none;
}

.custom-select select:hover {
  border-color: #388e3c;
}

.custom-select select:focus {
  outline: none;
}
.board-content-header-menu-category > div > button {
  font-size: 14px;
}
#write-btn > div > a {
  font-size: 14px;
}
</style>
