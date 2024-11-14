<template>
  <div class="board-content-layout">
    <HeaderView />
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
        <div
          class="board-content-header-menu-category"
          v-if="boardCategories && boardCategories.length > 0"
        >
          <div v-for="(category, index) in boardCategories" :key="index">
            <button
              :class="{
                'select-category-btn':
                  boardStore.boardSearch &&
                  boardStore.boardSearch?.boardCategoryId ===
                    category.boardCategoryId,
              }"
              @click="getBoardCategoryPage(category.boardCategoryId)"
            >
              {{ category.name }}
            </button>
          </div>
        </div>

        <div class="board-content-header-menu-search">
          <div class="search-group">
            <input
              @keydown.enter="
                getBoardCategoryPage(boardStore.boardSearch.boardCategoryId)
              "
              v-model="search.searchWord"
              class="search-input"
              type="text"
              placeholder="검색"
              style="border: 0px"
            />
            <button
              @click="
                getBoardCategoryPage(boardStore.boardSearch.boardCategoryId)
              "
              style="border: none; background: none; padding: 0"
            >
              <img
                src="@/assets/img/search_24dp_5F6368_FILL0_wght400_GRAD0_opsz24.png"
                alt="Search"
              />
            </button>
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
      <BoardPage :pageResult="boardStore.pageResult" />
    </footer>
    <FooterView />
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { useBoardStore } from "@/stores/board";
import HeaderView from "@/components/module/MainHeader.vue";
import FooterView from "@/components/module/MainFooter.vue";
import BoardCategory from "@/components/board/BoardCategory.vue";
import axios from "axios";
import BoardPage from "@/components/board/BoardPage.vue";

const search = ref({
  searchWord: "",
  page: 1,
});

const boardStore = useBoardStore();
const boardCategories = ref([]);
onMounted(() => {
  axios.get(boardStore.REST_API_URL + "/category").then((response) => {
    boardCategories.value = response.data;
  });
});

const getBoardCategoryPage = (boardCategoryId) => {
  boardStore.getBoardCategoryPage(boardCategoryId, search.value.searchWord, 1);
};
</script>

<style lang="css" scoped>
@import url(@/assets/css/board-content.css);
.select-category-btn {
  background-color: #ff7f32 !important;
  color: #ffffff !important;
}
.search-group > button > img {
  width: 26px;
  height: 26px;
}
.search-group > button > img:hover {
  cursor: pointer;
}
</style>
