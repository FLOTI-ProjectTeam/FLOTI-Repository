import { View, TouchableOpacity } from 'react-native';
import { useSegments } from 'expo-router';

import { useNavigation } from '@/hooks/useNavigation';
import { CommunitySearchProvider } from '@/contexts/CommunitySearchContext';

import { IconSymbol } from '@/components/ui/IconSymbol';
import HiddenTab from '@/components/ui/HiddenTab';
import SearchBar from '@/components/feature/community/SearchBar';
import CommunityTabBar from '@/components/feature/community/CommunityTabBar'

import { STYLE } from '@/constants/styles';

export default function CommunityLayout() {
  const { navigateTo } = useNavigation();

  const segments = useSegments(); // ['(tabs)', 'community', 'tip']
  const currentTab = segments[2] || 'tip';

  /* 이벤트 핸들러 */
  const handleGoToCreate = () => {
    if (currentTab === 'challenge') {
      navigateTo('/community/challenge/create'); // 챌린지는 별도 페이지
    } else {
      navigateTo(`/community/${currentTab}/create`); // 팁/질문 등은 모달? or 기존 로직 유지
    }
  };

  return (
    <CommunitySearchProvider>
      <View style={STYLE.BASE_CONTAINER}>
        <SearchBar />
        <CommunityTabBar />
        <HiddenTab />

        {/* 등록 버튼 */}
        <TouchableOpacity
          style={STYLE.FAD}
          activeOpacity={0.8} // 클릭 시 투명도 설정
          onPress={handleGoToCreate}
        >
          <IconSymbol name="plus.pen" size={28} color={'white'} />
        </TouchableOpacity>
      </View>
    </CommunitySearchProvider>
  );
}