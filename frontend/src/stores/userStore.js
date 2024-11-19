import { defineStore } from "pinia";
import axios from "axios";
import { ref } from "vue";
export const useUserStore = defineStore({
  id: "user",
  state: () => ({
    userId: null,
    nickname: null,
    role: null,
    profileUrl: null,
    email: null,
    accessToken: sessionStorage.getItem("accessToken") || "", // 세션에서 access token을 가져옴
    refreshToken: sessionStorage.getItem("refreshToken") || "",
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
      sessionStorage.setItem("refreshToken", refreshToken);
    },
    async clearUser() {
      const accessToken = this.accessToken;
      const refreshToken = this.refreshToken;
      if (!accessToken || !refreshToken) {
        alert("로그아웃에 필요한 토큰이 없습니다.");
        return;
      }
      try {
        // 로그아웃 API 요청을 보냄
        const response = await axios.delete(
          "http://localhost:8080/api/logout",
          {
            data: { refreshToken },
            headers: {
              "Content-Type": "application/json",
            },
          }
        );
        // 로그아웃 성공 후 상태 초기화
        if (response.status === 200) {
          this.accessToken = "";
          this.refreshToken = "";
          this.userId = null;
          this.nickname = null;
          this.role = null;
          this.profileUrl = null;
          this.email = null;

          // sessionStorage에서 토큰 삭제
          sessionStorage.removeItem("accessToken");
          sessionStorage.removeItem("refreshToken");

          console.log("로그아웃 성공");
          alert("로그아웃 성공");
        }
      } catch (error) {
        console.error("로그아웃 실패", error);
      }
    },
  },
  persist: true,
});
