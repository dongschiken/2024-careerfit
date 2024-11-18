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
          <router-link to=""> 헬스장 </router-link>
        </div>
        <div>
          <router-link to=""> Q&A </router-link>
        </div>
        <div>
          <router-link to=""> 공지 </router-link>
        </div>
        <div>
          <router-link to="/meal"> 식단관리 </router-link>
        </div>
      </div>
      <div class="header-nav-login-group">
        <div v-if="!isLoggedIn">
          <router-link to="/user/login">
            <button id="loginBtn" class="form-element login-button">로그인</button>
          </router-link>
          <router-link to="/user/join">
            <button id="joinBtn" class="form-element btn-member-signup">회원가입</button>
          </router-link>
        </div>
        <div v-else>
          <router-link to="/user/mypage">
            <button id="myPageBtn" class="form-element login-button">마이페이지</button>
          </router-link>
          <button id="logoutBtn" @click="handleLogout" class="form-element login-button">로그아웃</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { useUserStore } from '@/stores/userStore';
import { RouterLink } from 'vue-router';
import { computed } from 'vue';

export default {
  name: 'MainHeader',
  components: {
    RouterLink,
  },
  setup() {
    const userStore = useUserStore();
    const isLoggedIn = computed(() => userStore.accessToken !== '');

    const handleLogout = () => {
      userStore.clearUser();
      window.location.reload(); // 로그아웃 후 페이지 새로고침
    };

    return {
      isLoggedIn,
      handleLogout,
    };
  },
};
</script>

<style lang="css" scoped>
@import url(../../assets/css/header.css);
</style>
