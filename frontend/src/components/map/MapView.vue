<template>
  <div>
    <MainHeader />
    <!-- 주소 검색 -->
    <div id="address-search" class="search-bar">
      <input
        type="text"
        placeholder="주소를 검색하세요"
        class="search-input"
        v-model="placeQuery"
        @keydown.enter="searchPlace"
      />
      <button class="search-button" @click="searchPlace">검색</button>
    </div>

    <!-- 반경 설정 -->
    <div class="controls">
      <label for="radius" class="radius-label">반경 설정:</label>
      <select
        id="radius"
        v-model="radius"
        @change="updateRadius"
        class="radius-select"
      >
        <option value="1000">1km</option>
        <option value="3000">3km</option>
        <option value="5000">5km</option>
      </select>
    </div>

    <!-- 장소 필터 버튼 -->
    <div class="filter-buttons">
      <button @click="filterPlaces('전체')" class="filter-button filter-all">
        전체
      </button>
      <button @click="filterPlaces('헬스장')" class="filter-button filter-gym">
        헬스장
      </button>
      <button
        @click="filterPlaces('클라이밍')"
        class="filter-button filter-climbing"
      >
        클라이밍
      </button>
      <button @click="filterPlaces('공원')" class="filter-button filter-park">
        공원
      </button>
    </div>

    <!-- 지도 -->
    <div id="map" class="map-container"></div>

    <!-- 장소 정보 모달 -->
    <div v-if="selectedPlace" class="modal-overlay" @click.self="closeModal">
      <div class="place-info-modal">
        <h2 class="modal-title">{{ selectedPlace.name }}</h2>
        <p class="modal-address">{{ selectedPlace.address }}</p>
      </div>

      <!-- 채팅 목록 모달 -->
      <div class="chat-list-modal">
        <h2 class="modal-title">채팅 목록</h2>
        <ul class="chat-rooms">
          <li
            v-for="room in chatRooms"
            :key="room.chatRoomId"
            class="chat-room-item"
            @click="openEnterRoomModal(room.chatRoomId)"
          >
            <div class="chat-room-info">
              <div class="chat-room-header">
                <p class="chat-room-title">{{ room.title }}</p>
              </div>
              <div class="chat-room-details">
                <img
                  :src="room.creatorProfile || defaultProfile"
                  alt="프로필 이미지"
                  class="profile-img"
                />
                <p class="chat-room-creator">
                  {{ room.creatorNickname ? room.creatorNickname : "익명" }}
                </p>
                <p class="chat-room-last">
                  {{ formatDate(room.lastMessageAt) }}
                </p>
              </div>
            </div>
          </li>
        </ul>

        <p v-if="chatRooms.length === 0" class="empty-message">
          채팅방이 없습니다. 새로 생성해보세요!
        </p>
        <button class="create-room-button" @click="openCreateRoomModal">
          채팅방 만들기
        </button>
      </div>
    </div>

    <!-- 채팅방 생성 모달 -->
    <div
      v-if="showCreateRoomModal"
      class="create-room-modal-overlay"
      @click.self="closeCreateRoomModal"
    >
      <div class="create-room-modal">
        <h2 class="modal-title">채팅방 만들기</h2>
        <input
          v-model="newChatRoomTitle"
          placeholder="채팅방 제목을 입력하세요"
          class="chat-room-input"
          @input="onInputChange"
        />
        <button class="create-room-button" @click="createChatRoom">
          생성하기
        </button>
        <button class="close-button" @click="closeCreateRoomModal">닫기</button>
      </div>
    </div>
    <!-- 채팅방 입장 확인 모달 -->
    <div
      v-if="showEnterRoomModal"
      class="modal-overlay"
      @click.self="closeEnterRoomModal"
    >
      <div class="modal">
        <p>채팅방에 입장하시겠습니까?</p>
        <div class="modal-buttons">
          <button @click="confirmEnterChatRoom">확인</button>
          <button @click="closeEnterRoomModal">취소</button>
        </div>
      </div>
    </div>
  </div>
  <MainFooter />
</template>

<script>
import ncapi from "@/api/noTokenAxiosInstance";
import MainHeader from "@/components/module/MainHeader.vue";
import MainFooter from "@/components/module/MainFooter.vue";
import api from "@/api/axiosInstance"; // 인증이 필요한 요청을 위해 추가

