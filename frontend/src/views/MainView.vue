<template>
  <div>
    <HeaderView />
    <h1>Main View</h1>
    <div style="min-height: 1500px"></div>
    <FooterView />
  </div>
</template>

<script>
import HeaderView from "@/components/module/HeaderView.vue";
import FooterView from "@/components/module/FooterView.vue";

export default {
  name: "MainView",
  components: {
    HeaderView,
    FooterView,
  },
};
</script>

<style lang="css">
  
</style>
