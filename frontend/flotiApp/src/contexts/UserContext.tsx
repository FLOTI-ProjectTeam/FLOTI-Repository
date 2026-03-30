import { createContext, useContext, useEffect, useState } from 'react';

import { userStorage } from '@/utils/storage';
import { ProviderProps, UserContextType } from '@/types/context';

// Context 생성
export const UserContext = createContext<UserContextType | null>(null);

// Provider 컴포넌트 생성: 하위 화면에 로그인 상태 공유
export const UserProvider = ({ children }: ProviderProps) => {
  const [username, setUsername] = useState<string | null>('alice'); // 테스트용

  useEffect(() => {
    const initUser = async () => {
      try {
        const user = await userStorage.getUser();
        if (user?.username && user?.jwt) {
          // [Fix] Ensure apiClient can find the token (Sync user.jwt -> 'jwt')
          const AsyncStorage = require('@react-native-async-storage/async-storage').default;
          await AsyncStorage.setItem('jwt', user.jwt);

          setUsername(user.username);
        } else {
          // [개발용] 자동 로그인 처리 (user1/password) - 제거됨 (실제 로그인 화면 사용)
          // console.log('[Dev] Trying auto-login...');
          // const { login } = require('@/api/authApi'); 
          // const res = await login({ username: 'user1', password: '1234' });
          // if (res.username) setUsername(res.username);
          console.log('[Auth] User not logged in. Waiting for manual login.');
        }
      } catch (e) {
        console.error('[Dev] Auto-login failed:', e);
      }
    };
    initUser();
  }, []);

  return (
    <UserContext.Provider value={{ username, setUsername }}>
      {children}
    </UserContext.Provider>
  );
};

export const useUser = () => {
  const context = useContext(UserContext);
  if (!context) throw new Error('useUser must be used within UserProvider');
  return { user: { username: context.username }, ...context }; // 호환성을 위해 user 객체 랩핑
};