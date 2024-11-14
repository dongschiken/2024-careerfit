<template>
  <div class="pagination">
    <a href="#" class="first">First</a>
    <span>...</span>
    <div class="next-prev-button">
      <button
        class="prev"
        v-if="pageResult.prev"
        @click="changePage(pageResult.page - 1)"
      >
        <img
          class="prev-btn"
          src="@/assets/img/arrow_back_ios_24dp_5F6368_FILL0_wght400_GRAD0_opsz24.png"
          alt=""
        />
      </button>
    </div>
    <div class="page-list-group">
      <div v-for="page in pages" :key="page" class="page-number">
        <a
          :class="{ active: page === pageResult.page }"
          @click="changePage(page)"
        >
          {{ page }}
        </a>
      </div>
    </div>
    <div class="next-prev-button">
      <button v-if="pageResult.next" @click="changePage(pageResult.page + 1)">
        <img
          class="next-btn"
          src="@/assets/img/arrow_forward_ios_24dp_5F6368_FILL0_wght400_GRAD0_opsz24.png"
        />
      </button>
    </div>
    <span>...</span>
    <a href="#" class="last">Last</a>
  </div>
</template>

<script setup>
import { computed } from "vue";
const props = defineProps({
  pageResult: {
    type: Object,
    required: true,
  },
});

// 페이지 목록 계산
const pages = computed(() => {
  const pageNumbers = [];
  for (let i = props.pageResult.beginPage; i <= props.pageResult.endPage; i++) {
    pageNumbers.push(i);
  }
  return pageNumbers;
});

const changePage = (page) => {
  console.log("Changing to page:", page);
};
</script>

<style lang="css" scoped>
@import url(@/assets/css/board-page.css);
.page-list-group {
  display: flex;
  align-items: center;
}
.page-number:hover {
  cursor: pointer;
}
.next-prev-button:hover {
  cursor: pointer;
}
.next-prev-button > button {
  border: 0;
  background-color: transparent;
}
.next-btn {
  width: 17px;
}
.prev-btn {
  width: 17px;
}
</style>
