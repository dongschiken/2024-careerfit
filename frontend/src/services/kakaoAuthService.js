// KakaoAuthService.js
import api from "@/api/axiosInstance";
import axios from "axios";

class KakaoAuthService {
  async login() {
    const KAKAO_CLIENT_ID = "41c1a6b4b4c21c5909d57e7a96073a47";
    const REDIRECT_URI = encodeURIComponent(
      "http://192.168.210.52:3000/oauth/callback/kakao"
    );
    const KAKAO_AUTH_URL = `https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=${KAKAO_CLIENT_ID}&redirect_uri=${REDIRECT_URI}`;
    window.location.href = KAKAO_AUTH_URL;
  }

  async logout() {
    await api.post("https://kauth.kakao.com/oauth/logout", null, {
      headers: {
        Authorization: `Bearer ${sessionStorage.getItem("accessToken")}`,
      },
    });
  }
}

export default KakaoAuthService;
