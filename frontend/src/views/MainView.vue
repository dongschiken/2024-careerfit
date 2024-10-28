<template>
  <div>
    <HeaderView />
    <MainContentView />
    <div style="min-height: 1500px"></div>
    <FooterView />
  </div>
</template>

<script>
import HeaderView from "@/components/module/HeaderView.vue";
import FooterView from "@/components/module/FooterView.vue";
import MainContentView from "@/components/main/MainContentView.vue";

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
