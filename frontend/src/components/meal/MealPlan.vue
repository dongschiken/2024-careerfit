<template>
  <div>
    <MainHeader />
    <div class="header-container">
      <div class="action-buttons">
        <div @click="openMealModal()" class="button meal-button">
          <img
            src="@/assets/img/plus_icon.png"
            alt="plus"
            class="button-icon"
          />
          <div class="record-text">식단 기록 등록</div>
          <img src="@/assets/img/meal.png" class="monitor-weight-img" />
        </div>
        <div @click="openBodyModal()" class="button exercise-button">
          <img
            src="@/assets/img/plus_icon.png"
            alt="plus"
            class="button-icon"
          />
          <div class="record-text">신체 기록 등록</div>
          <img
            src="@/assets/img/monitor_weight.png"
            class="monitor-weight-img"
          />
        </div>
        <!-- 식단 모달 -->
        <div v-if="isMealModalOpen" class="modal">
          <div class="modal-content">
            <h2>식단 기록 모달</h2>
            <div class="modal-section">
              <label class="modal-label">분류</label>
              <div class="button-group">
                <button
                  class="type-button"
                  :class="{ selected: selectedMealType === '아침' }"
                  @click="selectMealType('아침')"
                >
                  아침
                </button>
                <button
                  class="type-button"
                  :class="{ selected: selectedMealType === '점심' }"
                  @click="selectMealType('점심')"
                >
                  점심
                </button>
                <button
                  class="type-button"
                  :class="{ selected: selectedMealType === '저녁' }"
                  @click="selectMealType('저녁')"
                >
                  저녁
                </button>
              </div>
            </div>
            <div class="modal-section">
              <label class="modal-label">포만감</label>
              <div class="button-group">
                <button
                  class="type-button"
                  :class="{ selected: selectedSatiation === '배부름' }"
                  @click="selectSatiation('배부름')"
                >
                  배부름
                </button>
                <button
                  class="type-button"
                  :class="{ selected: selectedSatiation === '적당함' }"
                  @click="selectSatiation('적당함')"
                >
                  적당함
                </button>
                <button
                  class="type-button"
                  :class="{ selected: selectedSatiation === '배고픔' }"
                  @click="selectSatiation('배고픔')"
                >
                  배고픔
                </button>
              </div>
            </div>
            <div class="modal-section">
              <label class="modal-label">사진 추가</label>
              <input
                type="file"
                class="modal-input"
                @change="handleFileChange"
              />
            </div>
            <div class="modal-section">
              <label class="modal-label">식사 시간</label>
              <input type="time" class="modal-input" v-model="mealTime" />
            </div>
            <div class="modal-section">
              <label class="modal-label">추가 메모</label>
              <textarea
                class="modal-textarea"
                v-model="additionalMemo"
                placeholder="오늘은 매우 적게 먹었다."
              ></textarea>
            </div>
            <div class="meal-modal-button-group">
              <button @click="closeMealModal" class="close-button">닫기</button>
              <button @click="mealRecordRegist" class="regist-button">
                등록
              </button>
            </div>
          </div>
        </div>

        <div v-if="isBodyModalOpen" class="modal">
          <div class="modal-content">
            <h2>신체 기록 모달</h2>
            <div class="modal-section">
              <label class="modal-label">체중 입력</label>
              <input
                type="number"
                class="modal-input"
                placeholder="예: 55.3kg"
                v-model="weight"
              />
            </div>
            <div class="modal-section">
              <label class="modal-label">골격근량</label>
              <input
                type="number"
                class="modal-input"
                placeholder="예: 25.3kg"
                v-model="muscleMass"
              />
            </div>
            <div class="modal-section">
              <label class="modal-label">체지방률</label>
              <input
                type="number"
                class="modal-input"
                placeholder="예: 25%"
                v-model="bodyFat"
              />
            </div>
            <div class="modal-section">
              <label class="modal-label">사진 추가</label>
              <input
                type="file"
                class="modal-input"
                @change="handleFileChange"
              />
            </div>
            <div class="modal-section">
              <label class="modal-label">컨디션</label>
              <div class="button-group">
                <button
                  class="condition-button"
                  :class="{ selected: selectedCondition === '좋음' }"
                  @click="selectCondition('좋음')"
                >
                  좋음
                </button>
                <button
                  class="condition-button"
                  :class="{ selected: selectedCondition === '보통' }"
                  @click="selectCondition('보통')"
                >
                  보통
                </button>
                <button
                  class="condition-button"
                  :class="{ selected: selectedCondition === '나쁨' }"
                  @click="selectCondition('나쁨')"
                >
                  나쁨
                </button>
              </div>
            </div>
            <div class="modal-section">
              <label class="modal-label">추가 메모</label>
              <textarea
                class="modal-textarea"
                placeholder="오늘은 몸이 가볍습니다."
                v-model="additionalcontent"
              ></textarea>
              {{ additionalcontent }}
            </div>
            <div class="body-modal-button-group">
              <button @click="closeBodyModal" class="close-button">닫기</button>
              <button @click="bodyRecordRegist()" class="regist-button">
                등록
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="my-days-container">
      <div class="my-days-layout">
        <div class="my-days">
          <div
            v-for="(day, index) in myDays"
            :key="index"
            class="my-day"
            :data-count="day.count"
          ></div>
        </div>
      </div>
    </div>
    <div class="container">
      <!-- Calendar Section -->
      <div class="calendar">
        <div class="calendar-header">
          <button @click="prevMonth" class="nav-button">
            <img
              src="@/assets/img/arrow_back_ios_24dp_5F6368_FILL0_wght400_GRAD0_opsz24.png"
            />
          </button>
          <h2>{{ currentYear }}.{{ currentMonth + 1 }}</h2>
          <button @click="nextMonth" class="nav-button">
            <img
              src="@/assets/img/arrow_forward_ios_24dp_5F6368_FILL0_wght400_GRAD0_opsz24.png"
            />
          </button>
        </div>
        <div class="days">
          <div class="day" v-for="(day, index) in days" :key="index">
            {{ day }}
          </div>
        </div>
        <div class="dates">
          <!-- Spacer for the first weekday -->
          <div
            v-for="n in startDayOfMonth"
            :key="'spacer-' + n"
            class="spacer"
          ></div>
          <!-- Dates -->
          <div
            v-for="(date, index) in dates"
            :key="index"
            :class="[
              'date',
              { selected: isSelectedDate(date), today: isToday(date) },
            ]"
            @click="selectDate(date)"
          >
            {{ date.getDate() }}
          </div>
        </div>
      </div>

      <div class="meal-records">
        <!-- 데이터가 없는 경우 -->
        <div v-if="meals === undefined || meals === null">
          <p class="no-data-message">
            현재 등록된 식단이 없습니다. careerfit의 마스코트 마이구민과 함께
            만들러 가실까요??
          </p>
          <RouterLink to="/">
            <button class="add-data-button">마이구민 이용하기</button>
          </RouterLink>
        </div>

        <!-- 데이터가 있는 경우 -->
        <div v-else>
          <div class="meal-card" v-for="(meal, index) in meals" :key="index">
            <div class="meal-type">
              <div>{{ meal.type }}</div>
              <div class="meal-type-square"></div>
            </div>
            <div class="meal-details">{{ meal.details }}</div>
            <div class="nutrients">{{ meal.nutrients }}</div>
          </div>
        </div>
      </div>
    </div>

    <div class="tabs">
      <button
        :class="{ active: selectedTab === 'meal' }"
        @click="getMealRecord()"
      >
        식단 기록
      </button>
      <button
        :class="{ active: selectedTab === 'exercise' }"
        @click="selectBodyRecord()"
      >
        신체 기록
      </button>
    </div>
    <!-- 식단 기록 -->
    <div class="detailed-record" v-if="selectedTab === 'meal'">
      <div
        class="meal-detail"
        v-for="(meal, index) in detailedMeals"
        :key="index"
      >
        <img :src="`http://localhost:8080/uploads/${meal.img}`" />
        <div class="meal-info">
          <div>
            <h4>{{ meal.type }}</h4>
          </div>
          <div>식사 시간 : {{ meal.time }}</div>
          <div>포만감 : {{ meal.fullness }}</div>
          <div>내메모 : {{ meal.content }}</div>
        </div>
      </div>
    </div>

    <!-- 신체 기록 -->
    <div class="detailed-body-record" v-if="selectedTab === 'exercise'">
      <div class="body-record" v-if="bodyRecords !== null">
        <div>
          <img :src="`http://localhost:8080/uploads/${bodyRecords.img}`" />
          <div>{{ bodyRecords.weight }}kg</div>
          <div>{{ bodyRecords.skeletalMuscle }}kg</div>
          <div>{{ bodyRecords.bodyFat }}%</div>
        </div>
        <div>{{ bodyRecords.bodyCondition }}</div>
        <div>{{ bodyRecords.content }}</div>
        <div class="body-info"></div>
      </div>
    </div>
    <MainFooter />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import MainHeader from "@/components/module/MainHeader.vue";
