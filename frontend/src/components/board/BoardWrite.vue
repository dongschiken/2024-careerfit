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

<script>
import HeaderView from "@/components/module/MainHeader.vue";
import FooterView from "@/components/module/MainFooter.vue";
export default {
  methods: {
    triggerImageUpload() {
      this.$refs.imageInput.click();
    },
    addImage(event) {
      const file = event.target.files[0];
      if (file) {
        const reader = new FileReader();
        reader.onload = (e) => {
          const img = document.createElement("img");
          img.src = e.target.result;
          const editor = this.$refs.editor;
          editor.appendChild(img);
          editor.appendChild(document.createElement("br"));
        };
        reader.readAsDataURL(file);
      }
    },
    submitPost() {
      const content = this.$refs.editor.innerHTML;
      alert("게시글 내용:\n" + content);
      // 서버에 전송하는 기능을 추가할 수 있습니다.
    },
  },
  components: {
    HeaderView,
    FooterView,
  },
};
</script>

<style>
body {
  font-family: Arial, sans-serif;
  margin: 20px;
}

.container {
  max-width: 600px;
  margin: 0 auto;
}

.editor {
  width: 100%;
  min-height: 300px;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
  background-color: #f9f9f9;
  overflow-y: auto;
}

.editor img {
  max-width: 100%;
  margin-top: 10px;
}

.buttons {
  display: flex;
  gap: 10px;
  margin: 15px 0;
}

.buttons button {
  flex: 1;
  padding: 10px;
  font-size: 16px;
  cursor: pointer;
  border: none;
  background-color: #007bff;
  color: white;
  border-radius: 5px;
}

.buttons button:hover {
  background-color: #0056b3;
}

.submit-btn {
  width: 100%;
  padding: 10px;
  font-size: 18px;
  cursor: pointer;
  border: none;
  background-color: #28a745;
  color: white;
  border-radius: 5px;
}

.submit-btn:hover {
  background-color: #218838;
}
</style>
