<template>
  <div class="header-container">
    <div class="header-layout-container">
      <div class="header-nav-title">
        <router-link to="/">CAREER FIT</router-link>
      </div>
      <div class="header-nav-menu-group">
        <div>
          <router-link to="/board"> 커뮤니티 </router-link>
        </div>
        <div>
          <router-link to=""> 공지 </router-link>
        </div>
        <div>
          <router-link to="/meal"> 식단관리 </router-link>
        </div>
        <div>
          <router-link to="/mate"> Mate </router-link>
        </div>
      </div>
      <div class="header-nav-login-group">
        <div v-if="!isLoggedIn">
          <router-link to="/user/login">
            <button id="loginBtn" class="form-element login-button">
              로그인
            </button>
          </router-link>
          <router-link to="/user/join">
            <button id="joinBtn" class="form-element btn-member-signup">
              회원가입
            </button>
          </router-link>
        </div>
        <div v-else>
          <router-link to="/user/mypage">
            <button id="myPageBtn" class="form-element login-button">
              마이페이지
            </button>
          </router-link>
          <button
            id="logoutBtn"
            @click="handleLogout"
            class="form-element login-button"
          >
            로그아웃
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { useUserStore } from "@/stores/userStore";
import { RouterLink } from "vue-router";
import { computed } from "vue";
import KakaoAuthService from "@/services/kakaoAuthService";
import axios from '@/api/noTokenAxiosInstance';
import api from '@/api/axiosInstance';

export default {
  name: "MainHeader",
  components: {
    RouterLink,
  },
  setup() {

    const isLoggedIn = computed(() => {
      const token = sessionStorage.getItem("accessToken");
      return token !== null;
    });

    const handleLogout = async () => {
      try {
        // Access Token과 Refresh Token 가져오기
        const accessToken = sessionStorage.getItem("accessToken");
        const refreshToken = sessionStorage.getItem("refreshToken");

        if (!accessToken) {
          alert("로그인된 상태가 아닙니다.");
          return;
        }

        // 백엔드로 로그아웃 요청
        await api.delete("/api/logout", {
          data: {
            accessToken: `Bearer ${accessToken}`, // "Bearer " 접두사 포함
            refreshToken: refreshToken || null, // RefreshToken은 Optional
          },
        });

        // 로그아웃 성공 시, 스토리지에서 토큰 삭제
        sessionStorage.removeItem("accessToken");
        sessionStorage.removeItem("refreshToken");

        alert("로그아웃되었습니다.");
        window.location.href = "/"; // 메인 페이지로 이동
      } catch (error) {
        console.error("로그아웃 실패:", error);
        alert("로그아웃에 실패했습니다. 다시 시도해주세요.");
      }
    };


    return {
      isLoggedIn,
      handleLogout,
    };
  },
};
</script>

<style lang="css" scoped>
@import url(@/assets/css/header.css);
</style>