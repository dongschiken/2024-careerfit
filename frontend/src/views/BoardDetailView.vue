<template>
  <div>
    <h1>게시글 상세 보기</h1>
    <div v-if="boardDetail">
      <h2>{{ boardDetail.title }}</h2>
      <p>{{ boardDetail.content }}</p>
      <!-- 게시글의 다른 세부 정보 -->
    </div>
  </div>
</template>

<script setup>
import { useBoardStore } from "@/stores/board";
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import axios from "axios";
const BoardStore = useBoardStore();
const route = useRoute();
const boardDetail = ref(null);
console.log(route);
const fetchBoardDetail = async () => {
  const boardId = route.params.boardId;
  try {
    const response = await axios.get(BoardStore.REST_API_URL + `/${boardId}`);
    boardDetail.value = response.data;
  } catch (error) {
    console.error("게시글 상세 정보 로드 실패:", error);
  }
};

// 컴포넌트 마운트 시 상세 정보 요청
onMounted(() => {
  fetchBoardDetail();
});
</script>
