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
        <h2 class="modal-title">📃 채팅 목록</h2>

        <ul v-show="chatRooms.length !== 0" class="chat-rooms">
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
        <div class="chat-none">
          <p
            v-if="chatRooms.length === 0"
            class="empty-message"
            style="margin-bottom: 120px"
          >
            채팅방이 없습니다. 새로 생성해보세요!
          </p>
        </div>

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
        <h2 class="modal-title">새로운 채팅</h2>
        <input
          v-model="newChatRoomTitle"
          placeholder="채팅방 제목을 입력하세요"
          class="chat-room-input"
          @input="onInputChange"
        />
        <div class="create-room-buttons">
          <button class="create-room-button" @click="createChatRoom">
            생성하기
          </button>
          <button class="close-button" @click="closeCreateRoomModal">
            닫기
          </button>
        </div>
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

    <!-- 채팅 모달 -->
    <div
      v-if="showChatRoomModal"
      class="chat-room-modal-overlay"
      @click.self="closeChatRoomModal"
    >
      <div class="chat-room-modal">
        <h2 class="modal-title">{{ selectedPlace.name }} 채팅방</h2>
        <h3 class="modal-title">{{ selectedChatRoom?.title }}</h3>
        <ul class="chat-messages">
          <li
            v-for="(message, index) in messages"
            :key="index"
            :class="[
              'message',
              message.userId === sessionStorage.getItem('userId') ? 'self' : '',
            ]"
          >
            <img
              v-if="message.userProfile"
              :src="message.userProfile"
              class="message-profile"
            />
            <div class="message-content">
              <span class="nickname">{{ message.userNickname }}</span>
              <p>{{ message.message }}</p>
            </div>
          </li>
        </ul>

        <div class="chat-input">
          <input
            v-model="newChatMessage"
            placeholder="메시지를 입력하세요"
            @keydown.enter="sendMessage(selectedChatRoomId, newChatMessage)"
          />
          <button @click="sendMessage(selectedChatRoomId, newChatMessage)">
            전송
          </button>
        </div>
      </div>
    </div>
    <MainFooter />
  </div>
</template>

<script>
import ncapi from "@/api/noTokenAxiosInstance";
import MainHeader from "@/components/module/MainHeader.vue";
import MainFooter from "@/components/module/MainFooter.vue";
import api from "@/api/axiosInstance"; // 인증이 필요한 요청을 위해 추가
import WebSocketService from "@/services/WebSocketService";
import { reactive } from "vue";

