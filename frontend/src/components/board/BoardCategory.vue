<template>
  <div>
    <div v-for="n in 10" :key="n" class="board-content-list-group">
      <div class="board-content-left">
        <div class="board-content-title">제 스펙에 연봉 얼마가 적당한가요?</div>
        <div class="board-content-content">
          <p class="ellipsis">
            Lorem ipsum dolor sit amet consectetur adipisicing elit. Adipisci
            sunt dignissimos unde officiis sapiente commodi, obcaecati repellat
            ex similique hic.
          </p>
        </div>
        <div class="user-profile-group">
          <div class="user-profile-img">
            <img src="@/assets/img/snoopy.png" alt="" />
          </div>
          <div class="user-profile-name">동스치킨</div>
        </div>
        <div class="board-content-left-category">회사생활</div>
      </div>
      <div class="board-content-right">
        <div class="board-content-right-img">
          <img src="@/assets/img/snoopy.png" alt="" />
        </div>
        <div class="board-content-right-footer">
          <span class="date-time">약 6시간 전</span>
          <span class="like-count">
            <img
              src="@/assets/img/thumb_up_24dp_5F6368_FILL0_wght400_GRAD0_opsz24.png"
              alt=""
            />
            <span>10개</span>
          </span>
          <span class="reply-count">
            <img
              src="@/assets/img/chat_24dp_5F6368_FILL0_wght400_GRAD0_opsz24.png"
              alt=""
            />
            <span>10개</span>
          </span>
          <span class="view-count">
            <img
              src="@/assets/img/visibility_24dp_5F6368_FILL0_wght400_GRAD0_opsz24.png"
              alt=""
            />
            <span>10개</span>
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="css" scoped>
@import url(@/assets/css/board-category.css);
</style>
