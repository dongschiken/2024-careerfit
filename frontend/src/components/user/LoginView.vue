<template>
  <div>
    <div class="login-page">
      <div class="login-container">
        <h1 class="login-title" @click="main">CAREER FIT</h1>

        <form class="login-form" @submit.prevent="handleLogin">
          <input
            type="email"
            v-model="formData.email"
            class="form-element login-input"
            placeholder="이메일"
          />
          <input
            type="password"
            v-model="formData.password"
            class="form-element login-input"
            placeholder="비밀번호"
          />
          <button type="submit" class="form-element login-button">
            로그인
          </button>
        </form>

        <p class="login-question">아직 회원이 아니세요?</p>

        <div class="login-options">
          <button class="form-element btn-member-signup" @click="handleSignup">
            <span class="btn-icon"
              ><img src="@/assets/regist-icon.png" alt="회원가입 아이콘"
            /></span>
            회원가입
          </button>
          <button
            class="form-element btn-kakao-login"
            @click="handleKakaoLogin"
          >
            <span class="btn-icon"
              ><img src="@/assets/kakao-icon.png" alt=""
            /></span>
            카카오 로그인
          </button>
        </div>
      </div>
      <div class="reserve">ⓒ (주)peach Corp. All rights reserved.</div>
    </div>
  </div>
</template>

<script>
import api from "@/api/axiosInstance";
import { useUserStore } from "@/stores/userStore";
export default {
  name: "LoginComponent",
  data() {
    return {
      formData: {
        email: "",
        password: "",
      },
    };
  },
  methods: {
    async handleLogin() {
      console.log("로그인 시도:", this.formData);

      try {
        // 로그인 요청을 보냅니다.
        const response = await api.post("/login", this.formData);

        // 응답 확인
        console.log("로그인 응답:", response);

        // 응답 데이터에서 accessToken과 refreshToken을 가져옵니다.
        const accessToken = response.data.accessToken;

        // Access Token이 있는지 확인하고 세션 스토리지에 저장합니다.
        if (accessToken) {
          this.storeTokens(accessToken);
        } else {
          throw new Error("Token을 찾을 수 없습니다.");
        }
        alert("로그인에 성공했습니다.");

        // 로그인 성공 후 라우터를 사용해 메인 페이지로 이동
        this.$router.push("/");
      } catch (error) {
        console.error("로그인 실패", error);
        alert("로그인에 실패했습니다. 이메일 또는 비밀번호를 확인하세요.");
      }
    },

    handleSignup() {
      this.$router.push("/user/join");
    },
    handleKakaoLogin() {
      console.log("카카오 로그인 클릭");
    },
    main() {
      this.$router.push("/");
    },
    storeTokens(accessToken, refreshToken) {
      const userStore = useUserStore();
      userStore.setTokens(accessToken, refreshToken);
    },
  },
};
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: white;
  padding: 1rem;
}

.login-container {
  width: 100%;
  max-width: 400px;
  padding: 2.5rem;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.login-title {
  text-align: center;
  color: #ff7d29;
  font-size: 1.8rem;
  margin-bottom: 2.5rem;
  cursor: pointer;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-bottom: 2rem;
}

/* 모든 폼 요소들의 공통 스타일 */
.form-element {
  width: 100%;
  height: 48px; /* 모든 요소 높이 통일 */
  border-radius: 10px;
  font-size: 1rem;
  box-sizing: border-box; /* padding이 전체 크기에 포함되도록 설정 */
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 1rem;
  margin: 0;
  border: 1px solid #ddd;
}

.login-input {
  outline: none;
  transition: border-color 0.2s;
}

.login-input:focus {
  border-color: #ff7d29;
}

.login-button {
  background-color: #ff7d29;
  color: white;
  border: none;
  cursor: pointer;
  font-weight: bold;
  transition: background-color 0.2s;
}

.login-button:hover {
  background-color: #ff7d29;
}

.login-question {
  text-align: center;
  color: #666;
  margin: 2rem 0;
  font-size: 0.9rem;
}

.login-options {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.btn-member-signup {
  background-color: #f9f9f1;
  gap: 0.5rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-member-signup:hover {
  background-color: #f0f0e6;
}

.btn-kakao-login {
  background-color: #ffe500;
  gap: 0.5rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-kakao-login:hover {
  background-color: #f5dc00;
}

.btn-icon {
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-icon img {
  width: 20px;
  height: 20px;
  object-fit: contain;
}

.login-page {
  display: flex;
  flex-direction: column;
}

.reserve {
  margin-top: 1.2rem;
  color: #666;
  font-size: 0.9rem;
}
@media (max-width: 480px) {
  .login-container {
    padding: 2rem;
  }

  .login-title {
    font-size: 1.5rem;
    margin-bottom: 2rem;
  }

  .form-element {
    height: 44px; /* 모바일에서는 조금 더 작게 */
  }
}
</style>
