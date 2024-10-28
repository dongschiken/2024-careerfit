<template>
  <HeaderView />
  <div class="board-content-layout">
    <header class="board-header-group">
      상단 헤더 + 글작성 + 카테고리 + 검색 + 정렬
    </header>
    <main class="board-main-group">게시글 10개</main>
    <footer class="board-footer-group">페이징 처리</footer>
  </div>

  <FooterView />
</template>

<script>
import HeaderView from "@/components/module/HeaderView.vue";
import FooterView from "@/components/module/FooterView.vue";
import axios from "axios";

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
  },
};
</script>

<style lang="css">
.board-content-layout {
  max-width: 1200px;
  max-height: 1200px;
  min-width: 1200px;
  min-height: 1200px;
  margin: 0 auto; /* 좌우 마진을 auto로 설정하여 중앙 정렬 */
  padding: 0 20px; /* 선택적: 양쪽에 약간의 패딩 추가 */
}
</style>