export default {
  data() {
    return {
      map: null,
      markers: [],
      centerLat: null,
      centerLng: null,
      radius: 3000,
      currentKeyword: "전체",
      selectedPlace: null,
      placeQuery: "",
      chatRooms: [],
      showCreateRoomModal: false,
      newChatRoomTitle: "",
      defaultProfile: "/assets/default-profile.png", // 기본 프로필 이미지 경로
      showEnterRoomModal: false, // 입장 모달 표시 여부
      selectedChatRoomId: null, // 선택된 채팅방 ID
    };
  },
  mounted() {
    this.getUserLocation();
  },
  methods: {
    getUserLocation() {
      if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
          (position) => {
            this.centerLat = position.coords.latitude;
            this.centerLng = position.coords.longitude;
            this.initMap();
          },
          (error) => {
            console.error("Geolocation 실패:", error);
            this.centerLat = 37.5665; // Default: 서울
            this.centerLng = 126.978;
            this.initMap();
          }
        );
      }
    },
    // 채팅방 클릭 시 모달 열기
    openEnterRoomModal(chatRoomId) {
      this.selectedChatRoomId = chatRoomId;
      this.showEnterRoomModal = true;
    },
    // 채팅방 입장 모달 닫기
    closeEnterRoomModal() {
      this.showEnterRoomModal = false;
      this.selectedChatRoomId = null;
    },
    // 채팅방 입장 확인
    confirmEnterChatRoom() {
      this.showEnterRoomModal = false;
      this.enterChatRoom(this.selectedChatRoomId);
    },
    initMap() {
      const mapContainer = document.getElementById("map");
      const mapOption = {
        center: new kakao.maps.LatLng(this.centerLat, this.centerLng),
        level: this.getZoomLevel(this.radius),
      };
      this.map = new kakao.maps.Map(mapContainer, mapOption);
      this.lockMap();
      this.searchPlaces();
    },
    lockMap() {
      if (this.map) {
        this.map.setDraggable(false);
        this.map.setZoomable(false);
      }
    },
    closeModal() {
      this.selectedPlace = null;
    },
    searchPlaces() {
      const ps = new kakao.maps.services.Places();
      const searchOption = {
        location: new kakao.maps.LatLng(this.centerLat, this.centerLng),
        radius: this.radius,
      };
      this.clearMarkers();
      const keywords =
        this.currentKeyword === "전체"
          ? ["헬스장", "클라이밍", "공원"]
          : [this.currentKeyword];
      keywords.forEach((keyword) => {
        ps.keywordSearch(
          keyword,
          (data, status) => {
            if (status === kakao.maps.services.Status.OK) {
              this.displayMarkers(data);
            } else {
              console.error(`${keyword} 검색 실패: ${status}`);
            }
          },
          searchOption
        );
      });
    },
    filterPlaces(keyword) {
      this.currentKeyword = keyword;
      this.searchPlaces();
    },
    searchPlace() {
      if (!this.placeQuery.trim()) {
        alert("장소를 입력하세요.");
        return;
      }
      const ps = new kakao.maps.services.Places();
      ps.keywordSearch(this.placeQuery, (data, status) => {
        if (status === kakao.maps.services.Status.OK) {
          const firstPlace = data[0];
          this.centerLat = parseFloat(firstPlace.y);
          this.centerLng = parseFloat(firstPlace.x);
          this.map.setCenter(
            new kakao.maps.LatLng(this.centerLat, this.centerLng)
          );
          this.searchPlaces();
        } else {
          alert("장소를 찾을 수 없습니다.");
        }
      });
    },
    clearMarkers() {
      this.markers.forEach((marker) => marker.setMap(null));
      this.markers = [];
    },
    updateRadius() {
      if (this.map) {
        const zoomLevel = this.getZoomLevel(this.radius);
        this.map.setLevel(zoomLevel);
        this.searchPlaces();
      }
    },
    getZoomLevel(radius) {
      if (radius <= 1000) return 4;
      if (radius <= 3000) return 5;
      return 6;
    },
    displayMarkers(places) {
      places.forEach((place) => {
        const markerPosition = new kakao.maps.LatLng(place.y, place.x);
        const marker = new kakao.maps.Marker({
          position: markerPosition,
          map: this.map,
        });

        kakao.maps.event.addListener(marker, "click", () => {
          this.selectedPlace = {
            id: place.id,
            name: place.place_name,
            address:
              place.road_address_name || place.address_name || "주소 정보 없음",
          };
          this.loadChatRooms(place.id);
        });

        this.markers.push(marker);
      });
    },
    async loadChatRooms(placeId) {
      try {
        const response = await ncapi.get(`/api/chat-rooms`, {
          params: { placeId },
        });
        if (Array.isArray(response.data)) {
          this.chatRooms = response.data.map((room) => ({
            ...room,
            creatorProfile: room.creatorProfile || this.defaultProfile, // 기본 이미지 설정
          }));
        } else {
          console.error("응답 데이터가 배열이 아닙니다:", response.data);
          this.chatRooms = [];
        }
      } catch (error) {
        console.error("채팅방 목록 로드 실패:", error);
        alert("채팅방 목록을 불러오지 못했습니다.");
      }
    },
    openCreateRoomModal() {
      this.showCreateRoomModal = true;
    },
    closeCreateRoomModal() {
      this.showCreateRoomModal = false;
      this.newChatRoomTitle = "";
    },
    async createChatRoom() {
      if (!this.newChatRoomTitle.trim()) {
        alert("채팅방 제목을 입력하세요.");
        return;
      }
      if (!this.selectedPlace || !this.selectedPlace.id) {
        alert("채팅방을 연결할 장소를 선택하세요.");
        return;
      }
      try {
        const token = sessionStorage.getItem("accessToken");
        if (!token) {
          alert("로그인이 필요합니다.");
          this.$router.push("/login");
          return;
        }

        const response = await api.post("/api/chat-room", {
          title: this.newChatRoomTitle,
          placeId: this.selectedPlace.id,
        });
        this.chatRooms.unshift({
          ...response.data,
          creatorProfile: response.data.creatorProfile || this.defaultProfile,
        });
        this.newChatRoomTitle = "";
        this.showCreateRoomModal = false;
        alert("채팅방 생성 성공!");
      } catch (error) {
        console.error("채팅방 생성 실패:", error);
        if (error.response && error.response.status === 403) {
          alert("권한이 없습니다. 로그인 상태를 확인해주세요.");
        } else {
          alert("채팅방 생성에 실패했습니다.");
        }
      }
    },
    async enterChatRoom(chatRoomId) {
      const token = sessionStorage.getItem("accessToken");
      if (!token) {
        alert("로그인이 필요합니다.");
        this.$router.push("/login"); // 로그인 페이지로 이동
        return;
      }
      try {
        await api.post(`/api/chat-room/${chatRoomId}/users`, {});
        this.$router.push(`/chat-room/${chatRoomId}`);
      } catch (error) {
        console.error("채팅방 입장 실패:", error);
        alert("채팅방 입장에 실패했습니다.");
      }
    },
    formatDate(date) {
      if (!date) return "시간 없음";
      const d = new Date(date);
      return `${d.getFullYear()}-${
        d.getMonth() + 1
      }-${d.getDate()} ${d.getHours()}:${d.getMinutes()}`;
    },
  },
  components: {
    MainHeader,
    MainFooter,
  },
};
</script>

