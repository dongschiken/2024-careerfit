<template>
  <div>
    <HeaderView />
    <MainContentView />
    <div style="min-height: 1500px"></div>
    <FooterView />
  </div>
</template>

<script>
import HeaderView from "@/components/module/MainHeader.vue";
import FooterView from "@/components/module/MainFooter.vue";
import MainContentView from "@/components/main/MainContent.vue";

export default {
  name: "MainView",
  components: {
    HeaderView,
    FooterView,
    MainContentView,
  },
};
</script>

<style lang="css"></style>