import MainFooter from "@/components/module/MainFooter.vue";
import api from "@/api/axiosInstance";

const currentYear = ref(new Date().getFullYear());
const currentMonth = ref(new Date().getMonth());
const selectedDate = ref(new Date());
const selectedTab = ref("meal");
const isMealModalOpen = ref(false);
const isBodyModalOpen = ref(false);
const selectedMealType = ref("");
const selectedSatiation = ref("");
const mealTime = ref("");
const additionalMemo = ref("");
const selectedFile = ref(null);
const weight = ref("");
const muscleMass = ref("");
const bodyFat = ref("");
const additionalcontent = ref("");
const myDays = ref([]);

const days = ["일", "월", "화", "수", "목", "금", "토"];

// 기본 식단 데이터
const meals = ref({});

// 모달창 띄워서 회원의 식단 기록 등록할 수 있게 하기
// 잔디밭 만들어서 회원이 얼마만큼의 식단을 기록하고 있는지 알려주기, 100일 -> 365일 순으로 달성할 때 선물 주기
// 상세 식단 데이터
// 처음에 회원의 현재 날짜에 해당하는 식단 리스트 전부 가져오기
const detailedMeals = ref([]);
const bodyRecords = ref({});

const bodyRecordRegist = async () => {
  if (
    !weight.value ||
    !muscleMass.value ||
    !bodyFat.value ||
    !selectedCondition.value ||
    !additionalcontent.value
  ) {
    alert("모든 필드를 입력해주세요!");
    return;
  }

  const currentDate = selectedDate.value || new Date();
  const formattedDate = `${currentDate.getFullYear()}-${String(
    currentDate.getMonth() + 1
  ).padStart(2, "0")}-${String(currentDate.getDate()).padStart(2, "0")}`;

  const bodyRecord = {
    date: formattedDate,
    weight: weight.value,
    skeletalMuscle: muscleMass.value,
    bodyFat: bodyFat.value,
    bodyCondition: selectedCondition.value,
    content: additionalcontent.value,
  };
  alert(additionalcontent.value);
  // FormData 객체 생성
  const formData = new FormData();
  formData.append(
    "bodyRecord",
    new Blob([JSON.stringify(bodyRecord)], { type: "application/json" })
  );
  if (selectedFile.value) {
    formData.append("file", selectedFile.value);
  }

  try {
    // 서버로 데이터 전송
    const response = await api.post("/api/body/record", formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });

    if (response.status === 201) {
      alert("신체 기록 등록완료!");
      closeBodyModal(); // 모달 닫기
    } else {
      alert("신체 기록 등록에 실패했습니다.");
    }
  } catch (error) {
    if (error.response.status === 404) {
      alert("이미 등록되어 있습니다.");
    } else {
      alert("신체 기록 등록 중 문제가 발생했습니다.");
    }
  }
};

