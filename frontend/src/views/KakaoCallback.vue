<template>
  <div>
    <p>카카오 로그인 처리 중...</p>
  </div>
</template>

<script>
import axios from "@/api/noTokenAxiosInstance";

export default {
  name: "KakaoCallback",
  async created() {
    // URL에서 'code' 파라미터 가져오기
    const urlParams = new URLSearchParams(window.location.search);
    const code = urlParams.get("code");
    console.log(code);

    if (code) {
      try {
        // 백엔드에 'code'를 전달하여 토큰 발급 요청
        const response = await axios.post(
          "http://192.168.210.52:8080/auth/kakao/callback",
          { code }
        );

        const { accessToken, refreshToken } = response.data;

        // Access Token과 Refresh Token 저장
        sessionStorage.setItem("accessToken", accessToken);
        sessionStorage.setItem("refreshToken", refreshToken);

        alert("카카오 로그인 성공!");
        this.$router.push("/"); // 로그인 성공 시 메인 페이지로 리다이렉트
      } catch (error) {
        console.error("카카오 로그인 실패", error);
        alert("로그인 처리 중 오류가 발생했습니다.");
        this.$router.push("/user/login"); // 로그인 실패 시 로그인 페이지로 리다이렉트
      }
    } else {
      alert("인증 코드가 없습니다.");
      this.$router.push("/user/login"); // 'code'가 없으면 로그인 페이지로 리다이렉트
    }
  },
};
</script>
