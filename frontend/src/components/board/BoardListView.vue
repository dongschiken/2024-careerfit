<template>
  <HeaderView />
  <h1>게시글 목록</h1>
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
