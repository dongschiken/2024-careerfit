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
          <button id="kakaoLoginBtn" class="form-element btn-kakao-login" @click="handleKakaoLogin">
  <span class="btn-icon"><img src="@/assets/kakao-icon.png" alt="카카오 로그인 아이콘"/></span>
  카카오 로그인
</button>
        </div>
      </div>
      <div class="reserve">ⓒ (주)peach Corp. All rights reserved.</div>
    </div>
  </div>
</template>

<script>
import ncapi from "@/api/noTokenAxiosInstance";
import { useUserStore } from "@/stores/userStore";

export default {
  name: "LoginComponent",
  setup() {
    const userStore = useUserStore();
    return {
      userStore
    };
  },
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
        const response = await ncapi.post("/login", this.formData);
        console.log("로그인 응답:", response);

        const { accessToken, refreshToken } = response.data;
        if (accessToken) {
          this.storeTokens(accessToken, refreshToken);
          alert("로그인에 성공했습니다.");
          this.$router.push("/");
        } else {
          throw new Error("Token을 찾을 수 없습니다.");
        }
      } catch (error) {
        console.error("로그인 실패", error);
        alert("로그인에 실패했습니다. 이메일 또는 비밀번호를 확인하세요.");
      }
    },

    handleSignup() {
      this.$router.push("/user/join");
    },


    handleKakaoLogin() {
    const KAKAO_CLIENT_ID = "41c1a6b4b4c21c5909d57e7a96073a47";
    const REDIRECT_URI = encodeURIComponent("http://localhost:3000/oauth/callback/kakao");
    // scope 파라미터는 앱에서 설정된 동의항목만 포함해야 합니다
    const KAKAO_AUTH_URL = `https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=${KAKAO_CLIENT_ID}&redirect_uri=${REDIRECT_URI}`;
    window.location.href = KAKAO_AUTH_URL;
},
  
 
async getKakaoUserInfo(accessToken) {
    try {
      const response = await window.Kakao.API.request({
        url: '/v2/user/me',
        success: (response) => {
          console.log(response);
          // 사용자 정보를 저장하거나 처리하는 로직 추가
          alert(`안녕하세요, ${response.kakao_account.profile.nickname}님!`);
        },
        fail: (error) => {
          console.error(error);
          alert('사용자 정보를 가져오는데 실패했습니다.');
        },
      });
    } catch (error) {
      console.error('API 요청 중 오류 발생:', error);
    }
  },

  main() {
    this.$router.push("/");
  },

  storeTokens(accessToken, refreshToken) {
    this.userStore.setTokens(accessToken, refreshToken);
  },
  }
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

.form-element {
  width: 100%;
  height: 48px;
  border-radius: 10px;
  font-size: 1rem;
  box-sizing: border-box;
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
    height: 44px;
  }
}
</style>