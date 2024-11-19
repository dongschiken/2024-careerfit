 <template>
  <div>
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
      <select id="radius" v-model="radius" @change="updateRadius" class="radius-select">
        <option value="1000">1km</option>
        <option value="3000">3km</option>
        <option value="5000">5km</option>
      </select>
    </div>

    <!-- 장소 필터 버튼 -->
    <div class="filter-buttons">
  <button @click="filterPlaces('전체')" class="filter-button filter-all">전체</button>
  <button @click="filterPlaces('헬스장')" class="filter-button filter-gym">헬스장</button>
  <button @click="filterPlaces('클라이밍')" class="filter-button filter-climbing">클라이밍</button>
  <button @click="filterPlaces('공원')" class="filter-button filter-park">공원</button>
</div>


    <!-- 지도 -->
    <div id="map" class="map-container"></div>

    <!-- 선택된 장소를 표시하는 모달 -->
    <div v-if="selectedPlace" class="modal-overlay" @click.self="closeModal">
      <div class="modal">
        <button class="close-button" @click="closeModal">✕</button>
        <div class="modal-content">
          <!-- 장소 정보 -->
          <div class="modal-info">
            <h3 class="modal-title">{{ selectedPlace.name }}</h3>
            <p class="modal-address">{{ selectedPlace.address }}</p>
          </div>
          <!-- 채팅방 리스트 -->
          <div class="modal-chat-list">
            <h4>채팅방 리스트</h4>
            <ul>
              <li v-for="(room, index) in chatRooms" :key="index">{{ room }}</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      map: null,
      markers: [], // 생성된 마커를 저장
      centerLat: null,
      centerLng: null,
      radius: 3000, // 기본 반경 3km
      currentKeyword: "전체", // 초기 키워드 (모든 장소 표시)
      selectedPlace: null, // 선택된 장소 정보
      placeQuery: "", // 사용자가 검색한 장소
      chatRooms: ["채팅방 1", "채팅방 2", "채팅방 3"], // 임시 채팅방 리스트
    };
  },
  mounted() {
    this.getUserLocation(); // 사용자 위치 가져오기
  },
  methods: {
    // 사용자 현재 위치 가져오기
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
            this.centerLat = 37.5665; // 서울 시청
            this.centerLng = 126.9780;
            this.initMap();
          }
        );
      }
    },

    // 지도 초기화
    initMap() {
      const mapContainer = document.getElementById("map");
      const mapOption = {
        center: new kakao.maps.LatLng(this.centerLat, this.centerLng),
        level: this.getZoomLevel(this.radius),
      };
      this.map = new kakao.maps.Map(mapContainer, mapOption);

      // 지도 고정
      this.lockMap();

      this.searchPlaces(); // 초기 장소 검색
    },

    // 지도 고정
    lockMap() {
      if (this.map) {
        this.map.setDraggable(false); // 드래그 비활성화
        this.map.setZoomable(false); // 확대/축소 비활성화
      }
    },

    // 장소 검색
    searchPlaces() {
      const ps = new kakao.maps.services.Places();
      const searchOption = {
        location: new kakao.maps.LatLng(this.centerLat, this.centerLng),
        radius: this.radius,
      };

      this.clearMarkers(); // 기존 마커 제거

      const keywords =
        this.currentKeyword === "전체"
          ? ["헬스장", "클라이밍", "공원"]
          : [this.currentKeyword]; // 현재 키워드만 검색

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

    // 장소 필터 버튼 클릭 시
    filterPlaces(keyword) {
      this.currentKeyword = keyword;
      this.searchPlaces(); // 필터링된 키워드로 장소 재검색
    },

    // 검색 후 지도 업데이트
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

          this.map.setCenter(new kakao.maps.LatLng(this.centerLat, this.centerLng));
          this.searchPlaces(); // 검색한 장소를 중심으로 반경 내 장소 표시
        } else {
          alert("장소를 찾을 수 없습니다.");
        }
      });
    },

    // 기존 마커 제거
    clearMarkers() {
      this.markers.forEach((marker) => marker.setMap(null));
      this.markers = [];
    },

    // 반경 변경 시
    updateRadius() {
      if (this.map) {
        const zoomLevel = this.getZoomLevel(this.radius);
        this.map.setLevel(zoomLevel);
        this.searchPlaces();
      }
    },

    // 반경에 따른 줌 레벨 계산
    getZoomLevel(radius) {
      if (radius <= 1000) return 4;
      if (radius <= 3000) return 5;
      return 6;
    },

    // 마커 표시
    displayMarkers(places) {
      places.forEach((place) => {
        const markerPosition = new kakao.maps.LatLng(place.y, place.x);
        const marker = new kakao.maps.Marker({
          position: markerPosition,
          map: this.map,
        });

        kakao.maps.event.addListener(marker, "click", () => {
          this.selectedPlace = {
            name: place.place_name,
            address: place.road_address_name || place.address_name || "주소 정보 없음",
          };
        });

        this.markers.push(marker);
      });
    },

    // 모달 닫기
    closeModal() {
      this.selectedPlace = null;
    },
  },
};
</script>


<style>
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
  border: 2px solid #FF9C4A;
  border-radius: 25px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  outline: none;
}

.search-input:focus {
  border-color: #FF7D29;
  outline: none;
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.2);
}

.search-button {
  padding: 14px 26px;
  font-size: 16px;
  font-weight: bold;
  color: white;
  background: linear-gradient(45deg, #FF9C4A, #FF7D29); /* 그라데이션 */
  border: none;
  border-radius: 25px;
  cursor: pointer;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15); /* 입체감 */
  transition: transform 0.2s ease, box-shadow 0.3s ease;
}

.search-button:hover {
  background-color: #FF7D29;
  transform: scale(1.05); /* 살짝 올라가는 애니메이션 */
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.3); /* 더 강한 입체감 */
}

.search-button:active {
  transform: translateY(2px); /* 클릭할 때 눌리는 느낌 */
  box-shadow: 0 3px 10px rgba(255, 125, 41, 0.2);
}


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
  border: 2px solid #FF9C4A;
  border-radius: 15px;
  background-color: white;
  cursor: pointer;
  transition: all 0.35 ease;
}

.radius-select:hover{
  border: 2px solid #FFBF78;
  background-color: #FFFAE6;
}

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
  background: linear-gradient(45deg, #FF9C4A, #FF7D29); /* 메인 컬러 계열 */
}

/* 호버 효과 */
.filter-button:hover {
  background: linear-gradient(45deg, #FF9C4A, #FF7D29);
  transform: scale(1.05);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.3);
}

.map-container {
  width: 100%;
  height: 80vh;
  border: 1px solid #ddd;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.5s ease;
}

.modal {
  background: white;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
  width: 80%;
  max-width: 400px;
  text-align: center;
  animation: slideUp 0.5s ease;
}

.close-button {
  position: absolute;
  top: 10px;
  right: 10px;
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
}

.modal-title {
  font-size: 1.5rem;
  margin-bottom: 10px;
}

.modal-address {
  color: #555;
  margin-bottom: 20px;
}

.modal-action-button {
  width: 100%;
  padding: 10px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  font-size: 16px;
  cursor: pointer;
}

.modal-action-button:hover {
  background-color: #0056b3;
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
