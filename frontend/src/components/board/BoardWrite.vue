<template>
  <div>
    <HeaderView />
    <div class="container">
      <div><h2>함께라서 더 가치 있는 시간</h2></div>
      <div>
        <p>
          <b>user님</b>, careerfit에서 다양한 사람들과 소통하며 새로운 경험을
          얻어가세요!
        </p>
      </div>
      <div class="form-container">
        <div class="form-group">
          <label for="category">카테고리</label>
          <select required v-model="category" id="category" class="form-input">
            <option disabled selected>카테고리를 선택해주세요.</option>
            <option value="1">회사생활</option>
            <option value="2">헬스이야기</option>
            <option value="3">식단관리</option>
          </select>
        </div>

        <div class="form-group">
          <label for="title">제목</label>
          <input
            required
            id="title"
            type="text"
            class="form-input"
            placeholder="제목을 입력해주세요."
            v-model="title"
          />
        </div>
      </div>
      <label class="content-label" for="">내용</label>
      <textarea
        required
        type="text"
        class="editor"
        placeholder="내용을 입력하세요"
        v-model="content"
      />

      <div class="buttons">
        <input
          type="file"
          accept="image/*"
          @change="addImage"
          ref="imageInput"
          style="display: none"
        />
        <button @click="triggerImageUpload">이미지 추가</button>
      </div>

      <!-- 이미지 미리보기 -->
      <div class="image-preview" v-if="imagePreviews.length">
        <div
          v-for="(preview, index) in imagePreviews"
          :key="index"
          class="preview-item"
        >
          <img :src="preview" alt="미리보기 이미지" />
          <span class="delete-x" @click="removeImage(index)">×</span>
        </div>
      </div>

      <div class="button-container">
        <button class="cancel-button">취소</button>
        <button
          v-if="boardId != null && !isNaN(boardId)"
          @click="setBoard()"
          class="submit-button"
        >
          수정
        </button>
        <button
          v-if="boardId == null || isNaN(boardId)"
          @click="registBoard()"
          class="submit-button"
        >
          등록
        </button>
      </div>
    </div>
    <FooterView />
  </div>
</template>

<script setup>
import { ref, onMounted, defineProps } from "vue";
import { useBoardStore } from "@/stores/board";
import { useRoute, useRouter } from "vue-router";
import api from "@/api/axiosInstance";
import HeaderView from "@/components/module/MainHeader.vue";
import FooterView from "@/components/module/MainFooter.vue";
const route = useRoute();
const router = useRouter();
import axios from "axios";
const boardStore = useBoardStore();
const title = ref("");
const content = ref("");
const category = ref("");
const imageFiles = ref([]);
const imagePreviews = ref([]);
const imageInput = ref(null);
const isLoading = ref(true);
const token = ref(sessionStorage.getItem("accessToken"));
const board = ref({
  boardImgs: [],
});

defineProps({
  boardId: {
    type: Number,
    required: true,
  },
});

const boardId = ref(Number(route.params.boardId)); // 명시적 변환
const addImage = (event) => {
  console.log(event);
  const files = Array.from(event.target.files);
  files.forEach((file) => {
    const reader = new FileReader();
    if (file) {
      reader.onload = (e) => {
        imagePreviews.value.push(e.target.result);
      };
      reader.readAsDataURL(file);
      imageFiles.value.push(file);
    }
  });
  event.target.value = null; // 동일 파일 재선택 허용
};

const triggerImageUpload = () => {
  if (imageInput.value) {
    imageInput.value.click();
  }
};
const removeImage = (index) => {
  imagePreviews.value.splice(index, 1);
  imageFiles.value.splice(index, 1);
};

const setBoard = async () => {
  const check = confirm("정말 수정하시겠습니까?");
  if (!check) return;
  try {
    const formData = new FormData();
    const board = {
      boardId: boardId.value,
      title: title.value,
      content: content.value,
      categoryId: category.value,
    };
    formData.append(
      "board",
      new Blob([JSON.stringify(board)], { type: "application/json" })
    );
    imageFiles.value.forEach((file) => {
      formData.append("files", file);
    });
    const response = await api.put("/api/board", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
    if (response.status == 201) {
      alert("수정이 완료되었습니다.");
      router.replace({
        name: "board",
        params: {
          boardId: boardId.value,
        },
      });
    }
  } catch (error) {
    console.log(error);
    alert("게시물 수정 중 오류발생");
  }
};

const registBoard = async () => {
  try {
    const formData = new FormData();
    const board = {
      title: title.value,
      content: content.value,
      categoryId: category.value,
    };
    formData.append(
      "board",
      new Blob([JSON.stringify(board)], { type: "application/json" })
    );
    imageFiles.value.forEach((file) => {
      formData.append("files", file);
    });
    console.log(`Bearer ${token.value}`); // 값 출력 확인
    const response = await api.post("/api/board", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
    if (response.status == 201) {
      alert("등록이 완료되었습니다.");
      router.replace({
        name: "board",
      });
    }
  } catch (error) {
    console.log(error);
    alert("게시글 등록에 실패했습니다.");
  }
};
const getBoard = async (boardId) => {
  try {
    const response = await api.get(`/api/board/${boardId}`);
    board.value = response.data.board;
    content.value = board.value.content;
    title.value = board.value.title;
    category.value = board.value.category.boardCategoryId;
    processBoardImages();
  } catch (error) {
    console.log(error);
    alert("게시글 데이터를 가져오는 중 문제가 발생했습니다.");
  } finally {
    isLoading.value = false;
  }
};

const processBoardImages = () => {
  if (board.value.boardImgs) {
    for (const img of board.value.boardImgs) {
      const fileUrl = `http://localhost:8080/uploads/${img.path}${img.systemName}`;
      fetch(fileUrl)
        .then((res) => res.blob()) // 이미지 데이터를 Blob으로 변환
        .then((blob) => {
          const file = new File([blob], img.originName, { type: blob.type });

          const reader = new FileReader();
          reader.onload = (e) => {
            imagePreviews.value.push(e.target.result);
          };
          reader.readAsDataURL(file);
          imageFiles.value.push(file);
        })
        .catch((err) => {
          console.error("이미지 처리 중 오류 발생:", err);
        });
    }
  }
};

onMounted(() => {
  if (!boardId.value) return;
  getBoard(boardId.value);
});
</script>

<style scoped>
@import url(@/assets/css/board-write.css);
</style>
