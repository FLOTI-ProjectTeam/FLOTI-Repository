import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';

import { BASE_URL } from '@/constants/endpoints';

// Axios 인스턴스 생성: API 호출 시 공통 설정 적용
const apiClient = axios.create({
    baseURL: BASE_URL,
    timeout: 5000,
});

// 모든 요청에 JWT 토큰 자동 추가
apiClient.interceptors.request.use(config => {
    const token = AsyncStorage.getItem('jwt');
    if (token)
        config.headers.Authorization = `Bearer ${token}`;
    return config;
});

export default apiClient;