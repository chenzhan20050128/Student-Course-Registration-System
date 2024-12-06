import axios from 'axios';

const instance = axios.create({
  baseURL: 'http://localhost:8080', // 根据你的后端服务地址进行调整
  timeout: 1000,
  headers: { 'Content-Type': 'application/json' }
});

export default instance;