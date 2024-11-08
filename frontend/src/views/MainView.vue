<template>
  <div>
    <HeaderView />
    <MainContent />
    <div style="min-height: 1500px"></div>
    <FooterView />
  </div>
</template>

<script>
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
};
</script>

<style lang="css"></style>
