import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

/**
 * 获取所有选课轮次
 */
export const getAllBatches = () => {
    return axios.get(`${API_BASE_URL}/elective-batches`);
};

/**
 * 获取当前进行中的选课轮次
 */
export const getCurrentBatch = () => {
    return axios.get(`${API_BASE_URL}/elective-batches/current`);
};

/**
 * 根据ID获取选课轮次详情
 */
export const getBatchById = (id) => {
    return axios.get(`${API_BASE_URL}/elective-batches/${id}`);
};
