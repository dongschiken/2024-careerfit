<template>
  <div>
    <link
      rel="stylesheet"
      href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css"
      integrity="sha512-iecdLmaskl7CVkqkXNQ/ZH/XLlvWZOJyj7Yy7tcenmpD1ypASozpmT/E0iPtmFIB46ZmdtAc9eNBvH0H/ZpiBw=="
      crossorigin="anonymous"
      referrerpolicy="no-referrer"
    />
    <div class="join-page">
      <div class="join-container">
        <div class="join-header">
          <h1 class="brand-title" @click="main">CAREER FIT</h1>
          <p class="join-desc">
            하나의 아이디로 CAREER FIT의 다양한 서비스를 이용해보세요.
          </p>
        </div>

        <form @submit.prevent="handlejoin" class="join-form">
          <!-- 이메일 필드 -->
          <div class="input-group">
            <div class="input-with-button">
              <input
                type="email"
                id="email"
                v-model="formData.email"
                class="input-field"
                placeholder="이메일"
                required
                @input="validateEmailFormat"
              />
              <button
                type="button"
                class="check-verification-button"
                @click="sendEmailVerification"
              >
                이메일 인증
              </button>
            </div>
            <span class="error-text" v-if="errors.email">{{
              errors.email
            }}</span>
          </div>

          <!-- 이메일 인증 코드 입력 -->
          <div class="input-group">
            <div class="input-with-button">
              <input
                type="text"
                id="verificationCode"
                v-model="formData.verificationCode"
                class="input-field"
                placeholder="인증 코드"
                required
              />
              <span v-if="timer > 0" class="timer-text">{{
                formattedTimer
              }}</span>
              <button
                type="button"
                class="check-button"
                @click="verifyEmailCode"
              >
                인증 확인
              </button>
            </div>
            <span class="error-text" v-if="verificationMessage">{{
              verificationMessage
            }}</span>
            <span class="success-text" v-if="isEmailVerified"
              >이메일 인증에 성공하였습니다.</span
            >
          </div>

          <!-- 비밀번호 필드 -->
          <div class="input-group">
            <input
              type="password"
              id="password"
              v-model="formData.password"
              class="input-field"
              placeholder="비밀번호"
              required
            />
            <span class="error-text" v-if="errors.password">{{
              errors.password
            }}</span>
          </div>

          <!-- 비밀번호 확인 필드 -->
          <div class="input-group">
            <input
              type="password"
              id="passwordConfirm"
              v-model="formData.passwordConfirm"
              class="input-field"
              placeholder="비밀번호를 한 번 더 입력하세요."
              required
              @input="validatePasswordMatch"
            />
            <span class="error-text" v-if="errors.passwordConfirm">{{
              errors.passwordConfirm
            }}</span>
            <span
              class="success-text"
              v-if="passwordsMatch && formData.passwordConfirm.length > 0"
              >비밀번호가 일치합니다.</span
            >
          </div>

          <!-- 닉네임 필드 -->
          <div class="input-group">
            <div class="input-with-button">
              <input
                type="text"
                id="nickname"
                v-model="formData.nickname"
                class="input-field"
                placeholder="닉네임"
                required
              />
              <button
                type="button"
                class="check-button"
                @click="checkNicknameDuplicate"
              >
                중복확인
              </button>
              <button
                type="button"
                class="random-button"
                @click="generateRandomNickname"
              >
                랜덤 생성
              </button>
            </div>
            <span class="error-text" v-if="errors.nickname">{{
              errors.nickname
            }}</span>
            <span class="success-text" v-if="nicknameAvailable"
              >사용 가능한 닉네임입니다.</span
            >
            <span
              class="error-text"
              v-if="!nicknameAvailable && nicknameChecked"
              >사용할 수 없는 닉네임입니다.</span
            >
          </div>

          <!-- 전화번호 필드 -->
          <div class="input-group">
            <input
              type="text"
              id="phone"
              v-model="formData.phone"
              class="input-field"
              placeholder="전화번호 (예: 010-1234-5678)"
              @input="formatPhoneNumber"
              required
            />
            <span class="error-text" v-if="errors.phone">{{
              errors.phone
            }}</span>
          </div>

          <!-- 우편번호 필드 -->
          <div class="input-group">
            <div class="input-with-button">
              <input
                type="text"
                id="postCode"
                v-model="formData.postCode"
                class="input-field"
                placeholder="우편번호"
                readonly
                required
              />
              <button
                type="button"
                class="address-button"
                @click="searchAddress"
              >
                주소 검색
              </button>
            </div>
          </div>

          <!-- 지번 주소 필드 -->
          <div class="input-group">
            <input
              type="text"
              id="parcelAddress"
              v-model="formData.parcelAddress"
              class="input-field"
              placeholder="지번 주소"
              readonly
              required
            />
          </div>

          <!-- 도로명 주소 필드 -->
          <div class="input-group">
            <input
              type="text"
              id="streetAddress"
              v-model="formData.streetAddress"
              class="input-field"
              placeholder="도로명 주소"
              readonly
              required
            />
          </div>

          <!-- 상세 주소 필드 -->
          <div class="input-group">
            <input
              type="text"
              id="detailAddress"
              v-model="formData.detailAddress"
              class="input-field"
              placeholder="상세 주소"
            />
          </div>

          <!-- 회원가입 버튼 -->
          <button type="submit" class="join-button">회원가입</button>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import api from "@/api/axiosInstance";

