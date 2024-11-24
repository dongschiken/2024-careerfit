<template>
  <div class="modal-backdrop" @click="$emit('close')">
    <div class="modal-content" @click.stop>
      <div class="modal-inner">
        <div class="modal-header">
          <h2>비밀번호 변경</h2>
          <button @click="$emit('close')" class="close-button">
            <i class="fas fa-times"></i>
          </button>
        </div>

        <div class="modal-body">
          <form @submit.prevent="handleSubmit">
            <!-- 현재 비밀번호 -->
            <div class="form-group">
              <label>현재 비밀번호</label>
              <div class="input-with-button">
                <input
                  v-model="form.currentPassword"
                  type="password"
                  placeholder="현재 비밀번호를 입력해주세요"
                  :class="{ error: currentPasswordStatus === false }"
                  @input="validateCurrentPassword"
                />
              </div>
              <p v-if="errors.currentPassword" class="error-text">
                {{ errors.currentPassword }}
              </p>
            </div>

            <!-- 새 비밀번호 -->
            <div class="form-group">
              <label>새 비밀번호</label>
              <div class="input-with-button">
                <input
                  v-model="form.newPassword"
                  type="password"
                  placeholder="새 비밀번호를 입력해주세요"
                  @input="validatePassword"
                />
              </div>
              <p v-if="errors.password" class="error-text">
                {{ errors.password }}
              </p>
              <p class="help-text">영문, 숫자, 특수문자를 포함한 8자 이상</p>
            </div>

            <!-- 새 비밀번호 확인 -->
            <div class="form-group">
              <label>새 비밀번호 확인</label>
              <div class="input-with-button">
                <input
                  v-model="form.confirmPassword"
                  type="password"
                  placeholder="새 비밀번호를 다시 입력해주세요"
                  @input="validatePasswordMatch"
                />
              </div>
              <p v-if="errors.passwordConfirm" class="error-text">
                {{ errors.passwordConfirm }}
              </p>
              <p
                v-if="passwordsMatch && form.confirmPassword"
                class="success-text"
              >
                비밀번호가 일치합니다.
              </p>
            </div>

            <div class="button-group">
              <button
                type="button"
                @click="$emit('close')"
                class="cancel-button"
              >
                취소
              </button>
              <button
                type="submit"
                :disabled="!isValid()"
                class="submit-button"
              >
                변경하기
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import { useUserStore } from "@/stores/userStore";
import api from "@/api/axiosInstance";

const userStore = useUserStore();
const emit = defineEmits(["close"]);

const form = ref({
  currentPassword: "",
  newPassword: "",
  confirmPassword: "",
});

const errors = ref({
  password: "",
  passwordConfirm: "",
});

const currentPasswordStatus = ref(null);
const passwordsMatch = ref(false);

// PasswordModal.vue의 validateCurrentPassword 함수 수정
// PasswordModal.vue의 validateCurrentPassword 함수
const validateCurrentPassword = async () => {
  if (!form.value.currentPassword) {
    currentPasswordStatus.value = null;
    return;
  }
  try {
    const response = await api.post(
      `/api/user/${userStore.userId}/check-current-password`,
      { currentPassword: form.value.currentPassword }
    );
    currentPasswordStatus.value = response.data;
    errors.value.currentPassword = currentPasswordStatus.value
      ? ""
      : "현재 비밀번호가 일치하지 않습니다.";
  } catch (error) {
    console.error("현재 비밀번호 확인 실패:", error);
    currentPasswordStatus.value = false;
    errors.value.currentPassword = "비밀번호 확인 중 오류가 발생했습니다.";
  }
};

// 새 비밀번호 유효성 검사 (회원가입과 동일한 로직)
const validatePassword = () => {
  const passwordRegex =
    /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[@$!%*?&#])[A-Za-z\d@$!%*?&#]{8,}$/;

  if (!form.value.newPassword) {
    errors.value.password = "새 비밀번호를 입력해주세요.";
    return false;
  }

  if (!passwordRegex.test(form.value.newPassword)) {
    errors.value.password =
      "비밀번호는 8자 이상이며, 영문, 숫자, 특수문자를 각각 하나 이상 포함해야 합니다.";
    return false;
  }

  errors.value.password = "";
  return true;
};

// 비밀번호 일치 검사
const validatePasswordMatch = () => {
  if (form.value.confirmPassword.length > 0) {
    passwordsMatch.value =
      form.value.newPassword === form.value.confirmPassword;
    if (!passwordsMatch.value) {
      errors.value.passwordConfirm = "비밀번호가 일치하지 않습니다.";
    } else {
      errors.value.passwordConfirm = "";
    }
  }
};

const isValid = async () => {
  await validateCurrentPassword();
  return (
    currentPasswordStatus.value &&
    form.value.newPassword &&
    form.value.confirmPassword &&
    passwordsMatch.value &&
    !errors.value.password
  );
};

const handleSubmit = async () => {
  try {
    if (!(await isValid())) {
      return;
    }

    const passwordChangeData = {
      currentPassword: form.value.currentPassword,
      newPassword: form.value.newPassword,
      confirmPassword: form.value.confirmPassword,
    };

    const response = await userStore.changePassword(passwordChangeData);

    if (response) {
      alert("비밀번호가 성공적으로 변경되었습니다.");
      emit("close");
    }
  } catch (error) {
    if (error.response?.status === 400) {
      alert(
        "현재 비밀번호가 일치하지 않거나, 새 비밀번호가 요구사항을 충족하지 않습니다."
      );
    } else {
      alert("비밀번호 변경 중 오류가 발생했습니다.");
    }
  }
};
</script>

<style scoped>
/* 기존 스타일 코드 */
</style>
<style scoped>
.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 450px;
  overflow-y: auto;
  max-height: 80vh;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  animation: modalFadeIn 0.3s ease-out;
}

.modal-inner {
  padding: 25px;
  display: flex;
  flex-direction: column;
  align-items: stretch;
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

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
}

.input-with-button {
  display: flex;
  gap: 10px;
}

.input-with-button input {
  flex: 1;
}

input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 0.95rem;
  transition: border-color 0.2s;
}

input:focus {
  outline: none;
  border-color: #ff7d29;
}

input.error {
  border-color: #ff4d4d;
}

.help-text {
  color: #666;
  font-size: 12px;
  margin-top: 4px;
}

.error-text {
  color: #ff4d4d;
  font-size: 12px;
  margin-top: 4px;
}

.success-text {
  color: #44b544;
  font-size: 12px;
  margin-top: 4px;
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

@keyframes modalFadeIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