// 모달 닫기
const closeBodyModal = () => {
  isBodyModalOpen.value = false;

  // 입력 필드 초기화
  weight.value = "";
  muscleMass.value = "";
  bodyFat.value = "";
  selectedCondition.value = "";
  additionalcontent.value = "";
  selectedFile.value = null;
};

// 등록 버튼 클릭 이벤트
const mealRecordRegist = async () => {
  if (!selectedMealType.value || !selectedSatiation.value || !mealTime.value) {
    alert("모든 필드를 입력해주세요!");
    return;
  }
  const currentDate = selectedDate.value || new Date();
  const formattedDate = `${currentDate.getFullYear()}-${String(
    currentDate.getMonth() + 1
  ).padStart(2, "0")}-${String(currentDate.getDate()).padStart(2, "0")}`;

  const mealRecord = {
    type: selectedMealType.value,
    fullness: selectedSatiation.value,
    mealTime: mealTime.value,
    content: additionalMemo.value,
    date: formattedDate,
  };
  // FormData 객체 생성
  const formData = new FormData();
  formData.append(
    "mealRecord",
    new Blob([JSON.stringify(mealRecord)], { type: "application/json" })
  );
  if (selectedFile.value) {
    formData.append("file", selectedFile.value);
  }

  try {
    // 서버로 데이터 전송
    const response = await api.post("/api/meal/record", formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });

    if (response.status === 201) {
      alert("식단 기록 등록완료!");
      window.location.reload();
      closeMealModal();
    } else {
      alert("식단 기록 등록에 실패했습니다.");
    }
  } catch (error) {
    console.error("등록 오류:", error);
    alert("동일한 식단 기록이 존재합니다.");
  }
};

