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
                  src="@/assets/img/edit_square_24dp_5F6368_FILL0_wght400_GRAD0_opsz24.png"
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
              src="@/assets/img/search_24dp_5F6368_FILL0_wght400_GRAD0_opsz24.png"
              alt=""
            />
          </div>
        </div>
        <div class="board-content-header-menu-sort">
          <div>
            <img
              src="@/assets/img/swap_vert_24dp_5F6368_FILL0_wght400_GRAD0_opsz24.png"
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
@import url(@/assets/css/board-content.css);
</style>
