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
        <div>
          <router-link to="/user/login">
            <button id="loginBtn">로그인</button>
          </router-link>
        </div>
        <div>
          <router-link to="/user/join">
            <button id="joinBtn">회원가입</button>
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { RouterLink } from "vue-router";
</script>

<style lang="css" scoped>
@import url(../../assets/css/header.css);
</style>
