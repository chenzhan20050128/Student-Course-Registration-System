import { createRouter, createWebHistory } from 'vue-router';
import Login from '../views/Login.vue';
import Register from '../views/Register.vue';
import AdminHome from '../views/AdminHome.vue';
import TeacherHome from '../views/TeacherHome.vue';
import StudentHome from '../views/StudentHome.vue';

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
    path: '/register',
    name: 'Register',
    component: Register
  },
  {
    path: '/admin-home',
    name: 'AdminHome',
    component: AdminHome
  },
  {
    path: '/teacher-home',
    name: 'TeacherHome',
    component: TeacherHome
  },
  {
    path: '/student-home',
    name: 'StudentHome',
    component: StudentHome
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;