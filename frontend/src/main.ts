//import './assets/main.css'

import { createApp } from 'vue';
import App from './views/App.vue';
import router from './router';
import axios from './http';

const app = createApp(App);
app.config.globalProperties.$http = axios;
app.use(router).mount('#app');
