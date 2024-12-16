//import './assets/main.css'

import { createApp } from 'vue';
import App from './views/App.vue';
import router from './router';
import axios from './http';
// 引入 Element Plus
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import { createPinia } from 'pinia';

const app = createApp(App);
const pinia = createPinia();
app.config.globalProperties.$http = axios;
app.use(pinia);
app.use(router)
app.use(ElementPlus);
app.mount('#app');




