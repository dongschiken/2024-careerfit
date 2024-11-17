<template>
  <div>
    <HeaderView />
    <MainContent />
    <div style="min-height: 1500px"></div>
    <FooterView />
  </div>
</template>

<script>
import { useUserStore } from "@/stores/userStore";
import HeaderView from "@/components/module/MainHeader.vue";
import FooterView from "@/components/module/MainFooter.vue";
import MainContent from "@/components/main/MainContent.vue";

export default {
  name: "MainView",
  components: {
    HeaderView,
    FooterView,
    MainContent,
  },
  computed: {
    isLoggedIn() {
      const userStore = useUserStore();
      return userStore.accessToken !== "";
    },
  },
  mounted() {
    if (this.isLoggedIn) {
      // 로그인 상태라면
      const userStore = useUserStore();
      userStore.setUser({ nickname: "사용자 이름" }); // 여기에 실제 유저 데이터를 넣으면 됨
    }
  },
};
</script>

<style lang="css"></style>
