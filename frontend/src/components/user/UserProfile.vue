<template>
  <div class="page-wrapper">
    <div class="page-container">
      <div v-if="!loading">
        <div class="user-profile-container">
          <div class="profile-header">
            <p class="profile-title">{{ userStore.nickname }}'s CAREER</p>
          </div>

          <div class="profile-content">
            <div class="profile-image-section">
              <img :src="profileImage" class="profile-image" alt="Profile" />
              <button
                @click="showProfileImageModal = true"
                class="edit-info-button"
              >
                <i class="fas fa-camera"></i>
              </button>
            </div>

            <p class="user-email">{{ userStore.email }}</p>

            <div class="button-group">
              <button @click="showEditUserInfoModal = true" class="edit-button">
                회원정보 변경
              </button>
              <button @click="showPasswordModal = true" class="edit-button">
                비밀번호 변경
              </button>
            </div>

            <div class="menu-grid">
              <button class="menu-button">
                <i class="fas fa-heart"></i>
                좋아요한 게시물
              </button>
              <button class="menu-button">
                <i class="fas fa-edit"></i>
                내가 쓴 게시물
              </button>
              <button class="menu-button">
                <i class="fas fa-utensils"></i>
                나의 식단
              </button>
              <button class="menu-button">
                <i class="fas fa-comment"></i>
                내 채팅
              </button>
            </div>
          </div>
        </div>

        <!-- 모달 컴포넌트들 -->
        <div
          v-if="showEditUserInfoModal"
          class="modal-backdrop"
          @click="closeModals"
        >
          <div class="modal-content" @click.stop>
            <EditUserInfoModal
              @close="showEditUserInfoModal = false"
              @update="handleUserUpdate"
            />
          </div>
        </div>

        <div
          v-if="showPasswordModal"
          class="modal-backdrop"
          @click="closeModals"
        >
          <div class="modal-content" @click.stop>
            <PasswordModal @close="showPasswordModal = false" />
          </div>
        </div>

        <div
          v-if="showProfileImageModal"
          class="modal-backdrop"
          @click="closeModals"
        >
          <div class="modal-content" @click.stop>
            <ProfileImageModal
              @close="showProfileImageModal = false"
              @update="handleProfileUpdate"
            />
          </div>
        </div>
      </div>

      <div v-else class="loading-container">
        <p>로딩 중...</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useUserStore } from "@/stores/userStore";
import EditUserInfoModal from "./EditUserInfoModal.vue";
import PasswordModal from "./PasswordModal.vue";
import ProfileImageModal from "./ProfileImageModal.vue";
import api from "@/api/axiosInstance";

const userStore = useUserStore();
const showEditUserInfoModal = ref(false);
const showPasswordModal = ref(false);
const showProfileImageModal = ref(false);
const loading = ref(true);

// 프로필 이미지 computed 속성
const profileImage = computed(() => {
  return userStore.profileUrl || "/default-profile.png";
});

const initializeProfile = async () => {
  try {
    loading.value = true;
    // api 인스턴스 사용
    const tokenResponse = await api.get("/api/token-user");

    // userStore에 사용자 정보를 설정
    if (tokenResponse.data) {
      userStore.setUser(tokenResponse.data);
    }

    // 이제 userId가 설정되었으므로 전체 사용자 정보를 가져옵니다
    await userStore.fetchCurrentUser();
  } catch (error) {
    console.error("사용자 정보 로드 실패:", error);
    if (error.response && error.response.status === 401) {
      await userStore.clearUser();
      router.push("/login");
    }
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  initializeProfile();
});

const handleUserUpdate = async () => {
  await initializeProfile();
  showEditUserInfoModal.value = false;
};

const handleProfileUpdate = async () => {
  await initializeProfile();
  showProfileImageModal.value = false;
};

// 모달 닫기 함수
const closeModals = () => {
  showEditUserInfoModal.value = false;
  showPasswordModal.value = false;
  showProfileImageModal.value = false;
};
</script>

<style scoped>
/* 페이지 전체 wrapper */
.page-wrapper {
  min-height: 100vh;
  background-color: #f8f9fa;
  padding: 40px 20px;
}

/* 페이지 컨테이너 */
.page-container {
  max-width: 800px;
  margin: 0 auto;
}

/* 유저 프로필 컨테이너 */
.user-profile-container {
  background: white;
  border-radius: 20px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

/* 프로필 헤더 */
.profile-header {
  background: #ff7d29;
  padding: 20px;
  text-align: center;
}

.profile-title {
  color: white;
  font-size: 1.4rem;
  font-weight: 600;
  margin: 0;
}

/* 프로필 컨텐츠 */
.profile-content {
  padding: 40px 30px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* 프로필 이미지 섹션 */
.profile-image-section {
  position: relative;
  margin-bottom: 30px;
}

.profile-image {
  width: 140px;
  height: 140px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #ff7d29;
  box-shadow: 0 4px 12px rgba(255, 125, 41, 0.15);
}

.edit-info-button {
  position: absolute;
  bottom: 5px;
  right: 5px;
  background: #ff7d29;
  color: white;
  border: none;
  border-radius: 50%;
  width: 35px;
  height: 35px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 2px 6px rgba(255, 125, 41, 0.2);
}

.edit-info-button:hover {
  transform: scale(1.1);
  background: #ff6b10;
}

/* 이메일 */
.user-email {
  color: #666;
  font-size: 1.1rem;
  margin: 15px 0;
}

/* 버튼 그룹 */
.button-group {
  margin: 25px 0;
  display: flex;
  gap: 12px;
}

.edit-button {
  padding: 10px 20px;
  background: #ff7d29;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  font-weight: 500;
  font-size: 0.95rem;
}

.edit-button:hover {
  background: #ff6b10;
  transform: translateY(-1px);
}

/* 메뉴 그리드 */
.menu-grid {
  width: 100%;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-top: 30px;
}

.menu-button {
  padding: 20px;
  background: white;
  border: 2px solid #ffbf78;
  border-radius: 12px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  transition: all 0.2s ease;
  color: #333;
  font-weight: 500;
}

.menu-button:hover {
  background: #feffd2;
  border-color: #ff7d29;
  transform: translateY(-2px);
}

.menu-button i {
  color: #ff7d29;
  font-size: 1.2rem;
}

/* 모달 스타일 */
.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 450px;
  max-height: calc(100vh - 40px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  animation: modalFadeIn 0.3s ease-out;
}

@keyframes modalFadeIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 로딩 컨테이너 */
.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 400px;
  color: #666;
}
</style>
