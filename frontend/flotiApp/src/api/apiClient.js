import axios from 'axios';
import { BASE_URL } from '@/constants/api';

/* axios 인스턴스 생성 */
const apiClient = axios.create({
    baseURL: BASE_URL,
    timeout: 5000,
});

/* 모든 요청 전에 JWT 토큰 자동 추가 */
apiClient.interceptors.request.use(config => {
    const jwt = localStorage.getItem('jwt'); 
    if (jwt) config.headers.Authorization = `Bearer ${jwt}`;
    return config;
});

export default apiClient;