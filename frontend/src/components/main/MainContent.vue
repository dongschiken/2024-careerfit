<template>
  <div class="main-container">
    <div class="main-layout-container">
      <div class="main-intro-group">
        <div class="main-intro">
          <div><h2>성공과 건강을 위한 스마트한 선택</h2></div>
          <div class="main-intro-small">
            직장인들을 위한 헬스케어<br />
            오직 <span class="highlight-orange">CAREER FIT</span> 에서
          </div>
        </div>
        <div class="main-ai-chatbot" id="chatbot">
          <svg
            style="width: 40px; height: 43px; margin-left: 5px"
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 24 24"
            fill="currentColor"
            stroke-width="0.5"
          >
            <path
              d="M10 3H14C18.4183 3 22 6.58172 22 11C22 15.4183 18.4183 19 14 19V22.5C9 20.5 2 17.5 2 11C2 6.58172 5.58172 3 10 3ZM12 17H14C17.3137 17 20 14.3137 20 11C20 7.68629 17.3137 5 14 5H10C6.68629 5 4 7.68629 4 11C4 14.61 6.46208 16.9656 12 19.4798V17Z"
            ></path>
            <text x="12" y="14" text-anchor="middle" fill="white" font-size="8">
              AI
            </text>
          </svg>
        </div>
      </div>

      <div class="main-board-group">
        <div class="comunity-group">
          <div class="comunity-header">
            <div class="comunity-header-text">
              <router-link to="/board">커뮤니티</router-link>
            </div>
          </div>
          <div v-for="n in 5" :key="n" class="main-board-content-group">
            <div class="main-board-content-layout">
              <div class="main-board-content-left">
                <div class="profile-img">
                  <img src="../../assets/img/snoopy.png" alt="" />
                </div>
                <div class="nickname">초대리</div>
                <div class="material-icon">
                  <img
                    src="../../assets/img/edit_square_24dp_5F6368_FILL0_wght400_GRAD0_opsz24.png"
                    alt=""
                  />
                </div>
                <div class="date-time">6분 전</div>
              </div>
              <div class="main-board-content-right">
                <div class="material-icon">
                  <img
                    src="../../assets/img/thumb_up_24dp_5F6368_FILL0_wght400_GRAD0_opsz24.png"
                    alt=""
                  />
                  <div>24</div>
                </div>
                <div class="material-icon">
                  <img
                    src="../../assets/img/chat_24dp_5F6368_FILL0_wght400_GRAD0_opsz24.png"
                    alt=""
                  />
                  <div>24</div>
                </div>
              </div>
            </div>
            <div class="contents">데이터 솔루션에 대해 들어보신분??</div>
          </div>
        </div>

        <div class="comunity-group">
          <div class="comunity-header">
            <div class="comunity-header-text">
              <router-link to="/board">커뮤니티</router-link>
            </div>
          </div>
          <div v-for="n in 5" :key="n" class="main-board-content-group">
            <div class="main-board-content-layout">
              <div class="main-board-content-left">
                <div class="profile-img">
                  <img src="../../assets/img/snoopy.png" alt="" />
                </div>
                <div class="nickname">초대리</div>
                <div class="material-icon">
                  <img
                    src="../../assets/img/edit_square_24dp_5F6368_FILL0_wght400_GRAD0_opsz24.png"
                    alt=""
                  />
                </div>
                <div class="date-time">6분 전</div>
              </div>
              <div class="main-board-content-right">
                <div class="material-icon">
                  <img
                    src="../../assets/img/thumb_up_24dp_5F6368_FILL0_wght400_GRAD0_opsz24.png"
                    alt=""
                  />
                  <div>24</div>
                </div>
                <div class="material-icon">
                  <img
                    src="../../assets/img/chat_24dp_5F6368_FILL0_wght400_GRAD0_opsz24.png"
                    alt=""
                  />
                  <div>24</div>
                </div>
              </div>
            </div>
            <div class="contents">데이터 솔루션에 대해 들어보신분??</div>
          </div>
        </div>
      </div>
      <!-- <div class="main-health-ranking-group">헬스장 랭킹</div> -->
    </div>
  </div>
</template>

<script></script>

<style lang="css">
@import url(../../assets/css/main-content.css);
.comunity-header-text > a {
  text-decoration: none;
  color: black;
}
</style>
