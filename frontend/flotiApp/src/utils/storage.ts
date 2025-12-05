import AsyncStorage from '@react-native-async-storage/async-storage';

import { LoginResponse } from '@/types/auth/login';

// 스토리지 CRUD
const storage = {
    set: async (key: string, value: any) => {
      await AsyncStorage.setItem(key, JSON.stringify(value));
    },
  
    get: async <T>(key: string): Promise<T | null> => {
      const json = await AsyncStorage.getItem(key);
      return json ? JSON.parse(json) : null;
    },
  
    remove: async (key: string) => {
      await AsyncStorage.removeItem(key);
    },
  
    clear: async () => {
      await AsyncStorage.clear();
    }
};

// 사용자 관련 스토리지
export const userStorage = {
    setUser: async (user: any) => storage.set('user', user),
    getUser: async () => storage.get<LoginResponse>('user'),
    removeUser: async () => storage.remove('user')
};

// 토큰 관련 스토리지
export const tokenStorage = {
    setToken: async (token: string) => storage.set('token', token),
    getToken: async () => storage.get<string>('token'),
    removeToken: async () => storage.remove('token'),
  
    setRefreshToken: async (token: string) => storage.set('refreshToken', token),
    getRefreshToken: async () => storage.get<string>('refreshToken'),
    removeRefreshToken: async () => storage.remove('refreshToken'),
};