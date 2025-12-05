/**
 * Context란?
 * - 컴포넌트 간에 데이터를 전역으로 공유할 수 있는 방법
 * - Provider를 통해 데이터를 하위 컴포넌트에 전달하고, Hook을 통해 언제든 접근 가능
 */

export type ProviderProps = {
  children: React.ReactNode;
};

// 사용자 상태
export type UserContextType = {
  username: string | null;
  setUsername: React.Dispatch<React.SetStateAction<string | null>>;
};

// 검색어 상태
export type SearchContextType = {
  search: string; // 입력 중인 검색어
  setSearch: (search: string) => void;
  searchTrigger: string;  // 실제 사용할 검색어
  setSearchTrigger: (searchTrigger: string) => void;
};