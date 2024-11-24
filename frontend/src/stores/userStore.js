import { defineStore } from "pinia";
import { ref } from "vue";
import api from "@/api/axiosInstance";
import axios from "axios";

export const useUserStore = defineStore({
  id: "user",
  state: () => ({
    userId: null,
    nickname: null,
    role: null,
    profileUrl: null,
    email: null,
    accessToken: sessionStorage.getItem("accessToken") || "",
    refreshToken: sessionStorage.getItem("refreshToken") || "",
    REST_API: "http://localhost:8080/api",
    HOST: "http://localhost:8080",
    loading: false,
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
      sessionStorage.setItem("accessToken", accessToken);
      sessionStorage.setItem("refreshToken", refreshToken);
    },

    async clearUser() {
      const accessToken = this.accessToken;
      const refreshToken = this.refreshToken;
      if (!accessToken || !refreshToken) {
        return;
      }
      sessionStorage.removeItem("accessToken");
      this.accessToken = "";
      try {
        const response = await api.delete("http://localhost:8080/api/logout", {
          data: { refreshToken },
          headers: {
            "Content-Type": "application/json",
          },
        });
        if (response.status === 200) {
          this.accessToken = "";
          this.refreshToken = "";
          this.userId = null;
          this.nickname = null;
          this.role = null;
          this.profileUrl = null;
          this.email = null;

          sessionStorage.removeItem("refreshToken");
          alert("로그아웃 성공");
        }
      } catch (error) {
        console.error("로그아웃 실패", error);
      }
    },

    // 현재 사용자 정보 조회
    async fetchCurrentUser() {
      try {
        this.loading = true;
        // userId가 없는 경우 먼저 토큰에서 정보를 가져옵니다
        if (!this.userId) {
          const tokenResponse = await axios.get(`${this.REST_API}/token-user`, {
            headers: {
              Authorization: `Bearer ${this.accessToken}`,
            },
          });
          if (tokenResponse.data) {
            this.setUser(tokenResponse.data);
          }
        }

        // 이제 userId로 상세 정보를 가져옵니다
        if (this.userId) {
          const response = await axios.get(
            `${this.REST_API}/user/${this.userId}`,
            {
              headers: {
                Authorization: `Bearer ${this.accessToken}`,
              },
            }
          );
          this.setUser(response.data);
          return response.data;
        }
      } catch (error) {
        console.error("사용자 정보 조회 실패:", error);
        if (error.response && error.response.status === 401) {
          await this.clearUser();
          throw new Error("로그인이 필요합니다.");
        }
        throw error;
      } finally {
        this.loading = false;
      }
    },

    // 사용자 정보 업데이트
    async updateUser(userData) {
      try {
        const response = await axios.put(
          `${this.REST_API}/user/${this.userId}`,
          userData,
          {
            headers: {
              Authorization: `Bearer ${this.accessToken}`,
            },
          }
        );

        // 응답 헤더에서 새로운 토큰 확인
        const newToken = response.headers.authorization;
        if (newToken) {
          const token = newToken.replace("Bearer ", "");
          this.setTokens(token, this.refreshToken);
        }

        // 사용자 정보 갱신
        await this.fetchCurrentUser();
        return response.data;
      } catch (error) {
        console.error("사용자 정보 수정 실패:", error);
        if (error.response && error.response.status === 401) {
          await this.clearUser();
          throw new Error("로그인이 필요합니다.");
        }
        throw error;
      }
    },

    async updateProfilePicture(file) {
      try {
        const formData = new FormData();
        formData.append("file", file);

        const response = await axios.put(
          `${this.REST_API}/user/${this.userId}/profile-picture`,
          formData,
          {
            headers: {
              Authorization: `Bearer ${this.accessToken}`,
              "Content-Type": "multipart/form-data",
            },
          }
        );
        await this.fetchCurrentUser();
        return response.data;
      } catch (error) {
        console.error("프로필 이미지 업데이트 실패:", error);
        throw error;
      }
    },

    async changePassword(passwordData) {
      try {
        const response = await api.put(
          `/api/user/${this.userId}/password`,
          passwordData,
          {
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${this.accessToken}`,
            },
          }
        );
        return response.data;
      } catch (error) {
        console.error("Password change failed:", error);
        throw error;
      }
    },

    async checkNickname(nickname) {
      try {
        const response = await axios.get(
          `${this.REST_API}/check-nickname?nickname=${nickname}`,
          {
            headers: {
              Authorization: `Bearer ${this.accessToken}`,
            },
          }
        );
        return response.data.available;
      } catch (error) {
        console.error("닉네임 중복 확인 실패:", error);
        throw error;
      }
    },

    // 토큰 검증
    async validateToken() {
      if (!this.accessToken) return false;
      try {
        await this.fetchCurrentUser();
        return true;
      } catch (error) {
        return false;
      }
    },
  },
  persist: true,
});
