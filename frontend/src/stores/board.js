import { ref, computed } from "vue";
import { defineStore } from "pinia";
import axios from "axios";
const REST_API_URL = `http://localhost:8080/api/board`;
export const useBoardStore = defineStore("board", () => {
  const boardList = ref([]); // 게시글 목록을 스토에서 관리
  const getBoardList = () => {
    axios.get(REST_API_URL).then((response) => {
      boardList.value = response.data;
      console.log(boardList.value);
    });
  };
  return { boardList, getBoardList };
});
