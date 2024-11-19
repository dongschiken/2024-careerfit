<template>
  <div>
    <MainHeader />
    <BoardDetailContent />
    <BoardReply :boardId="boardId" />
    <MainFooter />
  </div>
</template>

<script setup>
import { useBoardStore } from "@/stores/board";
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import BoardReply from "@/components/reply/BoardReply.vue";
import BoardDetailContent from "@/components/board/BoardDetailContent.vue";
import MainHeader from "@/components/module/MainHeader.vue";
import MainFooter from "@/components/module/MainFooter.vue";
import axios from "axios";
const BoardStore = useBoardStore();
const route = useRoute();
const boardDetail = ref(null);
const boardId = ref();
console.log(route);
const fetchBoardDetail = async () => {
  boardId.value = route.params.boardId;
  try {
    const response = await axios.get(
      BoardStore.REST_API_URL + `/${boardId.value}`
    );
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
