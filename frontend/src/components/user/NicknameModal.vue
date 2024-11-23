<template>
  <div class="modal-inner">
    <div class="modal-header">
      <h2>닉네임 변경</h2>
      <button @click="$emit('close')" class="close-button">
        <i class="fas fa-times"></i>
      </button>
    </div>

    <div class="modal-body">
      <form @submit.prevent="handleSubmit">
        <div class="form-group">
          <label>새로운 닉네임</label>
          <div class="nickname-input">
            <input
              v-model="nickname"
              type="text"
              placeholder="새로운 닉네임을 입력해주세요"
              :class="{ error: !isNicknameValid && nickname }"
            />
            <button
              type="button"
              @click="checkNickname"
              :disabled="!nickname || nickname === originalNickname"
              class="check-button"
            >
              중복확인
            </button>
          </div>
          <p v-if="nicknameChecked && isAvailable" class="success-message">
            사용 가능한 닉네임입니다.
          </p>
          <p v-if="nicknameChecked && !isAvailable" class="error-message">
            이미 사용중인 닉네임입니다.
          </p>
        </div>

        <div class="button-group">
          <button type="button" @click="$emit('close')" class="cancel-button">
            취소
          </button>
          <button type="submit" :disabled="!isValid" class="submit-button">
            변경하기
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import { useUserStore } from "@/stores/userStore";

const userStore = useUserStore();
const emit = defineEmits(["close", "update"]);

const nickname = ref("");
const originalNickname = ref(userStore.nickname);
const nicknameChecked = ref(false);
const isAvailable = ref(false);

const isValid = computed(() => {
  return (
    nickname.value &&
    (nickname.value === originalNickname.value ||
      (nicknameChecked.value && isAvailable.value))
  );
});

const checkNickname = async () => {
  if (!nickname.value) return;
  if (nickname.value === originalNickname.value) {
    nicknameChecked.value = true;
    isAvailable.value = true;
    return;
  }

  try {
    const available = await userStore.checkNickname(nickname.value);
    nicknameChecked.value = true;
    isAvailable.value = available;
  } catch (error) {
    console.error("닉네임 중복 확인 실패:", error);
    alert("닉네임 중복 확인 중 오류가 발생했습니다.");
  }
};

const handleSubmit = async () => {
  if (!isValid.value) return;

  try {
    await userStore.updateUser({ nickname: nickname.value });
    emit("update");
    emit("close");
  } catch (error) {
    console.error("닉네임 변경 실패:", error);
    alert("닉네임 변경 중 오류가 발생했습니다.");
  }
};
</script>

<style scoped>
.modal-inner {
  padding: 20px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.modal-header h2 {
  font-size: 1.5rem;
  font-weight: 600;
  margin: 0;
}

.close-button {
  background: none;
  border: none;
  font-size: 1.25rem;
  cursor: pointer;
  padding: 5px;
  color: #666;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
}

.nickname-input {
  display: flex;
  gap: 10px;
}

.nickname-input input {
  flex: 1;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 1rem;
}

.nickname-input input.error {
  border-color: #ff4d4d;
}

.check-button {
  padding: 10px 15px;
  background: #ff7d29;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  white-space: nowrap;
}

.check-button:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.success-message {
  color: #2ecc71;
  font-size: 0.875rem;
  margin-top: 5px;
}

.error-message {
  color: #ff4d4d;
  font-size: 0.875rem;
  margin-top: 5px;
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
  font-size: 1rem;
}

.cancel-button {
  background: white;
  border: 1px solid #ddd;
}

.submit-button {
  background: #ff7d29;
  color: white;
  border: none;
}

.submit-button:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.submit-button:not(:disabled):hover {
  background: #ff6b10;
}
</style>
