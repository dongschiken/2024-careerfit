<template>
  <div>
    <MainHeader />
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

      <!-- Meal Records -->
      <div class="meal-records">
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
    <!-- Tabs -->
    <div class="tabs">
      <button
        :class="{ active: selectedTab === 'meal' }"
        @click="selectedTab = 'meal'"
      >
        식단 기록
      </button>
      <button
        :class="{ active: selectedTab === 'exercise' }"
        @click="selectedTab = 'exercise'"
      >
        신체 기록
      </button>
    </div>

    <!-- Detailed Meal Record -->
    <div class="detailed-record" v-if="selectedTab === 'meal'">
      <div
        class="meal-detail"
        v-for="(meal, index) in detailedMeals"
        :key="index"
      >
        <img src="@/assets/img/pasta-7209002_1280.jpg" alt="Meal Image" />
        <div class="meal-info">
          <h4>{{ meal.type }}</h4>
          <p>{{ meal.time }}</p>
          <p>{{ meal.details }}</p>
        </div>
      </div>
    </div>
    <MainFooter />
  </div>
</template>
<script>
import { ref, computed } from "vue";
import MainHeader from "@/components/module/MainHeader.vue";
import MainFooter from "@/components/module/MainFooter.vue";
export default {
  setup() {
    const currentYear = ref(new Date().getFullYear());
    const currentMonth = ref(new Date().getMonth());
    const selectedDate = ref(new Date());
    const selectedTab = ref("meal");

    const days = ["일", "월", "화", "수", "목", "금", "토"];

    const meals = [
      {
        type: "아침",
        details: "닭가슴살 100g, 현미밥 130g, 브로콜리2, 파프리카 조금",
        nutrients: "kcal: 250 | fat: 10g | protein: 30g | carbohydrates: 43g",
      },
      {
        type: "점심",
        details: "닭가슴살 100g, 현미밥 130g, 브로콜리2, 파프리카 조금",
        nutrients: "kcal: 250 | fat: 10g | protein: 30g | carbohydrates: 43g",
      },
      {
        type: "저녁",
        details: "닭가슴살 100g, 현미밥 130g, 브로콜리2, 파프리카 조금",
        nutrients: "kcal: 250 | fat: 10g | protein: 30g | carbohydrates: 43g",
      },
    ];

    const detailedMeals = [
      {
        type: "아침",
        time: "오전 09:30",
        details: "닭가슴살 100g, 현미밥 130g, 브로콜리2, 파프리카 조금",
      },
      {
        type: "점심",
        time: "오전 11:30",
        details: "닭가슴살 100g, 현미밥 130g, 브로콜리2, 파프리카 조금",
      },
      {
        type: "저녁",
        time: "오후 06:30",
        details: "닭가슴살 100g, 현미밥 130g, 브로콜리2, 파프리카 조금",
      },
    ];

    const dates = computed(() => {
      const end = new Date(currentYear.value, currentMonth.value + 1, 0);
      const datesArray = [];
      for (let i = 1; i <= end.getDate(); i++) {
        datesArray.push(new Date(currentYear.value, currentMonth.value, i));
      }
      return datesArray;
    });

    const startDayOfMonth = computed(() => {
      const firstDay = new Date(currentYear.value, currentMonth.value, 1);
      return firstDay.getDay(); // Number of empty cells at the start of the month
    });

    function prevMonth() {
      if (currentMonth.value === 0) {
        currentMonth.value = 11;
        currentYear.value -= 1;
      } else {
        currentMonth.value -= 1;
      }
    }

    function nextMonth() {
      if (currentMonth.value === 11) {
        currentMonth.value = 0;
        currentYear.value += 1;
      } else {
        currentMonth.value += 1;
      }
    }

    function selectDate(date) {
      selectedDate.value = date;
    }

    function isSelectedDate(date) {
      return (
        date.getFullYear() === selectedDate.value.getFullYear() &&
        date.getMonth() === selectedDate.value.getMonth() &&
        date.getDate() === selectedDate.value.getDate()
      );
    }

    function isToday(date) {
      const today = new Date();
      return (
        date.getFullYear() === today.getFullYear() &&
        date.getMonth() === today.getMonth() &&
        date.getDate() === today.getDate()
      );
    }

    return {
      currentYear,
      currentMonth,
      selectedDate,
      selectedTab,
      days,
      meals,
      detailedMeals,
      dates,
      startDayOfMonth,
      prevMonth,
      nextMonth,
      selectDate,
      isSelectedDate,
      isToday,
    };
  },
  components: {
    MainHeader,
    MainFooter,
  },
};
</script>
<style scoped>
.container {
  display: flex;
  grid-template-columns: 1fr 2fr 1fr;
  align-items: center;
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
.days,
.dates {
  display: grid;
  grid-template-columns: repeat(7, 1fr); /* 7열 균등 배치 */
  gap: 0.5rem;
  justify-items: center; /* 그리드 내부 아이템을 수평 가운데 정렬 */
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
  min-width: 430px;
  max-width: 430px;
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
}

.meal-detail img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 10px;
}
.nutrients {
  padding-left: 20px;
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
div.container
  > div.meal-records
  > div:nth-child(1)
  > div.meal-type
  > div.meal-type-square {
  background-color: #bbce8a;
}
div.container
  > div.meal-records
  > div:nth-child(2)
  > div.meal-type
  > div.meal-type-square {
  background-color: #ff7d29;
}
div.container
  > div.meal-records
  > div:nth-child(3)
  > div.meal-type
  > div.meal-type-square {
  background-color: #7b7351;
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
</style>
