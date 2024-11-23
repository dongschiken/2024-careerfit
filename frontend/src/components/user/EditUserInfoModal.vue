<template>
  <transition name="modal">
    <div class="modal-backdrop">
      <div class="modal-content">
        <div class="modal-inner">
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
                <div class="input-with-button">
                  <input
                    v-model="form.nickname"
                    type="text"
                    placeholder="닉네임을 입력해주세요"
                    :class="{ error: !isNicknameValid && form.nickname }"
                  />
                  <button
                    type="button"
                    @click="checkNickname"
                    :disabled="
                      !form.nickname || form.nickname === originalNickname
                    "
                    class="check-button"
                  >
                    중복확인
                  </button>
                </div>
                <p
                  v-if="nicknameChecked && isAvailable"
                  class="success-message"
                >
                  사용 가능한 닉네임입니다.
                </p>
                <p v-if="nicknameChecked && !isAvailable" class="error-message">
                  이미 사용중인 닉네임입니다.
                </p>
              </div>

              <!-- 전화번호 -->
              <div class="form-group">
                <label>전화번호</label>
                <div class="input-with-button">
                  <input
                    v-model="form.phone"
                    type="text"
                    placeholder="전화번호를 입력해주세요"
                    @input="formatPhoneNumber"
                    maxlength="13"
                  />
                </div>
              </div>

              <!-- 주소 -->
              <div class="form-group">
                <label>우편번호</label>
                <div class="input-with-button">
                  <input
                    v-model="form.postCode"
                    type="text"
                    readonly
                    placeholder="우편번호"
                  />
                  <button
                    type="button"
                    @click="openPostcode"
                    class="check-button"
                  >
                    주소검색
                  </button>
                </div>
              </div>
              <div class="form-group">
                <label>지번주소</label>
                <div class="input-with-button">
                  <input
                    v-model="form.parcelAddress"
                    type="text"
                    readonly
                    placeholder="지번주소"
                  />
                </div>
              </div>
              <div class="form-group">
                <label>도로명주소</label>
                <div class="input-with-button">
                  <input
                    v-model="form.streetAddress"
                    type="text"
                    readonly
                    placeholder="도로명주소"
                  />
                </div>
              </div>
              <div class="form-group">
                <label>상세주소</label>
                <div class="input-with-button">
                  <input
                    v-model="form.detailAddress"
                    type="text"
                    placeholder="상세주소를 입력해주세요"
                  />
                </div>
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
                  :disabled="!isValid"
                  class="submit-button"
                >
                  수정하기
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useUserStore } from "@/stores/userStore";
import api from "@/api/axiosInstance";

const userStore = useUserStore();
const emit = defineEmits(["close", "update"]);

const showModal = ref(false);
let modalContentElement = null;

const loadDaumPostcodeScript = () => {
  return new Promise((resolve) => {
    if (window.daum?.Postcode) {
      resolve();
      return;
    }
    const script = document.createElement("script");
    script.src =
      "//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js";
    script.onload = () => resolve();
    document.head.appendChild(script);
  });
};

onMounted(async () => {
  showModal.value = true;
  modalContentElement = document.querySelector(".modal-content");
  setTimeout(() => {
    modalContentElement.style.opacity = "1";
    modalContentElement.style.transform = "scale(1)";
  }, 10);
  await loadDaumPostcodeScript();
  loadUserProfile();
});

const form = ref({
  nickname: "",
  phone: "",
  postCode: "",
  parcelAddress: "",
  streetAddress: "",
  detailAddress: "",
});

const originalNickname = ref("");
const nicknameChecked = ref(false);
const isAvailable = ref(false);

const loadUserProfile = async () => {
  try {
    const accessToken = sessionStorage.getItem("accessToken");
    api.defaults.headers.common["Authorization"] = `Bearer ${accessToken}`;

    const response = await api.get(`/api/user/${userStore.userId}`);
    const user = response.data;
    form.value = {
      nickname: user.nickname || "",
      phone: user.phone || "",
      postCode: user.postCode || "",
      parcelAddress: user.parcelAddress || "",
      streetAddress: user.streetAddress || "",
      detailAddress: user.detailAddress || "",
    };
    originalNickname.value = user.nickname || "";
  } catch (error) {
    console.error("사용자 정보 로드 실패:", error);
    if (error.response.status === 401) {
      await userStore.clearUser();
      alert("로그인 세션이 만료되었습니다. 다시 로그인해주세요.");
      this.$router.push("/login");
    } else {
      alert("사용자 정보를 가져오는 데 실패했습니다.");
    }
  }
};

const isValid = computed(() => {
  return (
    form.value.nickname &&
    (form.value.nickname === originalNickname.value ||
      (nicknameChecked.value && isAvailable.value))
  );
});

const checkNickname = async () => {
  if (!form.value.nickname) return;
  if (form.value.nickname === originalNickname.value) {
    nicknameChecked.value = true;
    isAvailable.value = true;
    return;
  }

  try {
    const available = await userStore.checkNickname(form.value.nickname);
    nicknameChecked.value = true;
    isAvailable.value = available;
  } catch (error) {
    console.error("닉네임 중복 확인 실패:", error);
    alert("닉네임 중복 확인 중 오류가 발생했습니다.");
  }
};

const formatPhoneNumber = (event) => {
  let value = event.target.value.replace(/[^0-9]/g, "");
  if (value.length > 3 && value.length <= 7) {
    value = value.slice(0, 3) + "-" + value.slice(3);
  } else if (value.length > 7) {
    value = value.slice(0, 3) + "-" + value.slice(3, 7) + "-" + value.slice(7);
  }
  form.value.phone = value;
};

const openPostcode = async () => {
  try {
    if (!window.daum?.Postcode) {
      await loadDaumPostcodeScript();
    }
    new window.daum.Postcode({
      oncomplete: (data) => {
        form.value.postCode = data.zonecode;
        form.value.parcelAddress = data.jibunAddress;
        form.value.streetAddress = data.roadAddress;
      },
      width: "100%",
      height: "100%",
    }).open();
  } catch (error) {
    console.error("주소 검색 실패:", error);
    alert("주소 검색 서비스를 불러오는데 실패했습니다.");
  }
};

const handleSubmit = async () => {
  if (!isValid.value) return;

  try {
    const accessToken = sessionStorage.getItem("accessToken");
    api.defaults.headers.common["Authorization"] = `Bearer ${accessToken}`;

    await api.put(`/api/user/${userStore.userId}`, form.value);
    alert("회원정보가 성공적으로 수정되었습니다.");
    emit("update");
    emit("close");
  } catch (error) {
    console.error("회원정보 수정 실패:", error);
    if (error.response?.status === 401) {
      await userStore.clearUser();
      alert("로그인이 필요합니다.");
      router.push("/login");
    } else {
      alert("회원정보 수정 중 오류가 발생했습니다.");
    }
  }
};
</script>

<style scoped>
.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.3s, transform 0.3s;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
  transform: scale(0.9);
}

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
  margin-bottom: 20px;
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

input[readonly] {
  background-color: #f8f9fa;
  cursor: not-allowed;
}

.check-button {
  padding: 10px 16px;
  background: #ff7d29;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  white-space: nowrap;
  transition: background-color 0.2s;
}

.check-button:hover {
  background: #ff6b10;
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
  font-size: 0.95rem;
  transition: all 0.2s ease-in-out;
  transform-origin: center;
}

.cancel-button:active,
.submit-button:active {
  transform: scale(0.95);
}

.cancel-button:hover,
.submit-button:hover {
  transform: translateY(-1px);
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
</style>
