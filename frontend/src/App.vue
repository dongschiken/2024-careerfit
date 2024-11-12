<template>
  <div>
    <router-view />
    <nav>
      <RouterLink to="/">Home</RouterLink>
      <RouterLink :to="{ name: 'board' }">Board</RouterLink>
      <RouterLink :to-="{ name : 'join' }">Join</RouterLink>
    </nav>
  </div>
</template>

<script setup></script>

<style scoped>
nav a {
  font-weight: bold;
  text-decoration: none;
  color: black;
}
nav a.router-link-exact-active {
  color: #43b983;
}
</style>
