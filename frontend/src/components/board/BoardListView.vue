<template>
  <HeaderView />
  <h1>게시글 목록</h1>
  <FooterView />
</template>

<script>
import HeaderView from "@/components/module/HeaderView.vue";
import FooterView from "@/components/module/FooterView.vue";
export default {
  components: {
    HeaderView,
    FooterView,
  },
};
</script>
