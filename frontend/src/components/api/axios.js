import axios from "axios";

// 기본 axios 인스턴스 생성
const api = axios.create({
  baseURL: "http://localhost:8080", // API 서버 주소
  headers: {
    "Content-Type": "application/json",
  },
});

// 요청 인터셉터에서 JWT 토큰을 헤더에 추가
api.interceptors.request.use(
  (config) => {
    const accessToken = sessionStorage.getItem("accessToken");
    if (accessToken) {
      config.headers["Authorization"] = `Bearer ${accessToken}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 응답 인터셉터에서 토큰 만료 시 refresh token으로 새로운 access token 요청
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response.status === 401) {
      // 토큰 만료 시
      const refreshToken = getCookie("refreshToken");
      if (refreshToken) {
        try {
          const response = await api.post("/api/refresh-token", {
            refreshToken,
          });
          sessionStorage.setItem("accessToken", response.data.accessToken);
          error.config.headers[
            "Authorization"
          ] = `Bearer ${response.data.accessToken}`;
          return api(error.config); // 재요청
        } catch (e) {
          console.error("Refresh Token Error: ", e);
        }
      }
    }
    return Promise.reject(error);
  }
);

// 쿠키에서 refreshToken을 가져오는 함수
function getCookie(name) {
  const value = document.cookie.match("(^|;)\\s*" + name + "\\s*=\\s*([^;]+)");
  return value ? value.pop() : "";
}

export default api;