// 모달 닫기
const closeMealModal = () => {
  isMealModalOpen.value = false;

  // 입력 필드 초기화
  selectedMealType.value = "";
  selectedSatiation.value = "";
  mealTime.value = "";
  additionalMemo.value = "";
  selectedFile.value = null;
};

// 파일 처리
const handleFileChange = (event) => {
  const file = event.target.files[0];
  selectedFile.value = file;
};

// 해당 월의 날짜 리스트
const dates = computed(() => {
  const end = new Date(currentYear.value, currentMonth.value + 1, 0);
  const datesArray = [];
  for (let i = 1; i <= end.getDate(); i++) {
    datesArray.push(new Date(currentYear.value, currentMonth.value, i));
  }
  return datesArray;
});

// 해당 월 시작 요일 계산
const startDayOfMonth = computed(() => {
  const firstDay = new Date(currentYear.value, currentMonth.value, 1);
  return firstDay.getDay();
});

const prevMonth = () => {
  if (currentMonth.value === 0) {
    currentMonth.value = 11;
    currentYear.value -= 1;
  } else {
    currentMonth.value -= 1;
  }
};

const selectMealType = (type) => {
  selectedMealType.value = type;
};

const selectSatiation = (satiation) => {
  selectedSatiation.value = satiation;
};
const selectedCondition = ref("");

const selectCondition = (condition) => {
  selectedCondition.value = condition;
};

const nextMonth = () => {
  if (currentMonth.value === 11) {
    currentMonth.value = 0;
    currentYear.value += 1;
  } else {
    currentMonth.value += 1;
  }
};

const openMealModal = () => {
  isMealModalOpen.value = true;
};

const openBodyModal = () => {
  isBodyModalOpen.value = true;
};
const processMeals = (responseMeals) => {
  if (responseMeals === null) return;
  return [
    {
      type: "아침",
      details: responseMeals.morning.name,
      nutrients: `칼로리: ${responseMeals.morning.kcal}kcal, 단백질: ${responseMeals.morning.protein}g, 지방: ${responseMeals.morning.fat}g, 탄수화물: ${responseMeals.morning.carbs}g`,
    },
    {
      type: "점심",
      details: responseMeals.lunch.name,
      nutrients: `칼로리: ${responseMeals.lunch.kcal}kcal, 단백질: ${responseMeals.lunch.protein}g, 지방: ${responseMeals.lunch.fat}g, 탄수화물: ${responseMeals.lunch.carbs}g`,
    },
    {
      type: "저녁",
      details: responseMeals.dinner.name,
      nutrients: `칼로리: ${responseMeals.dinner.kcal}kcal, 단백질: ${responseMeals.dinner.protein}g, 지방: ${responseMeals.dinner.fat}g, 탄수화물: ${responseMeals.lunch.carbs}g`,
    },
  ];
};

const getMealRecord = async () => {
  selectedTab.value = "meal";
  const currentDate = selectedDate.value || new Date();
  const formattedDate = `${currentDate.getFullYear()}-${String(
    currentDate.getMonth() + 1
  ).padStart(2, "0")}-${String(currentDate.getDate()).padStart(2, "0")}`;
  try {
    const response = await api.get(`/api/meal/record/${formattedDate}`);
    if (response.data.length !== 0) {
      detailedMeals.value = response.data.map((meal) => ({
        type: meal.type, // 아침/점심/저녁
        time: meal.mealTime, // 시간
        content: meal.content, // 세부 내용
        fullness: meal.fullness,
        img: `${meal.img}`, // 이미지 경로
      }));
      console.log(detailedMeals.value);
    } else {
      detailedMeals.value = null;
    }
  } catch (error) {
    console.log(error);
    alert("날짜에 해당하는 데이터를 가져오는 중 문제가 발생했습니다.");
  }
};