<style>
/* 검색 바 스타일 */
.search-bar {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  margin: 30px auto 15px auto;
  text-align: center;
}

.search-input {
  width: 55%;
  padding: 12px;
  font-size: 16px;
  border: 2px solid #ff9c4a;
  border-radius: 25px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  outline: none;
}

.search-input:focus {
  border-color: #ff7d29;
  outline: none;
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.2);
}

.search-button {
  padding: 14px 26px;
  font-size: 16px;
  font-weight: bold;
  color: white;
  background: linear-gradient(45deg, #ff9c4a, #ff7d29);
  border: none;
  border-radius: 25px;
  cursor: pointer;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
  transition: transform 0.2s ease, box-shadow 0.3s ease;
}

.search-button:hover {
  background-color: #ff7d29;
  transform: scale(1.05);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.3);
}

.search-button:active {
  transform: translateY(2px);
  box-shadow: 0 3px 10px rgba(255, 125, 41, 0.2);
}

/* 반경 설정 */
.controls {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin: 20px auto;
  text-align: center;
}

.radius-label {
  font-weight: bold;
  margin-right: 7px;
  font-size: 16px;
}

.radius-select {
  padding: 8px 16px;
  font-size: 16px;
  border: 2px solid #ff9c4a;
  border-radius: 15px;
  background-color: white;
  cursor: pointer;
  transition: all 0.35 ease;
}

.radius-select:hover {
  border: 2px solid #ffbf78;
  background-color: #fffae6;
}

