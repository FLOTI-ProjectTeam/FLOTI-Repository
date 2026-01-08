import { View, TouchableOpacity } from 'react-native';
import { useSegments } from 'expo-router';

import { useNavigation } from '@/hooks/useNavigation';
import { CommunitySearchProvider } from '@/contexts/CommunitySearchContext';

import { IconSymbol } from '@/components/ui/IconSymbol';
import HiddenTab from '@/components/ui/HiddenTab';
import SearchBar from '@/components/feature/community/SearchBar';
import CommunityTabBar from '@/components/feature/community/CommunityTabBar';

import { STYLE } from '@/constants/styles';

export default function CommunityLayout() {
  const { navigateTo } = useNavigation();

  const segments = useSegments(); // ['(tabs)', 'community', 'tip']
  const currentTab = segments[2] || 'tip';

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
          onPress={() => navigateTo(`/community/${currentTab}/create`)}
        >
          <IconSymbol name="plus.pen" size={28} color={'white'} />
        </TouchableOpacity>
      </View>
    </CommunitySearchProvider>
  );
}