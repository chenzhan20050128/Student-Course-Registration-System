//import './assets/main.css'

import { createApp } from 'vue';
import App from './views/App.vue';
import router from './router';
import axios from './http';
// 引入 Element Plus
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';

const app = createApp(App);
app.config.globalProperties.$http = axios;
app.use(router).mount('#app');
app.use(ElementPlus);
