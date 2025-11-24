import { View, TouchableOpacity } from 'react-native';
import { useRouter, Href, useSegments } from 'expo-router';

import { IconSymbol } from '@/components/ui/IconSymbol';
import HiddenTab from '@/components/ui/HiddenTab';
import SearchBar from '@/components/feature/community/SearchBar';
import CommunityTabBar from '@/components/feature/community/CommunityTabBar'
import { CommunitySearchProvider } from '@/contexts/CommunitySearchContext';
import { STYLE } from '@/constants/styles';

export default function CommunityLayout() {
  const router = useRouter();
  const segments = useSegments(); // ['(tabs)', 'community', 'tip']
  const currentTab: string = segments[2] || 'tip';

  /* 이벤트 핸들러 */
  const handleGoToCreate = () => router.push(`/community/${currentTab}/create` as Href)

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