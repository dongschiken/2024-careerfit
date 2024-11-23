<template>
  <div class="modal-inner">
    <div class="modal-header">
      <h2>프로필 이미지 변경</h2>
      <button @click="$emit('close')" class="close-button">
        <i class="fas fa-times"></i>
      </button>
    </div>

    <div class="modal-body">
      <div class="preview-container">
        <img
          :src="previewUrl || userStore.profileUrl || '/default-profile.png'"
          alt="Profile Preview"
          class="preview-image"
        />
      </div>

      <div class="file-input-container">
        <input
          type="file"
          ref="fileInput"
          @change="handleFileChange"
          accept="image/*"
          class="file-input"
        />
        <p class="help-text">* JPG, PNG 파일만 업로드 가능합니다. (최대 5MB)</p>
      </div>

      <div class="button-group">
        <button type="button" @click="$emit('close')" class="cancel-button">
          취소
        </button>
        <button
          @click="handleUpload"
          :disabled="!selectedFile"
          class="submit-button"
        >
          변경하기
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useUserStore } from "@/stores/userStore";

const userStore = useUserStore();
const emit = defineEmits(["close", "update"]);

const selectedFile = ref(null);
const previewUrl = ref(null);
const fileInput = ref(null);

const handleFileChange = (event) => {
  const file = event.target.files[0];
  if (file) {
    // 파일 크기 체크 (5MB)
    if (file.size > 5 * 1024 * 1024) {
      alert("파일 크기는 5MB를 초과할 수 없습니다.");
      fileInput.value.value = "";
      return;
    }

    // 파일 형식 체크
    if (!file.type.match("image.*")) {
      alert("이미지 파일만 업로드 가능합니다.");
      fileInput.value.value = "";
      return;
    }

    selectedFile.value = file;

    // 이미지 미리보기 생성
    const reader = new FileReader();
    reader.onload = (e) => {
      previewUrl.value = e.target.result;
    };
    reader.readAsDataURL(file);
  }
};

const handleUpload = async () => {
  if (!selectedFile.value) return;

  try {
    await userStore.updateProfilePicture(selectedFile.value);
    emit("update");
    emit("close");
  } catch (error) {
    console.error("프로필 이미지 업로드 실패:", error);
    alert("이미지 업로드 중 오류가 발생했습니다.");
  }
};
</script>

<style scoped>
.modal-inner {
  padding: 25px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.modal-header h2 {
  font-size: 1.5rem;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.close-button {
  background: none;
  border: none;
  font-size: 1.25rem;
  cursor: pointer;
  color: #666;
  transition: color 0.2s;
}

.close-button:hover {
  color: #333;
}

.preview-container {
  width: 200px;
  height: 200px;
  margin: 0 auto 20px;
  border-radius: 50%;
  overflow: hidden;
  border: 3px solid #ff7d29;
  box-shadow: 0 4px 12px rgba(255, 125, 41, 0.15);
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.file-input-container {
  margin-bottom: 20px;
}

.file-input {
  width: 100%;
  padding: 10px;
  margin-bottom: 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
  background-color: white;
}

.help-text {
  font-size: 0.875rem;
  color: #666;
  margin-top: 8px;
}

.button-group {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 30px;
}

.cancel-button,
.submit-button {
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.95rem;
  transition: all 0.2s;
}

.cancel-button {
  background: white;
  border: 1px solid #ddd;
  color: #666;
}

.cancel-button:hover {
  background: #f8f9fa;
}

.submit-button {
  background: #ff7d29;
  color: white;
  border: none;
}

.submit-button:hover {
  background: #ff6b10;
}

.submit-button:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.file-input::file-selector-button {
  padding: 8px 16px;
  margin-right: 16px;
  border-radius: 4px;
  border: none;
  background: #ff7d29;
  color: white;
  cursor: pointer;
  transition: background-color 0.2s;
}

.file-input::file-selector-button:hover {
  background: #ff6b10;
}
</style>