const selectBodyRecord = async () => {
  selectedTab.value = "exercise";
  const currentDate = selectedDate.value || new Date();
  const formattedDate = `${currentDate.getFullYear()}-${String(
    currentDate.getMonth() + 1
  ).padStart(2, "0")}-${String(currentDate.getDate()).padStart(2, "0")}`;
  try {
    const response = await api.get(`/api/body/record/${formattedDate}`);
    bodyRecords.value = response.data;
  } catch (error) {}
};

// 날짜 선택
const selectDate = async (date) => {
  console.log("날짜" + date);
  selectedDate.value = date; // 선택된 날짜 업데이트
  const nowDate = `${currentYear.value}-${
    currentMonth.value + 1
  }-${date.getDate()}`;
  try {
    const response = await api.get(`/api/meal/${nowDate}`);
    meals.value = processMeals(response.data.meals); // 데이터 배열로 변환
    getMealRecord(nowDate);
    console.log(meals);
  } catch (error) {
    console.log(error);
    alert("날짜에 해당하는 데이터를 가져오는 중 문제가 발생했습니다.");
  }
};

// 선택된 날짜인지 확인
const isSelectedDate = (date) => {
  return (
    date.getFullYear() === selectedDate.value.getFullYear() &&
    date.getMonth() === selectedDate.value.getMonth() &&
    date.getDate() === selectedDate.value.getDate()
  );
};

// 오늘 날짜인지 확인
const isToday = (date) => {
  const today = new Date();
  return (
    date.getFullYear() === today.getFullYear() &&
    date.getMonth() === today.getMonth() &&
    date.getDate() === today.getDate()
  );
};

onMounted(() => {
  const today = new Date(); // 오늘 날짜
  console.log(today);
  selectDate(today); // 오늘 날짜로 selectDate 호출
  const currentYear = new Date().getFullYear();
  const startDate = new Date(currentYear, 0, 1);
  const endDate = new Date(currentYear, 11, 31);
  for (
    let date = startDate;
    date <= endDate;
    date.setDate(date.getDate() + 1)
  ) {
    const randomCount = Math.floor(Math.random() * 5) + 1;
    let countCategory = "";
    if (randomCount === 1) {
      countCategory = "1";
    } else if (randomCount <= 3) {
      countCategory = "2-3";
    } else if (randomCount <= 6) {
      countCategory = "4-6";
    } else if (randomCount <= 9) {
      countCategory = "7-9";
    } else {
      countCategory = "10+";
    }
    myDays.value.push({ count: countCategory });
  }
});
</script>
<style scoped>
.header-container {
  display: flex;
  min-width: 1000px;
  justify-content: center;
}
body {
  font-family: Arial, sans-serif;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  margin: 0;
  background-color: #f5f5f5;
}
.record-text {
  min-width: 105px;
  font-weight: bold;
  margin-left: 15px;
}
.monitor-weight-img {
  margin-left: 40px;
  max-width: 35px;
}
.action-buttons {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 1rem; /* 버튼 간 간격 */
  margin-top: 20px; /* 상단 여백 */
  margin-bottom: 30px;
}

/* 식단 등록 버튼 */
.meal-button {
  display: flex;
  align-items: center;
}

.meal-button:hover {
  transform: scale(1.05); /* 살짝 확대 */
}

/* 신체 기록 버튼 */
.exercise-button {
  display: flex;
  align-items: center;
}

.exercise-button:hover {
  transform: scale(1.05); /* 살짝 확대 */
}

.button {
  padding: 1rem;
  border: none;
  border-radius: 10px;
  background-color: #f0f0f0;
  cursor: pointer;
  transition: background-color 0.3s, color 0.3s, transform 0.2s;
}

.button-icon {
  min-width: 25px;
  max-width: 25px;
}

.container {
  display: flex;
  grid-template-columns: 1fr 2fr 1fr;
  align-items: flex-start;
  justify-content: center;
  flex-wrap: wrap;
  margin-top: 40px;
}

