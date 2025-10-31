/**
 * Context란?
 * - 컴포넌트 간에 데이터를 전역으로 공유할 수 있는 방법
 * - Provider를 통해 데이터를 하위 컴포넌트에 전달하고, Hook을 통해 언제든 접근 가능
 */

// 커뮤니티 검색어 상태
export type CommunitySearchContextType = {
  search: string; // 입력 중인 검색어
  setSearch: (value: string) => void;
  searchTrigger: string;  // 실제 사용할 검색어
  setSearchTrigger: (value: string) => void;
};