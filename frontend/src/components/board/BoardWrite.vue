<template>
  <div>
    <HeaderView />
    <div class="container">
      <h2>게시글 작성</h2>
      <div
        ref="editor"
        class="editor"
        contenteditable="true"
        placeholder="내용을 입력하거나 이미지를 추가하세요"
      ></div>

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

      <button class="submit-btn" @click="submitPost">게시글 등록</button>
    </div>
    <FooterView />
  </div>
</template>

<script setup>
import { ref } from "vue";
import HeaderView from "@/components/module/MainHeader.vue";
import FooterView from "@/components/module/MainFooter.vue";
const imageInput = ref(null);
const editor = ref(null);
// 이미지 업로드 트리거 함수
const triggerImageUpload = () => {
  imageInput.value.click();
};

// 이미지 추가 함수
const addImage = (event) => {
  const file = event.target.files[0];
  if (file) {
    const reader = new FileReader();
    reader.onload = (e) => {
      const img = document.createElement("img");
      img.src = e.target.result;
      editor.value.appendChild(img);
      editor.value.appendChild(document.createElement("br"));
    };
    reader.readAsDataURL(file);
  }
};

// 게시글 제출 함수
const submitPost = () => {
  const content = editor.value.innerHTML;
  alert("게시글 내용:\n" + content);
  // 서버에 전송하는 기능을 추가할 수 있습니다.
};
</script>

<style scoped>
@import url(@/assets/css/board-write.css);
</style>
