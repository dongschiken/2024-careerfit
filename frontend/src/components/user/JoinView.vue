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
              <button
                type="button"
                class="check-button"
                @click="verifyEmailCode"
              >
                인증 확인
              </button>
              <button
                type="button"
                class="check-button"
                @click="resendEmailVerification"
              >
                재요청
              </button>
            </div>
            <span class="error-text" v-if="errors.verificationCode">{{
              errors.verificationCode
            }}</span>
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
            />
            <span class="error-text" v-if="errors.passwordConfirm">{{
              errors.passwordConfirm
            }}</span>
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
            </div>
            <span class="error-text" v-if="errors.nickname">{{
              errors.nickname
            }}</span>
          </div>

          <!-- 우편번호 필드 -->
          <div class="input-group">
            <div class="input-with-button">
              <input
                type="text"
                id="postalCode"
                v-model="formData.postalCode"
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
import axios from "axios";

export default {
  name: "joinView",
  data() {
    return {
      formData: {
        email: "",
        verificationCode: "",
        password: "",
        passwordConfirm: "",
        nickname: "",
        postalCode: "",
        parcelAddress: "",
        streetAddress: "",
        detailAddress: "",
      },
      errors: {
        email: "",
        verificationCode: "",
        password: "",
        passwordConfirm: "",
        nickname: "",
      },
      isDaumLoaded: false, // Daum API 로드 상태
      isEmailVerified: false, // 이메일 인증 완료 여부
    };
  },
  mounted() {
    const script = document.createElement("script");
    script.src =
      "https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js";
    script.async = true;
    script.onload = () => {
      this.isDaumLoaded = true; // 스크립트 로드 완료
      window.daum = window.daum || {}; // daum 객체를 전역으로 설정
    };
    document.head.appendChild(script);
  },

  methods: {
    async sendEmailVerification() {
      try {
        // 이메일 인증 코드 전송 API 호출 로직
        const response = await axios.post("API_URL_FOR_EMAIL_VERIFICATION", {
          email: this.formData.email,
        });
        // API 응답 처리
        console.log("이메일 인증 코드 전송 성공:", response);
        // 인증 코드 전송 후 카운트다운 타이머 시작
        this.startVerificationCodeTimer();
      } catch (error) {
        console.error("이메일 인증 코드 전송 실패:", error);
      }
    },
    async verifyEmailCode() {
      try {
        // 이메일 인증 코드 확인 API 호출 로직
        const response = await axios.post(
          "API_URL_FOR_EMAIL_CODE_VERIFICATION",
          {
            email: this.formData.email,
            code: this.formData.verificationCode,
          }
        );
        // API 응답 처리
        if (response.data.isValid) {
          console.log("이메일 인증 성공");
          this.isEmailVerified = true;
        } else {
          this.errors.verificationCode = "인증 코드가 일치하지 않습니다.";
        }
      } catch (error) {
        console.error("이메일 인증 코드 확인 실패:", error);
      }
    },
    resendEmailVerification() {
      // 이메일 인증 코드 재전송 로직
      this.sendEmailVerification();
    },
    startVerificationCodeTimer() {
      // 인증 코드 유효 시간 관리를 위한 타이머 작성
      // (클라이언트 측에서 직접 처리하거나 서버에서 처리할 수 있음)
    },
    async checkNicknameDuplicate() {
      // 닉네임 중복 확인 로직
      console.log("닉네임 중복 확인:", this.formData.nickname);
    },
    main() {
      this.$router.push("/");
    },
    searchAddress() {
      if (!this.isDaumLoaded) {
        alert(
          "주소 검색 서비스를 사용할 수 없습니다. 잠시 후 다시 시도해 주세요."
        );
        return;
      }

      new window.daum.Postcode({
        oncomplete: (data) => {
          // 필수 필드에 값 할당
          this.formData.postalCode = data.zonecode; // 우편번호
          this.formData.parcelAddress = data.jibunAddress; // 지번 주소
          this.formData.streetAddress = data.roadAddress; // 도로명 주소

          // 상세 주소는 기본값으로 빈 문자열 설정 (사용자가 직접 입력)
          this.formData.detailAddress = "";
        },
      }).open();
    },

    validateForm() {
      let isValid = true;
      this.errors = {
        email: "",
        verificationCode: "",
        password: "",
        passwordConfirm: "",
        nickname: "",
      };

      // 이메일 검증
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(this.formData.email)) {
        this.errors.email = "올바른 이메일 형식이 아닙니다.";
        isValid = false;
      }

      // 인증 코드 검증
      if (this.formData.verificationCode.length !== 6) {
        this.errors.verificationCode = "인증 코드가 올바르지 않습니다.";
        isValid = false;
      }

      // 비밀번호 검증
      if (this.formData.password.length < 8) {
        this.errors.password = "비밀번호는 8자 이상이어야 합니다.";
        isValid = false;
      }

      // 비밀번호 확인 검증
      if (this.formData.password !== this.formData.passwordConfirm) {
        this.errors.passwordConfirm = "비밀번호가 일치하지 않습니다.";
        isValid = false;
      }

      // 닉네임 검증
      if (this.formData.nickname.length < 2) {
        this.errors.nickname = "닉네임은 2자 이상이어야 합니다.";
        isValid = false;
      }

      return isValid;
    },
    async handlejoin() {
      if (!this.validateForm()) return;

      try {
        // API 호출 로직
        console.log("회원가입 시도:", this.formData);
        // 성공 시 로그인 페이지로 이동
        this.$router.push("/login");
      } catch (error) {
        console.error("회원가입 실패:", error);
      }
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
.address-button {
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
.address-button:hover {
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
</style>