.calendar {
  padding: 1rem;
  text-align: center;
  border: 1px solid #e0e0e0;
  border-radius: 10px;
  box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.1);
  min-width: 400px;
  max-width: 400px;
}

.calendar-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 1rem;
}

.nav-button {
  background: none;
  border: none;
  font-size: 1.5rem;
  color: #333;
  cursor: pointer;
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.4); /* 어두운 투명 배경 */
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: #f7f7f7; /* 연회색 배경 */
  border: 1px solid #ddd; /* 가벼운 테두리 */
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2); /* 약간의 그림자 */
  border-radius: 15px; /* 둥근 모서리 */
  width: 500px; /* 고정된 너비 */
  padding: 20px 30px; /* 넉넉한 내부 여백 */
  text-align: center;
}

.modal-section {
  margin-bottom: 20px;
  text-align: left;
}

.modal-label {
  display: block;
  font-size: 1rem;
  font-weight: bold;
  color: #4a4a4a; /* 연한 검정색 */
  margin-bottom: 8px;
}

.modal-input {
  min-width: 479px;
  max-width: 479px;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 8px;
  background-color: #fff;
  font-size: 1rem;
  transition: border-color 0.15s ease;
}

.modal-input:focus {
  border-color: #ff7d29; /* 강조된 포커스 색상 */
  outline: none;
}

.modal-textarea {
  min-width: 479px;
  max-width: 479px;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 8px;
  background-color: #fff;
  font-size: 1rem;
  resize: none;
  min-height: 80px;
}

.button-group {
  display: flex;
  gap: 10px;
}

.condition-button {
  padding: 8px 15px;
  border: none;
  border-radius: 8px;
  background-color: #e0e0e0;
  font-size: 0.95rem;
  cursor: pointer;
  transition: background-color 0.15s ease;
}

.condition-button.selected {
  background-color: #ff7d29; /* 주황색으로 강조 */
  color: white;
}

.condition-button:hover {
  background-color: #d6d6d6;
}

.close-button {
  background: #ff7d29; /* 밝은 주황색 */
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  font-size: 1rem;
  cursor: pointer;
  margin-top: 20px;
  transition: background 0.15s ease;
}
.close-button:hover {
  transform: scale(1.05);
}
button.type-button:hover {
  background: #c9bbbb;
}
button.type-button {
  padding: 8px 15px;
  border: none;
  border-radius: 8px;
  background-color: #e0e0e0;
  font-size: 0.95rem;
  cursor: pointer;
  transition: background-color 0.15s ease;
}
button.type-button.selected {
  background-color: #ff7d29;
  color: white;
}
.days,
.dates {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 0.5rem;
  justify-items: center;
}

.spacer {
  width: 2.5rem;
  height: 2.5rem;
}

.day {
  font-weight: bold;
  color: #666;
  font-size: 0.9rem;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 2.5rem; /* 날짜와 동일한 높이 */
}

.date {
  width: 2.5rem;
  height: 2.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border-radius: 50%;
  font-size: 0.9rem;
  transition: background-color 0.3s;
  color: #757575;
  font-weight: 100;
}

.date:hover {
  background-color: #f0f8ff;
}

.date.selected {
  color: #000000; /* 텍스트 색상을 변경 */
  background-color: #dde1e5; /* 선택된 날짜의 배경색 설정 */
  border-radius: 50%; /* 둥근 모서리 */
  transition: background-color 0.3s, box-shadow 0.3s; /* 애니메이션 추가 */
}

.date.today {
  border: 1px solid #ff7d29;
}

.meal-records {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  min-width: 430px;
  max-width: 430px;
  min-height: 379px;
  margin-left: 40px;
}
div.container > div.meal-records > div {
  min-width: 430px;
  max-width: 430px;
  min-height: 118px;
  max-height: 100px;
}
.meal-card {
  border: 1px solid #ddd;
  border-radius: 10px;
  box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.05);
  text-align: center;
  min-width: 440px;
  max-width: 440px;
}

.tabs {
  display: flex;
  flex-direction: row;
  gap: 1rem;
  align-items: center;
  margin-top: 1rem;
  margin: auto;
  margin-top: 100px;
  min-width: 500px;
  max-width: 500px;
}

