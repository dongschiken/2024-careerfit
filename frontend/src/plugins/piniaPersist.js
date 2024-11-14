// plugins/piniaPersist.js
export function piniaPersistPlugin({ store }) {
  // 스토어가 초기화될 때 로컬 스토리지에서 페이지를 가져옴
  const savedPage = localStorage.getItem(store.$id);
  if (savedPage) {
    store.$patch(JSON.parse(savedPage));
  }

  // 스토어가 업데이트될 때마다 로컬 스토리지에 저장
  store.$subscribe((mutation, state) => {
    localStorage.setItem(store.$id, JSON.stringify(state));
  });
}
