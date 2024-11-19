import axios from "axios";

// Kakao API 키
const KAKAO_API_KEY = "YOUR_KAKAO_REST_API_KEY"; // 실제 키로 변경하세요

// 장소 검색 함수
export const searchPlaces = async (query, location, radius = 5000) => {
  const { lat, lng } = location;
  const url = `https://dapi.kakao.com/v2/local/search/keyword.json`;
  
  try {
    const response = await axios.get(url, {
      headers: {
        Authorization: `KakaoAK ${KAKAO_API_KEY}`,
      },
      params: {
        query,
        x: lng,
        y: lat,
        radius,
      },
    });
    return response.data.documents; // 장소 데이터 반환
  } catch (error) {
    console.error("Error fetching places:", error);
    return [];
  }
};