export default {
  data() {
    return {
      currentUser: null,
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
      defaultProfile: "/img/default-profile.png", // 기본 프로필 이미지 경로
      showEnterRoomModal: false, // 입장 모달 표시 여부
      selectedChatRoomId: null, // 선택된 채팅방 ID
      selectedChatRoom: null, // 선택된 채팅방 정보
      showChatRoomModal: false, // 채팅방 모달 표시 여부
      chatRoomMessages: [], // 해당 채팅방의 메시지 목록
      newChatMessage: "", // 새로운 메시지 입력 필드 값
      messages: {},
      chatRoomsByPlace: reactive({}), // 장소별 채팅방 데이터를 저장
      selectedPlaceId: null, // 선택된 장소 ID
      isConnected: false,
    };
  },
  async mounted() {
    try {
      await this.getCurrentUser(); // 사용자 정보 가져오기 완료 대기
      this.getUserLocation(); // 위치 정보 가져오기
      WebSocketService.connect(this.onConnected, this.onError); // WebSocket 연결
    } catch (error) {
      console.error("초기화 중 오류 발생:", error);
    }
  },

  methods: {
    onConnected() {
      if (this.selectedChatRoomId) {
        console.log("WebSocket 연결 성공");
        this.isConnected = true;
        WebSocketService.subscribe(
          `/topic/chatRoom/${this.selectedChatRoomId}`,
          this.onMessageReceived
        );
      } else {
        console.error("선택된 채팅방 ID가 없습니다.");
      }
    },
    onError(error) {
      console.error("WebSocket 연결 오류:", error);
      this.isConnected = false;
    },
    async getCurrentUser() {
      try {
        const response = await api.get(`/api/user/current`); // 사용자 정보 API 호출
        this.currentUser = response.data; // 사용자 정보 저장
        console.log("현재 사용자 정보:", this.currentUser);
      } catch (error) {
        console.error("사용자 정보를 가져오는 중 오류 발생:", error);
        alert("로그인 정보가 없습니다. 다시 로그인 해주세요.");
        this.$router.push("/login"); // 로그인 페이지로 이동
      }
    },
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
      console.log("채팅방 클릭: ", chatRoomId);
      this.selectedChatRoomId = chatRoomId; // 선택된 채팅방 ID 설정
      this.showEnterRoomModal = true; // 입장 여부 모달 열기
      this.showChatRoomModal = false; // 채팅 모달은 닫힌 상태 유지
    },

    // 채팅방 입장 모달 닫기
    closeEnterRoomModal() {
      this.showEnterRoomModal = false;
      this.selectedChatRoomId = null;
    },
    // 채팅방 입장 확인
    // 채팅방 입장 확인
    async confirmEnterChatRoom() {
      if (!this.selectedChatRoomId) {
        alert("선택된 채팅방 ID가 없습니다.");
        return;
      }

      if (!this.currentUser || !this.currentUser.userId) {
        alert("사용자 정보가 없습니다. 다시 로그인 해주세요.");
        return;
      }

      try {
        WebSocketService.subscribe(
          `/topic/chatRoom/${this.selectedChatRoomId}`,
          this.onMessageReceived
        );

        this.showEnterRoomModal = false; // 입장 모달 닫기
        this.showChatRoomModal = true; // 채팅 모달 열기
        await this.loadChatRoomMessages(this.selectedChatRoomId);
      } catch (error) {
        console.error("채팅방 입장 처리 중 오류:", error);
        alert("채팅방 입장에 실패했습니다.");
      }
    },

    onMessageReceived(payload) {
      const message = JSON.parse(payload.body);
      console.log("수신한 메시지:", message);

      // chatRoomId 기반 메시지 저장
      const chatRoomId = message.chatRoomId;
      if (!this.messages[chatRoomId]) {
        this.messages[chatRoomId] = [];
      }
      this.messages[chatRoomId].unshift(message);
    },
    closeChatRoomModal() {
      this.showChatRoomModal = false;
      this.chatRoomMessages = [];
      this.selectedChatRoom = null;
    },
    getChatRoomsForPlace(placeId) {
      // 해당 장소의 채팅방 목록 반환
      return this.chatRoomsByPlace[placeId] || [];
    },
    async loadChatRoomMessages(chatRoomId) {
      try {
        const response = await api.get(`/api/chat-room/${chatRoomId}/history`);
        this.chatRoomMessages = response.data || []; // 메시지 데이터가 없으면 빈 배열 설정
      } catch (error) {
        console.error("채팅 메시지 로드 실패:", error);
        alert("채팅 메시지를 불러오는 중 오류가 발생했습니다.");
        this.chatRoomMessages = []; // 오류 발생 시 초기화
      }
    },

    // 메시지 전송
    sendMessage(chatRoomId, message) {
      // currentUser가 정의되어 있는지 확인
      if (!this.currentUser || !this.currentUser.userId) {
        alert("사용자 정보가 없습니다. 다시 로그인 해주세요.");
        return;
      }

      if (!message.trim()) {
        alert("메시지를 입력하세요.");
        return;
      }

      const chatMessage = {
        chatRoomId,
        userId: this.currentUser.userId,
        message,
        userNickname: this.currentUser.userNickname || "익명",
        userProfile: this.currentUser.userProfile || this.defaultProfile,
      };

      WebSocketService.send(`/app/sendMessage/${chatRoomId}`, chatMessage);

      if (!this.messages[chatRoomId]) {
        this.messages[chatRoomId] = [];
      }
      this.messages[chatRoomId].unshift(chatMessage);
      this.newChatMessage = ""; // 입력창 초기화
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
          console.log("Marker 클릭 - placeId:", place.id); // 디버깅용 로그
          this.selectedPlace = {
            id: place.id,
            name: place.place_name,
            address:
              place.road_address_name || place.address_name || "주소 정보 없음",
          };
          this.selectedPlaceId = place.id;
          this.loadChatRooms(place.id);
        });

        this.markers.push(marker);
      });
    },

    async loadChatRooms(placeId) {
      console.log("loadChatRooms 호출 - placeId:", placeId); // 디버깅용 로그
      try {
        const response = await api.get(`/api/chat-rooms`, {
          params: { placeId },
        });
        console.log("API 응답 데이터:", response.data); // 응답 확인

        if (Array.isArray(response.data)) {
          // 장소별 채팅방 목록 저장
          this.chatRoomsByPlace = {
            ...this.chatRoomsByPlace,
            [placeId]: response.data.map((room) => ({
              ...room,
              creatorProfile: room.userProfile || this.defaultProfile,
              creatorNickname: room.userNickname || "익명",
            })),
          };
        } else {
          console.error("응답 데이터가 배열이 아닙니다:", response.data);
          this.chatRoomsByPlace = {
            ...this.chatRoomsByPlace,
            [placeId]: [], // 비어있는 배열로 초기화
          };
        }
      } catch (error) {
        console.error("채팅방 목록 로드 실패 - API 요청 에러:", error);
        alert("채팅방 목록을 불러오지 못했습니다.");
        this.chatRoomsByPlace = {
          ...this.chatRoomsByPlace,
          [placeId]: [], // 오류 발생 시 초기화
        };
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
        // 채팅방 생성 API 호출
        const response = await api.post("/api/chat-room", {
          title: this.newChatRoomTitle,
          placeId: this.selectedPlace.id,
        });

        // 3번: 서버 응답 데이터를 로컬에 즉시 반영
        const newRoom = {
          ...response.data,
          creatorProfile: response.data.userProfile || this.defaultProfile, // 프로필
          creatorNickname: response.data.userNickname || "익명", // 닉네임
        };
        this.chatRooms.unshift(newRoom); // 즉시 목록에 추가

        // 2번: 서버에서 전체 목록 재요청 (최신 상태 보장)
        await this.loadChatRooms(this.selectedPlace.id);

        // 초기화 및 알림
        this.newChatRoomTitle = ""; // 입력 필드 초기화
        this.showCreateRoomModal = false; // 모달 닫기
        alert("채팅방 생성 성공!");
      } catch (error) {
        console.error("채팅방 생성 실패:", error);
        alert("채팅방 생성 중 문제가 발생했습니다.");
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
    getMessagesForChatRoom(chatRoomId) {
      return this.messages[chatRoomId] || [];
    },
  },
  components: {
    MainHeader,
    MainFooter,
  },
};
</script>

