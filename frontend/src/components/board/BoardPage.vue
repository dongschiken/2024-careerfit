<template>
  <div class="pagination">
    <div class="next-prev-button">
      <img
        class="prev-btn"
        src="@/assets/img/arrow_back_ios_24dp_5F6368_FILL0_wght400_GRAD0_opsz24.png"
        alt=""
      />
      <a href="#" class="prev">Prev</a>
    </div>
    <div>
      <a href="#" class="first">First</a>
      <span>...</span>
      <a href="#" class="page-number">1</a>
      <a href="#" class="page-number">2</a>
      <a href="#" class="page-number">3</a>
      <a href="#" class="page-number">4</a>
      <a href="#" class="page-number active">5</a>
      <a href="#" class="page-number">6</a>
      <a href="#" class="page-number">7</a>
      <a href="#" class="page-number">8</a>
      <a href="#" class="page-number">9</a>
      <a href="#" class="page-number">10</a>
      <span>...</span>
      <a href="#" class="last">Last</a>
    </div>
    <div class="next-prev-button">
      <a href="#" class="next">Next</a>
      <img
        class="next-btn"
        src="@/assets/img/arrow_forward_ios_24dp_5F6368_FILL0_wght400_GRAD0_opsz24.png"
      />
    </div>
  </div>
</template>

<script setup></script>

<style lang="css">
.pagination {
  min-width: 800px;
  max-width: 800px;
  display: flex;
  align-items: center;
  font-family: Arial, sans-serif;
  justify-content: space-between;
}
div > .next-btn {
  width: 20px;
  height: 20px;
}
div > .prev-btn {
  width: 20px;
  height: 20px;
}
.pagination a {
  text-decoration: none;
  color: #666;
  padding-left: 5px;
  padding-right: 5px;
  border-radius: 4px;
  transition: background-color 0.3s ease;
}

.pagination .page-number:hover,
.first:hover,
.last:hover {
  background-color: #ddd;
}

.next-prev-button:hover {
  background-color: #ddd;
}

.pagination .prev,
.pagination .next,
.pagination .first,
.pagination .last {
  font-weight: bold;
  color: #000;
}

.pagination .page-number.active {
  font-weight: bold;
  color: #000;
}

.pagination span {
  color: #666;
}
.page-number {
  margin: 5px 5px;
}
.next-prev-button {
  display: flex;
  align-content: end;
  justify-content: end;
  align-items: end;
  text-align: end;
}
</style>
