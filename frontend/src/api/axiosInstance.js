import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080", // 백엔드의 기본 API URL로 설정
  withCredentials: true,
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
  (error) => Promise.reject(error)
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

      const refreshToken = document.cookie
        .split("; ")
        .find((row) => row.startsWith("refreshToken="))
        ?.split("=")[1];

      if (!refreshToken) {
        console.error("No refresh token found");
        return Promise.reject(error);
      }

      try {
        // 리프레시 토큰을 이용해 새로운 액세스 토큰 요청
        const response = await api.post(
          "http://localhost:8080/api/refresh-token",
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