<style scoped>
.chat-none {
  display: flex;
  align-items: center;
  flex: 1;
}
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
  width: 20px;
  height: 20px;
  border-radius: 50%;
  margin-right: 5px;
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
  margin-bottom: 40px;
  text-align: center;
}

.modal-address {
  font-size: 1rem;
  color: gray;
  margin-bottom: 20px;
}

.modal-overlay {
  display: flex;
  justify-content: center;
  align-items: center;
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1000; /* 채팅 모달보다 낮은 값 */
}

/* 기본 모달 스타일 */
.place-info-modal,
.chat-list-modal {
  position: absolute;
  top: 60%;
  right: 5;
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
  flex: 1;
  width: 100%;
  max-height: 300px;
  overflow-y: auto;
  margin: 0;
  padding: 0;
  margin-bottom: 15px;
}

.chat-room-item {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  margin-bottom: 15px;
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
  text-align: center;
  margin-top: auto;
  box-sizing: border-box;
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
  font-size: 1.1rem;
  font-weight: bold;
  margin: 0;
  color: #333;
}

.chat-room-details {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chat-room-creator {
  font-size: 0.8rem;
  color: black;
  margin-left: 0;
  font-weight: bold;
}

.chat-room-last {
  font-size: 0.8rem;
  color: gray;
  margin-left: auto;
  margin-bottom: 15px;
}

.chat-room-input {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
  width: 94%;
}

empty-message {
  text-align: start;
  color: #666;
  margin: 20px;
  flex: 1;
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

/* 생성하기 및 닫기 버튼 Flex 정렬 */
.create-room-buttons {
  display: flex;
  justify-content: space-between;
  width: 100%; /* 버튼 컨테이너 너비를 입력 폼과 맞춤 */
  gap: 10px; /* 버튼 간격 */
  margin-top: 20px; /* 위아래 간격 */
}

.create-room-buttons button {
  flex: 1; /* 버튼 너비를 동일하게 설정 */
  padding: 10px; /* 버튼 내부 여백 */
  font-size: 1rem; /* 텍스트 크기 */
  font-weight: bold; /* 텍스트 굵기 */
  border-radius: 5px; /* 모서리 둥글게 */
  cursor: pointer; /* 포인터 커서 */
}

.create-room-buttons .create-room-button {
  background: #007bff;
  color: white;
  border: none;
}

.create-room-buttons .create-room-button:hover {
  background: #0056b3;
}

.create-room-buttons .close-button {
  background: none;
  border: 1px solid #ff9c4a;
  color: #ff9c4a;
}

.create-room-buttons .close-button:hover {
  background: #ff9c4a;
  color: white;
}

.chat-room-modal-overlay {
  display: flex;
  justify-content: center;
  align-items: center;
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.7);
  z-index: 1100; /* 입장 확인 모달보다 높은 값 */
}

.chat-room-modal {
  width: 70%;
  height: 80%;
  background: white;
  border-radius: 10px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.chat-messages {
  display: flex;
  flex-direction: column-reverse;
  overflow-y: auto;
  margin-bottom: 20px;
  height: 100%;
}

.chat-input {
  display: flex;
  gap: 10px;
}

.chat-input input {
  flex: 1;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
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
