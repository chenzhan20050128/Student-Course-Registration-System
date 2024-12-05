import { createRouter, createWebHistory } from 'vue-router';
import Login from '../view/Login.vue';
import App from '../view/App.vue';

const routes = [
  {
    path: '/',
    name: 'Home',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/app',
    name: 'App',
    component: App
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;