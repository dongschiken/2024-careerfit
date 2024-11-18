import axios from "axios";

const api2 = axios.create({
  baseURL: "http://localhost:8080",
  headers: {
    "Content-Type": "application/json",
  },
});
const api = axios.create({
  baseURL: "http://localhost:8080", // 백엔드의 기본 API URL로 설정
  headers: {
    "Content-Type": "application/json",
  },
});

// 요청 인터셉터 설정
api.interceptors.request.use(
  (config) => {
    const token = sessionStorage.getItem("accessToken");

    if (token) {
      config.headers["Authorization"] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    Promise.reject(error);
  }
);

// 응답 인터셉터 설정
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // 액세스 토큰이 만료되었을 때 리프레시 토큰을 이용해 새로운 액세스 토큰을 요청
    if (
      error.response &&
      error.response.status === 401 &&
      !originalRequest._retry
    ) {
      originalRequest._retry = true;

      // sessionStorage에서 refreshToken 가져오기
      const refreshToken = sessionStorage.getItem("refreshToken");

      if (!refreshToken) {
        console.error("No refresh token found");
        return Promise.reject(error);
      }

      try {
        // 리프레시 토큰을 이용해 새로운 액세스 토큰 요청
        const response = await api.post(
          "/api/refresh-token", // 상대경로로 변경
          {
            refreshToken,
          },
          {
            headers: {
              "Content-Type": "application/json",
            },
          }
        );

        const newAccessToken = response.data.accessToken;

        // 새로운 액세스 토큰을 세션 스토리지에 저장
        sessionStorage.setItem("accessToken", newAccessToken);

        // 실패했던 요청에 새로운 액세스 토큰을 추가하고 재시도
        originalRequest.headers["Authorization"] = `Bearer ${newAccessToken}`;
        return api(originalRequest);
      } catch (refreshError) {
        console.error("No refresh token found. Redirecting to login...");
        window.location.href = "/login"; // 로그인 페이지로 리다이렉트
        return Promise.reject(error);
      }
    }
    return Promise.reject(error);
  }
);

export default api;
