import { createContext, useContext, useState, ReactNode } from 'react';

import { CommunitySearchContextType } from '@/types/context';

// Context 생성
const CommunitySearchContext = createContext<CommunitySearchContextType | undefined>(undefined);

// Provider 컴포넌트 생성: 하위 화면에 검색어 상태 공유
export const CommunitySearchProvider = ({ children }: { children: ReactNode }) => {
  const [search, setSearch] = useState('');
  const [searchTrigger, setSearchTrigger] = useState('');

  return (
    <CommunitySearchContext.Provider value={{ search, setSearch, searchTrigger, setSearchTrigger }}>
      {children}
    </CommunitySearchContext.Provider>
  );
};

// Hook 생성: Context 접근
export const useCommunitySearch = () => {
  const context = useContext(CommunitySearchContext);
  if (!context) throw new Error('useCommunitySearch는 CommunitySearchProvider 내에서 사용해야 합니다.');
  return context;
};