export default {
  name: "joinView",
  data() {
    return {
      formData: {
        email: "",
        phone: "", // 전화번호 필드 추가
        verificationCode: "",
        password: "",
        passwordConfirm: "",
        nickname: "",
        postCode: "",
        parcelAddress: "",
        streetAddress: "",
        detailAddress: "",
        role: "USER",
      },
      errors: {
        email: "",
        phone: "", // 전화번호 필드 오류 메시지 추가
        verificationCode: "",
        password: "",
        passwordConfirm: "",
        nickname: "",
      },
      passwordsMatch: false,
      nicknameAvailable: false,
      nicknameChecked: false,
      isEmailVerified: false,
      verificationMessage: "",
      timer: 0,
      timerInterval: null,
    };
  },
  computed: {
    formattedTimer() {
      const minutes = String(Math.floor(this.timer / 60)).padStart(2, "0");
      const seconds = String(this.timer % 60).padStart(2, "0");
      return `${minutes}:${seconds}`;
    },
  },
  mounted() {
    // Daum 주소 API 스크립트 로드
    const script = document.createElement("script");
    script.src =
      "//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js";
    script.onload = () => {
      console.log("Daum 주소 API 스크립트가 로드되었습니다.");
    };
    script.onerror = () => {
      console.error("Daum 주소 API 스크립트 로드에 실패했습니다.");
    };
    document.head.appendChild(script);
  },
  methods: {
    searchAddress() {
      if (typeof daum === "undefined" || !daum.Postcode) {
        console.error("Daum 주소 API가 로드되지 않았습니다.");
        alert(
          "주소 검색을 위한 API가 로드되지 않았습니다. 페이지를 새로고침 해주세요."
        );
        return;
      }
      new daum.Postcode({
        oncomplete: (data) => {
          // 사용자가 선택한 주소 정보를 사용합니다.
          this.formData.postCode = data.zonecode; // 우편번호
          this.formData.parcelAddress =
            data.jibunAddress || data.autoJibunAddress; // 지번 주소
          this.formData.streetAddress =
            data.roadAddress || data.autoRoadAddress; // 도로명 주소

          // console.log()로 확인해보기
          console.log("우편번호:", this.formData.postCode);
          console.log("지번 주소:", this.formData.parcelAddress);
          console.log("도로명 주소:", this.formData.streetAddress);
        },
      }).open();
    },
    async sendEmailVerification() {
      try {
        const response = await api.post("/auth/send-email-verification", {
          email: this.formData.email,
        });
        console.log("이메일 인증 코드 전송 성공:", response);
        this.startTimer();
      } catch (error) {
        console.error("이메일 인증 코드 전송 실패:", error);
        this.errors.email = "이메일 인증 코드 전송에 실패했습니다.";
      }
    },
    async verifyEmailCode() {
      if (!this.formData.verificationCode) {
        this.verificationMessage = "인증 코드를 입력해주세요.";
        this.isEmailVerified = false;
        return;
      }
      try {
        const response = await api.post("/auth/verify-email-code", {
          email: this.formData.email,
          code: this.formData.verificationCode,
        });
        if (response.data) {
          this.isEmailVerified = true;
          this.verificationMessage = "";
          alert("인증이 완료되었습니다.");
          clearInterval(this.timerInterval);
          this.timer = 0;
        } else {
          this.isEmailVerified = false;
          this.verificationMessage =
            "인증 코드가 일치하지 않습니다. 다시 확인해주세요.";
        }
      } catch (error) {
        console.error("인증 과정에서 문제가 발생했습니다", error);
        this.isEmailVerified = false;
        this.verificationMessage =
          "인증 과정에서 문제가 발생했습니다. 다시 시도해주세요.";
      }
    },
    startTimer() {
      this.timer = 300; // 5분 = 300초
      if (this.timerInterval) clearInterval(this.timerInterval);
      this.timerInterval = setInterval(() => {
        if (this.timer > 0) {
          this.timer--;
        } else {
          clearInterval(this.timerInterval);
        }
      }, 1000);
    },
    generateRandomNickname() {
      const adjectives = ["활기찬", "강인한", "열정적인", "빠른", "힘찬"];
      const nouns = ["챔피언", "트레이너", "에이스", "런너", "리프터"];
      const randomAdjective =
        adjectives[Math.floor(Math.random() * adjectives.length)];
      const randomNoun = nouns[Math.floor(Math.random() * nouns.length)];
      const randomNumber = Math.floor(Math.random() * 1000);
      const generatedNickname = `${randomAdjective}${randomNoun}${randomNumber}`;

      this.formData.nickname = generatedNickname;
      this.checkNicknameDuplicate();
    },
    async checkNicknameDuplicate() {
      this.nicknameChecked = true;
      try {
        const response = await api.get("/api/check-nickname", {
          params: { nickname: this.formData.nickname },
        });
        if (response.data.available) {
          this.nicknameAvailable = true;
          this.errors.nickname = "";
        } else {
          this.nicknameAvailable = false;
          this.errors.nickname = "사용할 수 없는 닉네임입니다.";
        }
      } catch (error) {
        console.error("닉네임 중복 확인 실패:", error);
        this.nicknameAvailable = false;
        this.errors.nickname = "닉네임 중복 확인 중 오류가 발생했습니다.";
      }
    },
    formatPhoneNumber() {
      // 숫자만 남기고 하이픈 추가
      let cleaned = this.formData.phone.replace(/\D/g, "");

      if (cleaned.length <= 3) {
        this.formData.phone = cleaned;
      } else if (cleaned.length <= 7) {
        this.formData.phone = `${cleaned.slice(0, 3)}-${cleaned.slice(3)}`;
      } else {
        this.formData.phone = `${cleaned.slice(0, 3)}-${cleaned.slice(
          3,
          7
        )}-${cleaned.slice(7, 11)}`;
      }
    },
    validatePasswordMatch() {
      if (this.formData.passwordConfirm.length > 0) {
        this.passwordsMatch =
          this.formData.password === this.formData.passwordConfirm;
        if (!this.passwordsMatch) {
          this.errors.passwordConfirm = "비밀번호가 일치하지 않습니다.";
        } else {
          this.errors.passwordConfirm = "";
        }
      }
    },
    validateEmailFormat() {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(this.formData.email)) {
        this.errors.email = "올바른 이메일 형식이 아닙니다.";
      } else {
        this.errors.email = "";
      }
    },
    async handlejoin() {
      if (!this.validateForm()) {
        alert("모든 필수 항목을 올바르게 입력해 주세요.");
        return;
      }

      console.log("회원가입 시도 전 formData 확인:", this.formData);

      try {
        const response = await api.post("/api/join", this.formData, {
          headers: {
            "Content-Type": "application/json",
          },
        });
        console.log("회원가입 성공:", response);
        alert("회원가입이 완료되었습니다.");
        window.location.href = "http://localhost:3000/user/login";
      } catch (error) {
        if (error.response) {
          console.error("회원가입 실패 - 서버 응답 에러:", error.response.data);
          alert(
            `회원가입에 실패하였습니다: ${
              error.response.data.message || "알 수 없는 오류"
            }`
          );
        } else if (error.request) {
          console.error(
            "회원가입 실패 - 서버에 요청을 보내지 못함:",
            error.request
          );
          alert("서버에 요청을 보내지 못했습니다. 네트워크 상태를 확인하세요.");
        } else {
          console.error("회원가입 실패 - 설정 오류:", error.message);
          alert("회원가입 과정에서 문제가 발생했습니다. 다시 시도해 주세요.");
        }
      }
    },
    validateForm() {
      let isValid = true;
      this.errors = {
        email: "",
        phone: "",
        verificationCode: "",
        password: "",
        passwordConfirm: "",
        nickname: "",
        post_code: "",
        parcel_address: "",
        street_address: "",
      };

      // 이메일 검증
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(this.formData.email)) {
        this.errors.email = "올바른 이메일 형식이 아닙니다.";
        isValid = false;
      }

      // 전화번호 검증
      const phoneRegex = /^01[0|1|6-9]-\d{3,4}-\d{4}$/;
      if (!phoneRegex.test(this.formData.phone)) {
        this.errors.phone =
          "올바른 전화번호 형식이 아닙니다. (예: 010-1234-5678)";
        isValid = false;
      }

      // 비밀번호 확인 검증
      if (!this.passwordsMatch) {
        this.errors.passwordConfirm = "비밀번호가 일치하지 않습니다.";
        isValid = false;
      }

      // 닉네임 중복 확인
      if (!this.nicknameChecked || !this.nicknameAvailable) {
        this.errors.nickname = "닉네임 중복 확인이 필요합니다.";
        isValid = false;
      }

      return isValid;
    },
  },
};
</script>