.tabs button {
  width: 100%;
  padding: 1rem;
  border: none;
  border-radius: 10px;
  background-color: #f0f0f0;
  cursor: pointer;
  font-weight: bold;
  transition: background-color 0.3s, color 0.3s, transform 0.2s; /* transform 애니메이션 추가 */
}

.tabs button.active {
  background-color: #ff7d29;
  color: white;
}

.tabs button:hover {
  transform: scale(1.05); /* 살짝 확대 */
}

.detailed-record {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-top: 20px;
  margin-bottom: 40px;
}
.meal-modal-button-group {
  display: flex;
  justify-content: space-between;
}
.meal-detail {
  display: flex;
  gap: 1rem;
  align-items: center;
  border: 1px solid #ddd;
  border-radius: 10px;
  min-width: 600px;
  max-width: 600px;
  margin: auto;
  box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.05);
  min-width: 600px;
  min-height: 140px;
  padding-left: 21px;
}

.meal-detail img {
  width: 110px;
  height: 110px;
  object-fit: cover;
  border-radius: 10px;
}
.nutrients {
  padding-top: 10px;
  font-weight: 200;
  font-size: 13px;
  text-align: start;
}
div.container > div.meal-records > div > div.meal-details {
  padding-left: 20px;
  padding-top: 10px;
  text-align: start;
  font-weight: 200;
  font-size: 15px;
}
div.container > div.meal-records > div > div.meal-type {
  padding-left: 20px;
  padding-top: 10px;
  text-align: start;
}
.meal-type {
  display: flex;
  justify-content: space-between;
}
.meal-type-square {
  width: 10px;
  height: 10px;
  border-radius: 2px;
  margin-right: 10px;
}

div:nth-child(1) > div.meal-type > div.meal-type-square {
  background-color: #e4bda4;
}

div:nth-child(2) > div.meal-type > div.meal-type-square {
  background-color: #e1cff3;
}
div:nth-child(3) > div.meal-type > div.meal-type-square {
  background-color: #ff7d29;
}
.body-modal-button-group {
  display: flex;
  justify-content: space-between;
}

.regist-button {
  color: 000;
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  font-size: 1rem;
  cursor: pointer;
  margin-top: 20px;
  background-color: #e0e0e0;
  transition: background 0.15s ease;
}

.regist-button:hover {
  transform: scale(1.05); /* 살짝 확대 */
}

.nav-button img {
  width: 20px;
  height: 20px;
  filter: grayscale(50%) contrast(120%);
  transition: transform 0.3s, filter 0.3s;
}

.nav-button img:hover {
  transform: scale(1.2);
  filter: grayscale(0%) contrast(150%);
}

.meal-card {
  border: 1px solid #ddd;
  border-radius: 10px;
  box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.05);
  padding: 18px;
  margin-bottom: 16px;
  background-color: #fff;
}

.meal-type {
  display: flex;
  justify-content: space-between;
  font-weight: bold;
  font-size: 1rem;
  margin-bottom: 0.5rem;
}

.meal-type-square {
  width: 10px;
  height: 10px;
  background-color: #ff7d29;
  border-radius: 2px;
}

.meal-details {
  font-size: 0.9rem;
  color: #555;
  margin-bottom: 0.5rem;
  text-align: start;
}

.nutrients {
  font-size: 0.8rem;
  color: #777;
}
.meal-info {
  margin-left: 30px;
}
.my-days {
  display: flex;
  flex-wrap: wrap;
  min-width: 860px;
  max-width: 860px;
}
.my-day {
  min-width: 1px;
  max-width: 1px;
  min-height: 1px;
  max-height: 1px;
  background-color: #e0e0e0;
  border-radius: 4px;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  padding: 10px;
  margin: 1px;
}
.my-day[data-count="1"] {
  background-color: #d4e157;
}
.my-day[data-count="2-3"] {
  background-color: #aed581;
}
.my-day[data-count="4-6"] {
  background-color: #81c784;
}
.my-day[data-count="7-9"] {
  background-color: #66bb6a;
}
.my-day[data-count="10+"] {
  background-color: #388e3c;
}
.my-days-container {
  display: flex;
  justify-content: center;
}
.my-days-layout {
  border: 1px solid rgb(242, 229, 229);
  border-radius: 10px;
  padding: 20px;
}
</style>
