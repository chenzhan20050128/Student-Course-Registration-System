import { createRouter, createWebHistory } from 'vue-router';
import Login from '../components/Login.vue';
import Register from '../components/Register.vue';
import Home from '../views/Home.vue';

const routes = [
    {
        path: '/',
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
        path: '/home',
        name: 'Home',
        component: Home,
        meta: { requiresAuth: true }
    }
];

const router = createRouter({
    history: createWebHistory(),
    routes
});

// 路由守卫：检查登录状态
router.beforeEach((to, from, next) => {
    const isLoggedIn = localStorage.getItem('user');

    if (to.meta.requiresAuth && !isLoggedIn) {
        // 需要登录但未登录，跳转到登录页
        next('/login');
    } else if ((to.path === '/login' || to.path === '/register') && isLoggedIn) {
        // 已登录但访问登录或注册页，跳转到主页
        next('/home');
    } else {
        next();
    }
});

export default router;
