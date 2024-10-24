import { createRouter, createWebHashHistory } from 'vue-router'
import MainView from '@/views/MainView.vue'
import BoardListView from '@/components/board/BoardListView.vue'

const routes = [
  {
    path: '/',
    name: 'main',
    component: MainView
  },
  {
    path: '/board',
    name: 'board',
    component: BoardListView
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router
