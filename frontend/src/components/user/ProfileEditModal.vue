<template>
  <div class="modal-overlay">
    <div class="modal-container">
      <div class="modal-header">
        <h2>회원정보 수정</h2>
        <button @click="$emit('close')" class="close-button">
          <i class="fas fa-times"></i>
        </button>
      </div>

      <div class="modal-body">
        <form @submit.prevent="handleSubmit">
          <!-- 닉네임 -->
          <div class="form-group">
            <label>닉네임</label>
            <div class="nickname-input">
              <input
                v-model="form.nickname"
                type="text"
                :class="{ error: !isNicknameValid && form.nickname }"
              />
              <button
                type="button"
                @click="checkNickname"
                :disabled="!form.nickname || form.nickname === originalNickname"
              >
                중복확인
              </button>
            </div>
            <p v-if="nicknameChecked" class="success-message">
              사용 가능한 닉네임입니다.
            </p>
          </div>

          <!-- 전화번호 -->
          <div class="form-group">
            <label>전화번호</label>
            <input
              v-model="form.phone"
              type="tel"
              placeholder="- 없이 입력해주세요"
            />
          </div>

          <!-- 주소 -->
          <div class="form-group">
            <label>우편번호</label>
            <div class="address-input">
              <input v-model="form.postCode" type="text" readonly />
              <button type="button" @click="openPostcode">주소검색</button>
            </div>
          </div>

          <div class="form-group">
            <label>지번주소</label>
            <input v-model="form.parcelAddress" type="text" readonly />
          </div>

          <div class="form-group">
            <label>도로명주소</label>
            <input v-model="form.streetAddress" type="text" readonly />
          </div>

          <div class="form-group">
            <label>상세주소</label>
            <input
              v-model="form.detailAddress"
              type="text"
              placeholder="상세주소를 입력해주세요"
            />
          </div>

          <div class="button-group">
            <button type="button" @click="$emit('close')" class="cancel-button">
              취소
            </button>
            <button type="submit" :disabled="!isValid" class="submit-button">
              수정하기
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useUserStore } from "@/stores/userStore";

const userStore = useUserStore();
const emit = defineEmits(["close", "update"]);

const form = ref({
  nickname: "",
  phone: "",
  postCode: "",
  parcelAddress: "",
  streetAddress: "",
  detailAddress: "",
});

const nicknameChecked = ref(false);
const originalNickname = ref("");

onMounted(() => {
  // 초기값 설정
  form.value = {
    nickname: userStore.nickname || "",
    phone: userStore.phone || "",
    postCode: userStore.postCode || "",
    parcelAddress: userStore.parcelAddress || "",
    streetAddress: userStore.streetAddress || "",
    detailAddress: userStore.detailAddress || "",
  };
  originalNickname.value = userStore.nickname || "";
});

const openPostcode = () => {
  new window.daum.Postcode({
    oncomplete: (data) => {
      form.value.postCode = data.zonecode;
      form.value.parcelAddress = data.jibunAddress;
      form.value.streetAddress = data.roadAddress;
    },
  }).open();
};

const checkNickname = async () => {
  if (form.value.nickname === originalNickname.value) {
    nicknameChecked.value = true;
    return;
  }

  try {
    const isAvailable = await userStore.checkNickname(form.value.nickname);
    nicknameChecked.value = isAvailable;
    if (!isAvailable) {
      alert("이미 사용중인 닉네임입니다.");
    }
  } catch (error) {
    console.error("닉네임 중복 확인 실패:", error);
    alert("닉네임 중복 확인 중 오류가 발생했습니다.");
  }
};

const isValid = computed(() => {
  return (
    form.value.nickname &&
    (form.value.nickname === originalNickname.value || nicknameChecked.value)
  );
});

const handleSubmit = async () => {
  try {
    await userStore.updateUser(form.value);
    emit("update");
    emit("close");
  } catch (error) {
    console.error("회원정보 수정 실패:", error);
    alert("회원정보 수정 중 오류가 발생했습니다.");
  }
};
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-container {
  background: white;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  padding: 20px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h2 {
  margin: 0;
  font-size: 1.25rem;
  font-weight: bold;
}

.close-button {
  background: none;
  border: none;
  font-size: 1.25rem;
  cursor: pointer;
  padding: 5px;
}

.modal-body {
  padding: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: 500;
}

.form-group input {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.nickname-input,
.address-input {
  display: flex;
  gap: 10px;
}

.nickname-input input,
.address-input input {
  flex: 1;
}

.nickname-input button,
.address-input button {
  padding: 8px 16px;
  background: #f5f5f5;
  border: 1px solid #ddd;
  border-radius: 4px;
  white-space: nowrap;
}

.button-group {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.cancel-button,
.submit-button {
  padding: 8px 20px;
  border-radius: 4px;
  cursor: pointer;
}

.cancel-button {
  background: white;
  border: 1px solid #ddd;
}

.submit-button {
  background: #ff6b00;
  border: none;
  color: white;
}

.submit-button:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.success-message {
  color: green;
  font-size: 0.875rem;
  margin-top: 5px;
}

.error {
  border-color: red !important;
}
</style>
