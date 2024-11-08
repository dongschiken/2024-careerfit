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

<style lang="css">
.board-content-content > .ellipsis {
  max-width: 400px;
  min-width: 400px;
  max-height: 36px;
  min-height: 36px;
  margin-top: 60px;
  text-align: start;
  display: -webkit-box;
  -webkit-line-clamp: 2; /* 표시할 줄 수 */
  overflow: hidden;
  -webkit-box-orient: vertical;
  text-overflow: ellipsis;
  font-weight: 400;
}
.board-content-list-group {
  display: flex;
  margin-top: 35px;
  border-top: 1px solid gray;
  min-width: 800px;
  max-width: 800px;
  justify-content: space-between;
}
.user-profile-group {
  display: flex;
  padding-top: 37px;
}
.user-profile-group:hover {
  cursor: pointer;
}
.board-content-title {
  margin-top: 15px;
  font-size: 19px;
  text-align: start;
}
.user-profile-img > img {
  width: 35px;
  height: 35px;
  border-radius: 50%;
}
.user-profile-name {
  align-content: center;
  margin-left: 15px;
  font-weight: 600;
}
.board-content-left-category {
  display: flex;
  padding-top: 10px;
  justify-content: start;
  font-weight: 400;
}
.board-content-right > .board-content-right-img > img {
  margin-top: 20px;
  width: 160px;
  height: 150px;
  border-radius: 10px;
}
.board-content-right-footer {
  margin-top: 52px;
  display: flex;
}
.board-content-right-footer > span > img {
  width: 20px;
  height: 20px;
}
.date-time {
  display: flex;
  align-items: end;
  font-weight: 400;
}

.board-content-right-img {
  display: flex;
  justify-content: end;
}
.like-count,
.reply-count,
.view-count {
  display: flex;
  margin-left: 15px;
  align-items: end;
}

.like-count > span {
  padding-left: 5px;
  font-weight: 400;
}
.reply-count > span {
  padding-left: 5px;
  font-weight: 400;
}
.view-count > span {
  padding-left: 5px;
  font-weight: 400;
}
</style>
