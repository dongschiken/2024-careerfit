<template>
  <div class="header-container">
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
    </div>
    <div class="header-nav-login-group">
      <div>
        <button id="loginBtn">
          <router-link to="/user/login">로그인</router-link>
        </button>
      </div>
      <div>
        <button id="joinBtn">
          <router-link to="/user/join">회원가입</router-link>
        </button>
      </div>
    </div>
  </div>
</template>

<script></script>

<style lang="css">
@import url(../../assets/css/header.css);
</style>
