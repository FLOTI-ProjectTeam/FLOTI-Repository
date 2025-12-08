import { createContext, useEffect, useState } from 'react';

import { ProviderProps, UserContextType } from '@/types/context';

import { userStorage } from '@/utils/storage';

// Context 생성
export const UserContext = createContext<UserContextType | null>(null);

// Provider 컴포넌트 생성: 하위 화면에 로그인 상태 공유
export const UserProvider = ({ children }: ProviderProps) => {
  const [username, setUsername] = useState<string | null>('writer92'); // 테스트용

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