.select-menu {
  border-radius: 15px;
}

/* 장소 필터 버튼 */
.filter-buttons {
  text-align: center;
  margin-bottom: 30px;
  display: flex;
  justify-content: center;
  gap: 12px;
}

.filter-button {
  padding: 12px 25px;
  font-size: 16px;
  font-weight: bold;
  color: white;
  border: none;
  border-radius: 17px;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.3s ease;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
  background: linear-gradient(45deg, #ff9c4a, #ff7d29);
}

.filter-button:hover {
  background: linear-gradient(45deg, #ff9c4a, #ff7d29);
  transform: scale(1.05);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.3);
}

/* 지도 */
.map-container {
  width: 100%;
  height: 80vh;
  border: 1px solid #ddd;
  position: relative;
  z-index: 0;
}

.profile-img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 10px;
  object-fit: cover;
}

.place-info {
  text-align: center;
}

.modal {
  background: white;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
  text-align: center;
}

.modal-buttons {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-top: 10px;
}

.modal-buttons button {
  padding: 10px 20px;
  font-size: 14px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.modal-buttons button:first-child {
  background: #007bff;
  color: white;
}

.modal-buttons button:last-child {
  background: #ccc;
  color: black;
}

.modal-title {
  font-size: 1.5rem;
  font-weight: bold;
  margin-bottom: 10px;
  text-align: center;
}

.modal-address {
  font-size: 1rem;
  color: gray;
  margin-bottom: 20px;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

/* 기본 모달 스타일 */
.place-info-modal,
.chat-list-modal {
  position: absolute;
  top: 55%;
  transform: translate(-50%, -50%);
  background: white;
  border-radius: 10px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
  width: 20%;
  height: 50%;
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  z-index: 1000;
}
chat-room-inp .place-info-modal {
  left: 38%; /* 기본 위치 */
  text-align: center;
}

.chat-list-modal {
  left: 63%; /* 기본 위치 */
}

/* 반응형 스타일 */
@media (max-width: 768px) {
  .modal-overlay {
    display: flex;
    flex-direction: column; /* 위아래로 배치 */
    align-items: center; /* 가운데 정렬 */
    justify-content: center;
  }

  .place-info-modal,
  .chat-list-modal {
    position: static; /* flex 컨테이너 안에서 정렬 */
    transform: none; /* translate 제거 */
    width: 50%; /* 화면에 꽉 차게 */
    height: 25%; /* 높이 줄임 */
    margin-bottom: 20px; /* 모달 사이 간격 */
  }
}
.chat-list {
  text-align: center;
}

.chat-list-title {
  font-size: 1.8rem;
  font-weight: bold;
  margin-bottom: 15px;
}

.chat-rooms {
  width: 100%;
  max-height: 300px;
  overflow-y: auto;
  margin-right: 5px;
}

.chat-room-item {
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: flex-start;
  padding: 10px;
  margin-bottom: 10px;
  margin-right: 10px;
  background: #f9f9f9;
  border: 1px solid #ddd;
  border-radius: 5px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: background 0.3s;
}

.chat-room-item:hover {
  background: #fffae6;
}

.create-room-button {
  width: 100%;
  margin-top: 15px;
  padding: 10px;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.create-room-button:hover {
  background-color: #0056b3;
}

/* 채팅방 생성 모달 */
.create-room-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1100;
}

.create-room-modal {
  width: 400px;
  padding: 20px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.chat-room-info {
  display: flex;
  flex-direction: column;
  gap: 5px;
  width: 100%;
}

.chat-room-header {
  width: 100%;
}

.chat-room-title {
  font-size: 1.2rem;
  font-weight: bold;
  margin: 0;
  color: #333;
}

.chat-room-details {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  margin-top: 5px;
}

.chat-room-creator {
  font-size: 0.9rem;
  color: gray;
  margin-left: 10px;
  margin-bottom: 0;
}

.chat-room-last {
  font-size: 0.8rem;
  color: gray;
  margin-left: auto;
  margin-bottom: 0;
}

.chat-room-input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
}

.close-button {
  margin-top: 10px;
  background: none;
  border: 1px solid #ff9c4a;
  color: #ff9c4a;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
}

.close-button:hover {
  background: #ff9c4a;
  color: white;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes slideUp {
  from {
    transform: translateY(50px);
  }
  to {
    transform: translateY(0);
  }
}
</style>
