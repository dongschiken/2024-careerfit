<template>
  <footer class="bottom-footer-container">
    <div class="page-logo">
      <h1><router-link to="/">CAREER FIT</router-link></h1>
    </div>

    <div class="bottom-footer-company-info-sector">
      <div class="bottom-footer-menu-group">
        <div>회사소개</div>
        <div>공지사항</div>
        <div>광고문의</div>
        <div>|</div>
        <div>버그제보</div>
        <div>운영정책</div>
      </div>
      <ul class="bottom-footer-company-info-ul">
        <li>
          <span>주소</span>
          <div>
            <p>대전시 00동 00구, 0층</p>
          </div>
        </li>
        <li>
          <span>전화번호</span>
          <div>
            <p>010-0000-0000</p>
          </div>
        </li>
        <li>
          <span>EMAIL</span>
          <div>
            <p>peach@co.kr</p>
          </div>
        </li>
      </ul>
      <div style="color: #b6aaca; margin-top: 40px; text-align: start">
        COPYRIGHT ⓒ (주)peach CO.,LTD. All Rights Reserved.
      </div>
    </div>
    <div class="bottom-footer-icons-sector">
      <div>개인정보 처리방침</div>
      <div>인스타그램</div>
      <div>유튜브</div>
      <div>지메일</div>
    </div>
  </footer>
</template>

<script></script>

<style lang="css">
@import url(../../assets/css/footer.css);
.bottom-footer-container {
  border-top: 1px solid grey;
}
.bottom-footer-menu-group {
  display: flex;
  margin-bottom: 100px;
  margin-top: 15px;
}
.bottom-footer-menu-group div {
  margin-right: 20px;
}
.bottom-footer-company-info-sector > .bottom-footer-menu-group {
  color: #757575;
}
.page-logo > h1 > a {
  text-decoration: none;
  color: #ff7f32;
}
</style>
