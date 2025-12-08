import { createContext, useContext, useEffect, useState } from 'react';

import { userStorage } from '@/utils/storage';
import { ProviderProps, UserContextType } from '@/types/context';

// Context 생성
export const UserContext = createContext<UserContextType | null>(null);

// Provider 컴포넌트 생성: 하위 화면에 로그인 상태 공유
export const UserProvider = ({ children }: ProviderProps) => {
  const [username, setUsername] = useState<string | null>('alice'); // 테스트용

  useEffect(() => {
    userStorage.getUser().then(user => {
      if (user?.username) setUsername(user.username);
    });
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