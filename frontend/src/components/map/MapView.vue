```vue
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

    <!-- 장소 정보 및 채팅 목록 모달 -->
    <div v-if="selectedPlace" class="modal-overlay" @click.self="closeModal">
      <div class="place-info-modal">
        <h2 class="modal-title">{{ selectedPlace.name }}</h2>
        <p class="modal-address">{{ selectedPlace.address }}</p>
        <button class="ai-mission-button" @click="openAiMissionModal">
          가상 메이트 미션 받기
        </button>
      </div>
      <div class="chat-list-modal">
        <h2 class="modal-title">📃 채팅 목록</h2>
        <ul
          v-show="getChatRoomsForPlace(selectedPlace.id).length !== 0"
          class="chat-rooms"
        >
          <li
            v-for="room in getChatRoomsForPlace(selectedPlace.id)"
            :key="room.chatRoomId"
            class="chat-room-item"
            @click="openEnterRoomModal(room.chatRoomId)"
          >
            <div class="chat-room-info">
              <div class="chat-room-header">
                <p class="chat-room-title">{{ room.title }}</p>
                <div v-if="room.unreadCount > 0" class="unread-count">
                  {{ room.unreadCount }}
                </div>
              </div>
              <div class="chat-room-details">
                <img
                  src="@/assets/img/default-profile.png"
                  alt="프로필 이미지"
                  class="profile-img"
                />
                <p class="chat-room-creator">
                  {{ room.creatorNickname ? room.creatorNickname : "익명" }}
                </p>
                <p class="chat-room-last">
                  {{ room.lastAt ? formatDate(room.lastAt) : "시간 없음" }}
                </p>
              </div>
            </div>
          </li>
        </ul>
        <div class="chat-none">
          <p
            v-if="getChatRoomsForPlace(selectedPlace?.id).length === 0"
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

    <!-- 가상 메이트 미션 모달 -->
    <div
      v-if="showAiMissionModal"
      class="ai-modal-overlay"
      @click.self="closeAiMissionModal"
    >
      <div class="ai-modal">
        <!-- 캐릭터 이미지와 정보 -->
        <img
          :src="selectedCharacter?.image"
          alt="캐릭터 이미지"
          class="character-image"
        />
        <h3>{{ selectedCharacter?.name }}</h3>
        <p class="character-style">{{ selectedCharacter?.style }} 스타일</p>
        <p class="character-message">
          {{
            selectedCharacter?.messages[
              Math.floor(Math.random() * selectedCharacter.messages.length)
            ]
          }}
        </p>

        <!-- 추천 미션 -->
        <h4>추천 미션</h4>
        <div class="ai-mission">
          <i class="fas fa-flag mission-icon"></i>
          <span>{{ aiMission }}</span>
        </div>

        <!-- 모달 버튼 -->
        <button @click="completeMission" class="mission-complete-button">
          미션 완료!
        </button>
        <button @click="closeAiMissionModal" class="ai-close-button">
          닫기
        </button>
      </div>
    </div>

    <!-- 채팅 모달 -->
    <div
      v-if="showChatRoomModal"
      class="chat-room-modal-overlay"
      @click.self="closeChatRoomModal"
    >
      <div class="chat-room-modal">
        <div class="chat-room-header">
          <h2 class="modal-title">{{ selectedPlace.name }}</h2>
          <h3 class="modal-chat-title">{{ selectedChatRoom?.title }}</h3>
        </div>
        <div class="chat-messages" ref="messageContainer">
          <div
            v-for="(message, index) in getMessagesForChatRoom(
              selectedChatRoomId
            )"
            :key="index"
            :class="[
              'message-wrapper',
              message.userId === currentUser.userId ? 'self' : 'other',
              {
                'continuous-message':
                  index <
                    getMessagesForChatRoom(selectedChatRoomId).length - 1 &&
                  message.userId ===
                    getMessagesForChatRoom(selectedChatRoomId)[index + 1]
                      .userId,
              },
            ]"
          >
            <div class="message-group">
              <div
                v-if="
                  message.userId !== currentUser.userId &&
                  !(
                    index <
                      getMessagesForChatRoom(selectedChatRoomId).length - 1 &&
                    message.userId ===
                      getMessagesForChatRoom(selectedChatRoomId)[index + 1]
                        .userId
                  )
                "
                class="profile"
              >
                <img
                        src="@/assets/img/default-profile.png"
                  alt="프로필 이미지"
                  class="profile-img"
                />
              </div>
              <div class="message-content">
                <span
                  v-if="
                    message.userId !== currentUser.userId &&
                    !(
                      index <
                        getMessagesForChatRoom(selectedChatRoomId).length - 1 &&
                      message.userId ===
                        getMessagesForChatRoom(selectedChatRoomId)[index + 1]
                          .userId
                    )
                  "
                  class="nickname"
                >
                  {{ message.nickname || "익명" }}
                </span>
                <div class="message-bubble">
                  {{ message.message }}
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="chat-input">
          <input
            v-model="newChatMessage"
            placeholder="메시지를 입력하세요"
            @keydown.enter="sendMessage"
            :disabled="!isConnected"
          />
          <button @click="sendMessage" :disabled="!isConnected">전송</button>
        </div>
      </div>
    </div>

    <MainFooter />
  </div>
</template>

<script>
import MainHeader from "@/components/module/MainHeader.vue";
import MainFooter from "@/components/module/MainFooter.vue";
import api from "@/api/axiosInstance";
import ncapi from "@/api/noTokenAxiosInstance";
import WebSocketService from "@/services/WebSocketService";
import { reactive, toRaw } from "vue";
export default {
  components: {
    MainHeader,
    MainFooter,
  },
  data() {
    return {
      defaultUserImage: new URL("@/assets/img/default-profile.png", import.meta.url)
        .href,
      currentUser: null,
      map: null,
      markers: [],
      centerLat: null,
      centerLng: null,
      radius: 3000,
      currentKeyword: "전체",
      selectedPlace: null,
      placeQuery: "",
      chatRoomsByPlace: reactive({}),
      showCreateRoomModal: false,
      newChatRoomTitle: "",
      defaultProfile: "@/assets/img/default-profile.png",
      showEnterRoomModal: false,
      selectedChatRoomId: null,
      selectedChatRoom: null,
      showChatRoomModal: false,
      messages: {},
      newChatMessage: "",
      isConnected: false,
    
      characters: {
        헬스장: {
          name: "근육맨 메이트",
          image: "/images/gym-mate.png",
          style: "파워풀하고 열정적인",
          messages: [
            "근육이 기다리고 있어요! 오늘은 최고 기록을 도전해봐요!",
            "벤치프레스 50kg? 당신이라면 충분히 가능합니다!",
            "운동 후에는 단백질 쉐이크 잊지 마세요!",
          ],
        },
        클라이밍: {
          name: "모험가 메이트",
          image: "/images/climbing-mate.png",
          style: "도전적이고 창의적인",
          messages: [
            "오늘은 꼭 정상에 도달할 거예요!",
            "난이도 B 루트? 당신에게는 충분히 쉬운 도전이죠!",
            "그립을 잘 잡고, 천천히 올라가 보세요!",
          ],
        },
        공원: {
          name: "건강 지킴이 메이트",
          image: "/images/park-mate.png",
          style: "친절하고 응원하는",
          messages: [
            "산뜻한 공기를 마시며 건강을 지켜봐요!",
            "5km 런닝 도전! 당신이라면 할 수 있어요!",
            "오늘 하루도 산뜻하게, 몸도 마음도 가볍게!",
          ],
        },
      },
      // 선택된 캐릭터 정보
      selectedCharacter: null,

      // 장소별 미션 데이터
      missions: {
        헬스장: [
          "벤치프레스 50kg 10회",
          "스쿼트 3세트, 15회씩",
          "런닝머신 20분 (속도 8km/h)",
          "플랭크 1분 30초",
          "푸쉬업 30회",
          "풀업 10회",
          "레그프레스 3세트, 12회씩",
          "랫풀다운 50kg, 15회",
        ],
        클라이밍: [
          "3 루트 완주 도전",
          "난이도 B 루트 성공",
          "암벽 타기 15분 연속",
          "클라이밍 스트레칭 5분",
          "중급 루트 2개 완주",
        ],
        공원: [
          "5km 런닝 (목표 30분 이내)",
          "스트레칭 15분 (전신)",
          "1km 인터벌 런닝",
          "조깅 3km (페이스 유지)",
          "20분 동안 빠르게 걷기",
        ],
      },
      // 추천된 미션과 모달 상태
      aiMission: "",
      showAiMissionModal: false,
    };
  },
  async mounted() {
    try {
      await this.getCurrentUser();
      this.getUserLocation();
      WebSocketService.connect(this.onConnected, this.onError);
    } catch (error) {
      console.error("초기화 중 오류 발생:", error);
    }
  },
  beforeDestroy() {
    if (this.currentSubscription) {
      this.currentSubscription.unsubscribe();
    }
    WebSocketService.disconnect();
  },
  methods: {
    async getCurrentUser() {
      try {
        const response = await api.get(`/api/user/current`);
        this.currentUser = response.data;
        console.log("현재 사용자 정보:", this.currentUser);
      } catch (error) {
        console.error("사용자 정보를 가져오는 중 오류 발생:", error);
        alert("로그인 정보가 없습니다. 다시 로그인 해주세요.");
        this.$router.push("/login");
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
            this.centerLat = 37.5665;
            this.centerLng = 126.978;
            this.initMap();
          }
        );
      }
    },
    handleImageError(event) {
      event.target.src = new URL("@/assets/img/default-profile.png", import.meta.url).href;
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
          this.chatRoomsByPlace = { ...this.chatRoomsByPlace, [placeId]: [] };
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
    isContinuousMessage(index) {
      const messages = this.getMessagesForChatRoom(this.selectedChatRoomId);
      return index > 0 && messages[index - 1].userId === messages[index].userId;
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
        await api.post("/api/chat-room", {
          title: this.newChatRoomTitle,
          placeId: this.selectedPlace.id,
        });
        this.newChatRoomTitle = "";
        this.showCreateRoomModal = false;
        await this.loadChatRooms(this.selectedPlace.id);
      } catch (error) {
        console.error("채팅방 생성 실패:", error);
        alert("채팅방 생성 중 문제가 발생했습니다.");
      }
    },
    openEnterRoomModal(chatRoomId) {
      this.selectedChatRoomId = chatRoomId;
      this.showEnterRoomModal = true;
    },
    closeEnterRoomModal() {
      this.showEnterRoomModal = false;
      this.selectedChatRoomId = null;
    },
    async confirmEnterChatRoom() {
      if (
        !this.selectedChatRoomId ||
        !this.currentUser ||
        !this.currentUser.userId
      ) {
        alert("채팅방 입장에 실패했습니다.");
        return;
      }
      try {
        WebSocketService.subscribe(
          `/topic/chatRoom/${this.selectedChatRoomId}`,
          this.onMessageReceived
        );
        this.showEnterRoomModal = false;
        this.showChatRoomModal = true;
        await this.loadChatRoomMessages(this.selectedChatRoomId);
      } catch (error) {
        console.error("채팅방 입장 처리 중 오류:", error);
        alert("채팅방 입장에 실패했습니다.");
      }
    },
    async loadChatRoomMessages(chatRoomId) {
      try {
        const response = await api.get(`/api/chat-room/${chatRoomId}/history`);
        this.messages = {
          ...this.messages,
          [chatRoomId]: response.data || [],
        };
        console.log(this.messages)
      } catch (error) {
        console.error("채팅 메시지 로드 실패:", error);
        alert("채팅 메시지를 불러오는 중 오류가 발생했습니다.");
      }
    },
    onConnected() {
      console.log("WebSocket 연결 성공");
      this.isConnected = true;
      if (this.selectedChatRoomId) {
        WebSocketService.subscribe(
          `/topic/chatRoom/${this.selectedChatRoomId}`,
          this.onMessageReceived
        );
      }
    },
    onError(error) {
      console.error("WebSocket 연결 오류:", error);
      this.isConnected = false;
    },
    onMessageReceived(payload) {
      const message = JSON.parse(payload.body);
      const chatRoomId = message.chatRoomId;
      if (!this.messages[chatRoomId]) {
        this.messages[chatRoomId] = [];
      }
      this.messages[chatRoomId] = [message, ...this.messages[chatRoomId]];

      // chatRoomsByPlace의 lastAt 업데이트
      const placeId = this.selectedPlace?.id;
      const chatRoom = this.chatRoomsByPlace[placeId]?.find(
        (room) => room.chatRoomId === chatRoomId
      );
      if (chatRoom) {
        const serverDate = new Date(message.sendDate);
        const correctedDate = new Date(
          serverDate.getTime() + 9 * 60 * 60 * 1000
        ); // UTC -> KST
        chatRoom.lastAt = correctedDate.toISOString();
      }
    },
    sendMessage() {
      if (!this.newChatMessage.trim() || !this.isConnected) return;
      const message = {
        chatRoomId: this.selectedChatRoomId,
        userId: this.currentUser.userId,
        message: this.newChatMessage.trim(),
        userNickname:
          this.currentUser.nickname || this.currentUser.userNickname || "익명",
        userProfile:
          this.currentUser.profileUrl ||
          this.currentUser.userProfile ||
          this.defaultProfile,
      };
      WebSocketService.send(
        `/app/sendMessage/${this.selectedChatRoomId}`,
        message
      );

      const now = new Date();
      const placeId = this.selectedPlace.id;
      const chatRoom = this.chatRoomsByPlace[placeId]?.find(
        (room) => room.chatRoomId === this.selectedChatRoomId
      );
      if (chatRoom) {
        chatRoom.lastAt = now.toISOString(); // 현재 시간을 갱신
      }

      this.newChatMessage = "";
    },
    openAiMissionModal() {
      if (!this.selectedPlace) {
        alert("장소를 먼저 선택하세요.");
        return;
      }

      // 선택된 장소의 유형 확인
      const category = this.getPlaceCategory(this.selectedPlace.name);

      if (category) {
        // 미션 추천
        const missions = this.missions[category];
        this.aiMission = missions[Math.floor(Math.random() * missions.length)];

        // 캐릭터 정보 설정
        this.selectedCharacter = this.characters[category];

        // 모달 표시
        this.showAiMissionModal = true;
      } else {
        alert("해당 장소의 카테고리를 알 수 없습니다.");
      }
    },

    // 장소 이름에서 카테고리 판별
    getPlaceCategory(placeName) {
      // 헬스장 관련 키워드
      if (
        placeName.includes("헬스") ||
        placeName.includes("피트니스") ||
        placeName.includes("휘트니스") ||
        placeName.includes("짐") ||
        placeName.includes("핏") ||
        placeName.includes("팀") ||
        placeName.includes("GYM") ||
        placeName.includes("스포") ||
        placeName.includes("PT") ||
        placeName.includes("크로스핏")
      ) {
        return "헬스장";
      }

      // 클라이밍 관련 키워드
      if (placeName.includes("클라이밍") || placeName.includes("암벽"))
        return "클라이밍";

      // 공원 관련 키워드
      if (placeName.includes("공원")) return "공원";

      // 해당 카테고리 없음
      return null;
    },

    getMaxWidth(message) {
      const length = message.length;
      if (length <= 10) return "150px";
      if (length <= 30) return "300px";
      return "450px"; // 최대 길이 제한
    },

    completeMission() {
      alert(
        `${this.selectedCharacter?.name}: "훌륭합니다! 오늘도 목표를 달성했어요!"`
      );
      this.closeAiMissionModal();
    },

    closeAiMissionModal() {
      this.showAiMissionModal = false;
      this.aiMission = "";
      this.selectedCharacter = null;
    },

    closeChatRoomModal() {
      this.showChatRoomModal = false;
      this.selectedChatRoom = null;
    },
    closeModal() {
      this.selectedPlace = null;
      this.selectedPlaceId = null;
    },
    getChatRoomsForPlace(placeId) {
      return this.chatRoomsByPlace[placeId] || [];
    },
    getMessagesForChatRoom(chatRoomId) {
      return this.messages[chatRoomId] || [];
    },
    formatDate(date) {
      if (!date) return "시간 없음";

      const serverDate = new Date(date);

      // 9시간 감소 (서버 시간에서 9시간 이전으로 조정)
      const correctedDate = new Date(serverDate.getTime() - 9 * 60 * 60 * 1000); // 9시간 빼기

      // 원하는 포맷으로 시간 출력 (예: YYYY-MM-DD HH:mm)
      return `${correctedDate.getFullYear()}-${
        correctedDate.getMonth() + 1
      }-${correctedDate.getDate()} ${correctedDate.getHours()}:${correctedDate
        .getMinutes()
        .toString()
        .padStart(2, "0")}`;
    },
  },
};
</script>

<style scoped>
/* 기본 레이아웃 */
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
  max-width: 800px;
  padding: 0 20px;
}

.search-input {
  flex: 1;
  padding: 15px 24px;
  font-size: 16px;
  border: 2px solid rgba(255, 125, 41, 0.3);
  border-radius: 16px;
  background-color: white;
  transition: all 0.3s ease;
}

.search-input:focus {
  border-color: #ff7d29;
  box-shadow: 0 4px 12px rgba(255, 125, 41, 0.15);
  outline: none;
}

.search-button {
  padding: 15px 30px;
  font-size: 16px;
  font-weight: 600;
  color: white;
  background: linear-gradient(45deg, #ff7d29, #ffbf78);
  border: none;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(255, 125, 41, 0.25);
}

.search-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(255, 125, 41, 0.35);
}

/* 반경 설정 */
.controls {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
  margin: 20px auto;
}

.radius-label {
  font-weight: 600;
  font-size: 16px;
  color: #2d3748;
}

.radius-select {
  padding: 8px 20px;
  font-size: 15px;
  border: 2px solid rgba(255, 125, 41, 0.3);
  border-radius: 12px;
  background-color: white;
  cursor: pointer;
  transition: all 0.3s ease;
}

.radius-select:hover {
  border-color: #ffbf78;
  background-color: #fff8f3;
}

/* 필터 버튼 */
.filter-buttons {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin: 20px auto 30px;
  padding: 0 20px;
}

.filter-button {
  padding: 10px 24px;
  font-size: 15px;
  font-weight: 600;
  color: white;
  background: linear-gradient(45deg, #ff7d29, #ffbf78);
  border: none;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 4px 8px rgba(255, 125, 41, 0.15);
}

.filter-button:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(255, 125, 41, 0.25);
}

.filter-button:hover {
  background: linear-gradient(45deg, #ff8a3d, #ffc686);
  transform: scale(1.05);
}

/* 지도 컨테이너 */
.map-container {
  width: 100%;
  height: 75vh;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

/* 모달 오버레이 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(4px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

/* 장소 정보 & 채팅 목록 모달 */
.place-info-modal,
.chat-list-modal {
  position: fixed;
  top: 50%;
  transform: translateY(-50%);
  width: 400px;
  height: 600px;
  background: white;
  border-radius: 24px;
  padding: 30px;
  display: flex;
  flex-direction: column;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
  animation: slideUp 0.4s ease;
}

.place-info-modal {
  left: calc(50% - 500px);
  background: linear-gradient(145deg, #ffffff, #fff5ec);
}

.chat-list-modal {
  left: calc(50% + 20px);
  background: white;
}

.modal-title {
  font-size: 1.8rem;
  font-weight: 700;
  color: #2d3748;
  margin-bottom: 15px;
  text-align: center;
}

.modal-chat-title {
  font-size: 1.3rem;
  font-weight: 600;
  color: #2d3748;
  margin-bottom: 15px;
  text-align: center;
}

.modal-address {
  font-size: 1.1rem;
  color: #718096;
  margin-bottom: 25px;
  text-align: center;
  padding: 12px;
  background: rgba(255, 125, 41, 0.1);
  border-radius: 12px;
}

/* 채팅방 목록 */
.chat-rooms {
  flex: 1;
  overflow-y: auto;
  margin: 0;
  padding: 0 10px;
  min-height: 470px;
  max-height: 400px;
}

.chat-rooms::-webkit-scrollbar {
  width: 6px;
}

.chat-rooms::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 10px;
}

.chat-rooms::-webkit-scrollbar-thumb {
  background: #ff7d29;
  border-radius: 10px;
}

.chat-room-item {
  padding: 12px;
  margin-bottom: 12px;
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  cursor: pointer;
  transition: all 0.2s ease;
}

.chat-room-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(255, 125, 41, 0.15);
  border-color: #ffbf78;
}

.chat-room-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.chat-room-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: #2d3748;
}

.chat-room-details {
  display: flex;
  align-items: center;
  gap: 7px;
}

.chat-room-creator {
  font-size: 0.95rem;
  color: #4a5568;
  font-weight: 500;
}

.chat-room-last {
  font-size: 0.85rem;
  color: #718096;
  margin-left: auto;
}

/* 버튼 스타일 */
.create-room-button,
.ai-mission-button {
  width: 100%;
  padding: 16px;
  margin-top: auto;
  font-weight: 600;
  color: white;
  background: linear-gradient(45deg, #ff7d29, #ffbf78);
  border: none;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(255, 125, 41, 0.2);
}

.create-room-button:hover,
.ai-mission-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 125, 41, 0.3);
}

/* 채팅방 생성 모달 */
.create-room-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(4px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999; /* z-index 값을 더 높게 수정 */
}

.create-room-modal {
  width: 400px;
  padding: 30px;
  background: white;
  border-radius: 24px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
  animation: scaleIn 0.2s ease;
  display: flex;
  flex-direction: column;
}

.chat-room-input {
  width: calc(100% - 30px);
  padding: 15px;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  font-size: 1rem;
  transition: all 0.3s ease;
  margin-bottom: 20px;
  align-self: center;
}

.chat-room-input:focus {
  border-color: #ff7d29;
  box-shadow: 0 0 0 3px rgba(255, 125, 41, 0.1);
  outline: none;
}

.create-room-buttons {
  display: flex;
  gap: 12px;
  width: calc(100% - 30px);
  align-self: center;
}

.create-room-buttons button {
  flex: 1;
  padding: 14px;
  font-size: 1rem;
  font-weight: 600;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.create-room-buttons .create-room-button {
  background: linear-gradient(45deg, #ff7d29, #ffbf78);
  color: white;
  border: none;
  box-shadow: 0 4px 12px rgba(255, 125, 41, 0.2);
}

.create-room-buttons .close-button {
  background: white;
  color: #ff7d29;
  border: 2px solid #ff7d29;
}

.create-room-buttons button:hover {
  transform: translateY(-2px);
}

/* 채팅방 입장 확인 모달 */
.modal {
  background: white;
  padding: 30px;
  border-radius: 20px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
  width: 300px;
  text-align: center;
  animation: scaleIn 0.17s ease;
}

.modal p {
  font-size: 1.1rem;
  color: #2d3748;
  margin-bottom: 20px;
}

.modal-buttons {
  display: flex;
  justify-content: center;
  gap: 12px;
}

.modal-buttons button {
  padding: 12px 24px;
  font-size: 1rem;
  font-weight: 600;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.modal-buttons button:first-child {
  background: linear-gradient(45deg, #ff7d29, #ffbf78);
  color: white;
  border: none;
  box-shadow: 0 4px 12px rgba(255, 125, 41, 0.2);
}

.modal-buttons button:last-child {
  background: white;
  color: #ff7d29;
  border: 2px solid #ff7d29;
}

.modal-buttons button:hover {
  transform: translateY(-2px);
}

/* 채팅방 모달 */
.chat-room-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(4px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1200;
}

.chat-room-modal {
  width: 60%;
  max-width: 600px;
  height: 70vh;
  background: white;
  border-radius: 24px;
  display: flex;
  flex-direction: column;
  position: relative;
  animation: modalSlideUp 0.2s ease;
  padding: 0;
}

.chat-room-header {
  padding: 20px 25px;
  border-bottom: 1px solid #edf2f7;
  background: white;
  border-radius: 25px;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px 20px;
  background: #f8fafc;
  display: flex;
  flex-direction: column-reverse;
  margin-bottom: 80px;
}

/* 메시지 스타일 수정 */
.message-wrapper {
  display: flex;
  margin-bottom: 2px;
  max-width: 70%;
  animation: fadeIn 0.2s ease;
}

.message-wrapper.self {
  margin-left: auto;
  flex-direction: row-reverse;
}

/* 연속된 메시지일 때는 위쪽 마진만 아주 작게 */
.message-wrapper.continuous-message {
  margin-top: 1px;
}

/* 연속되지 않은 첫 메시지는 위쪽 마진을 더 크게 */
.message-wrapper:not(.continuous-message) {
  margin-top: 12px;
}

.message-content {
  display: flex;
  flex-direction: column;
}

/* 말풍선 스타일 조정 */
.message-bubble {
  padding: 8px 12px;
  border-radius: 12px;
  font-size: 0.95rem;
  line-height: 1.4;
  max-width: 100%;
  word-break: break-word;
}

/* 연속된 메시지의 말풍선 모서리 조정 */
.message-wrapper.self .message-bubble {
  border-top-right-radius: 12px;
  background: linear-gradient(45deg, #ff7d29, #ffbf78);
  color: white;
  border-top-right-radius: 0; /* 오른쪽 위 모서리만 뾰족하게 */
}

.message-wrapper.self.continuous-message .message-bubble {
  border-top-right-radius: 12px;
}

.message-wrapper.other .message-bubble {
  border-top-left-radius: 12px;
  border-top-left-radius: 0; /* 왼쪽 위 모서리만 뾰족하게 */
  background: white;
  color: #2d3748;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.message-wrapper.other.continuous-message .message-bubble {
  border-top-left-radius: 12px;
  margin-left: 48px;
}

.nickname {
  font-size: 0.8rem;
  color: #718096;
  margin-bottom: 2px;
}

.profile {
  margin-right: 8px;
  width: 40px;
}

.profile-img {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
}

.chat-input {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 10px 30px;
  background: white;
  border-top: 1px solid #edf2f7;
  display: flex;
  gap: 12px;
  align-items: center;
  height: 80px;
  box-sizing: border-box;
  border-radius: 25px;
}

.chat-input input {
  flex: 1;
  padding: 12px 20px;
  border: 1px solid #e2e8f0;
  border-radius: 20px;
  font-size: 0.95rem;
  transition: all 0.2s ease;
  background: #f8fafc;
  height: 20px;
}

.chat-input input:focus {
  outline: none;
  border-color: #ff7d29;
  background: white;
  box-shadow: 0 0 0 3px rgba(255, 125, 41, 0.1);
}

.chat-input button {
  padding: 12px 24px;
  background: linear-gradient(45deg, #ff7d29, #ffbf78);
  color: white;
  border: none;
  border-radius: 20px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  min-width: 80px;
  height: 45px;
}

.chat-input button:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(255, 125, 41, 0.2);
}

.chat-input button:disabled {
  background: #e2e8f0;
  cursor: not-allowed;
}

/* 스크롤바 스타일 */
.chat-messages::-webkit-scrollbar {
  width: 5px;
}

.chat-messages::-webkit-scrollbar-track {
  background: #f1f1f1;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: #ff7d29;
  border-radius: 10px;
}

/* 애니메이션 */
@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(-50%);
  }
}

@keyframes scaleIn {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

@keyframes modalSlideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 반응형 */
@media (max-width: 1200px) {
  .place-info-modal,
  .chat-list-modal {
    width: 360px;
  }

  .place-info-modal {
    left: calc(50% - 380px);
  }

  .chat-list-modal {
    left: calc(50% + 20px);
  }

  .chat-room-modal {
    width: 70%;
  }
}

@media (max-width: 900px) {
  .place-info-modal,
  .chat-list-modal {
    position: fixed;
    width: 90%;
    max-width: 400px;
    left: 50%;
    transform: translate(-50%, -50%);
  }

  .place-info-modal {
    top: 30%;
  }

  .chat-list-modal {
    top: 70%;
  }

  .chat-room-modal {
    width: 90%;
    height: 80vh;
  }

  .message-wrapper {
    max-width: 85%;
  }
}

/* AI 미션 모달 스타일 */
.ai-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(rgba(0, 0, 0, 0.6), rgba(0, 0, 0, 0.2));
  backdrop-filter: blur(5px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.ai-modal {
  width: 400px;
  background: #fff;
  border-radius: 24px;
  padding: 40px 30px;
  text-align: center;
  box-shadow: 0 15px 35px rgba(50, 50, 93, 0.1), 0 5px 15px rgba(0, 0, 0, 0.07);
  position: relative;
}

.character-image {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  border: 6px solid #fff;
  box-shadow: 0 4px 12px rgba(255, 125, 41, 0.3);
  object-fit: cover;
  position: absolute;
  top: -60px;
  left: calc(50% - 60px);
}

.ai-modal h3 {
  font-size: 1.8rem;
  font-weight: 700;
  color: #ff8c00;
  margin: 40px 0 10px;
  text-transform: uppercase;
}

.character-style {
  display: inline-block;
  font-size: 1rem;
  font-weight: 500;
  color: #ff6b6b;
  background: #ffe0e0;
  padding: 8px 16px;
  border-radius: 20px;
  margin: 0 0 20px;
}

.character-message {
  background: linear-gradient(45deg, #fff0f0, #fff7e6);
  padding: 25px;
  border-radius: 16px;
  font-size: 1.1rem;
  color: #495057;
  line-height: 1.6;
  margin-bottom: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

/* 미션 섹션 */
.ai-modal h4 {
  font-size: 1.2rem;
  font-weight: 600;
  color: #212529;
  margin: 0 0 15px;
}

.ai-mission {
  background: linear-gradient(45deg, #ffa94d, #ffcb5a);
  color: #662d00;
  padding: 25px;
  border-radius: 24px;
  font-size: 1.3rem;
  font-weight: 600;
  margin-bottom: 30px;
  box-shadow: 0 4px 12px rgba(255, 169, 77, 0.3);
  position: relative;
  overflow: hidden;
  position: relative;
  padding-top: 40px;
}

.mission-icon {
  position: absolute;
  top: 7px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 1.8rem;
  color: #fff;
  animation: waveFlag 2s ease-in-out infinite;
  transform-origin: bottom center;
}

@keyframes waveFlag {
  0% {
    transform: translateX(-50%) rotate(0);
  }
  25% {
    transform: translateX(-50%) rotate(15deg);
  }
  50% {
    transform: translateX(-50%) rotate(0);
  }
  75% {
    transform: translateX(-50%) rotate(-15deg);
  }
  100% {
    transform: translateX(-50%) rotate(0);
  }
}
/*  */

.ai-mission::before {
  content: "";
  position: absolute;
  top: -40px;
  left: -40px;
  width: 80px;
  height: 80px;
  background: rgba(255, 255, 255, 0.4);
  border-radius: 50%;
  animation: pulse 1.5s infinite;
}

.ai-mission::after {
  content: "";
  position: absolute;
  bottom: -40px;
  right: -40px;
  width: 80px;
  height: 80px;
  background: rgba(255, 255, 255, 0.4);
  border-radius: 50%;
  animation: pulse 1.5s infinite 0.3s;
}

@keyframes pulse {
  0% {
    transform: scale(0);
    opacity: 1;
  }
  100% {
    transform: scale(2.5);
    opacity: 0;
  }
}
/* 버튼 스타일 */
.ai-modal button {
  width: 100%;
  padding: 16px 0;
  border-radius: 12px;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  text-transform: uppercase;
  letter-spacing: 1px;
  margin-bottom: 12px;
  border: none;
}

.mission-complete-button {
  background: linear-gradient(45deg, #ff922b, #ffa04d);
  color: #fff;
  box-shadow: 0 4px 12px rgba(255, 146, 43, 0.3);
}

.ai-close-button {
  color: #adb5bd;
}

.mission-complete-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 18px rgba(255, 146, 43, 0.35);
}

.ai-close-button:hover {
  color: #868e96;
}

/* 애니메이션 */
.ai-modal {
  animation: fadeInDown 0.6s ease;
}

@keyframes fadeInDown {
  from {
    opacity: 0;
    transform: translateY(-50px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 반응형 */
@media (max-width: 480px) {
  .ai-modal {
    width: 90%;
    padding: 60px 25px 35px;
  }

  .ai-modal h3 {
    margin-top: 20px;
  }
}
</style>
