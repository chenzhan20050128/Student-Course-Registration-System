import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

/**
 * 用户登录
 */
export const login = (loginData) => {
    return axios.post(`${API_BASE_URL}/auth/login`, loginData);
};

/**
 * 用户登出
 */
export const logout = () => {
    return axios.post(`${API_BASE_URL}/auth/logout`);
};

/**
 * 学生注册
 */
export const register = (registerData) => {
    return axios.post(`${API_BASE_URL}/auth/register`, registerData);
};