<style scoped>
.join-page {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: white;
  padding: 20px;
}

.join-container {
  width: 100%;
  max-width: 480px;
  background-color: white;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.join-header {
  text-align: center;
  margin-bottom: 40px;
}

.brand-title {
  font-size: 28px;
  font-weight: bold;
  color: #ff7d29;
  margin-bottom: 12px;
  cursor: pointer;
}

.join-desc {
  color: #666;
  font-size: 14px;
  font-weight: bold;
}

.join-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.input-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.input-with-button {
  display: flex;
  gap: 8px;
}

.input-field {
  flex: 1;
  padding: 12px;
  border: 1px solid #e0e0e0;
  border-radius: 10px;
  font-size: 14px;
}

.input-field:focus {
  border-color: #ff7d29;
  outline: none;
}

.check-verification-button,
.check-button,
.address-button,
.random-button {
  padding: 12px 16px;
  height: 46px;
  width: 89.5px;
  background-color: #ff7d29;
  color: white;
  border: 1px solid #e0e0e0;
  border-radius: 10px;
  font-size: 13px;
  white-space: nowrap;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.check-verification-button:hover,
.check-button:hover,
.address-button:hover,
.random-button:hover {
  background-color: #fd8b51;
}

.join-button {
  padding: 14px;
  background-color: #ff7d29;
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.2s;
}

.join-button:hover {
  background-color: #fd8b51;
}

.error-text {
  color: #ff4444;
  font-size: 12px;
  margin-top: 4px;
}

.success-text {
  color: #44b544;
  font-size: 12px;
  margin-top: 4px;
}

.timer-text {
  color: #444;
  font-size: 14px;
  margin-left: 2px;
  margin-top: 22px;
  align-self: center;
}
</style>
