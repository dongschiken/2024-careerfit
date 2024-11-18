import { defineStore } from "pinia";

export const useUserStore = defineStore({
  id: "user",
  state: () => ({
    userId: null,
    nickname: null,
    role: null,
    profileUrl: null,
    email: null,
    accessToken: sessionStorage.getItem("accessToken") || "", // 세션에서 access token을 가져옴
    refreshToken:
      document.cookie
        .split("; ")
        .find((row) => row.startsWith("refreshToken="))
        ?.split("=")[1] || "", // 쿠키에서 refresh token을 가져옴
  }),
  actions: {
    setUser(user) {
      this.userId = user.userId;
      this.nickname = user.nickname;
      this.role = user.role;
      this.profileUrl = user.profileUrl;
      this.email = user.email;
    },
    setTokens(accessToken, refreshToken) {
      this.accessToken = accessToken;
      this.refreshToken = refreshToken;
      sessionStorage.setItem("accessToken", accessToken); // sessionStorage에 access token 저장
      document.cookie = `refreshToken=${refreshToken}; path=/; max-age=${
        60 * 60 * 24 * 15
      }`; // refresh token 쿠키에 저장`
    },
    clearUser() {
      // this.accessToken = "";
      // this.refreshToken = "";
      this.user = {};
      sessionStorage.removeItem("accessToken");
      document.cookie =
        "refreshToken=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/"; // refresh token 쿠키 삭제
    },
  },
  persist: true,
});
