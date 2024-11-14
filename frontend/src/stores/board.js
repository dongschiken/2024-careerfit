import { ref, computed } from "vue";
import { defineStore } from "pinia";
import axios from "axios";
const REST_API_URL = `http://localhost:8080/api/board`;
export const useBoardStore = defineStore(
  "board",
  () => {
    const boardList = ref([]);
    const boardSearch = ref({});
    const pageResult = ref({
      page: 1,
      lastPage: 1,
      beginPage: 1,
      endPage: 1,
      prev: false,
      next: false,
    });
    const getBoardList = (data) => {
      boardList.value = data.boards;
      boardSearch.value = data.boardSearch;
      pageResult.value = data.pageResult;
    };

    const getBoardCategoryPage = async (boardCategoryId, searchWord, page) => {
      try {
        boardSearch.value.boardCategoryId = boardCategoryId;
        const params = {
          searchWord: searchWord || boardSearch.value.searchWord,
          page: page || 1,
          categoryId: boardCategoryId,
        };
        const response = await axios.get(REST_API_URL, { params });
        console.log(params);
        boardList.value = response.data.boards;
        boardSearch.value = {
          ...boardSearch.value,
          ...response.data.boardSearch,
        };
        pageResult.value = response.data.pageResult;
      } catch (error) {
        console.error("게시글 로드 실패:", error);
      }
    };

    return {
      boardList,
      getBoardList,
      REST_API_URL,
      getBoardCategoryPage,
      boardSearch,
      pageResult,
    };
  },
  {
    persist: true,
  }
);
