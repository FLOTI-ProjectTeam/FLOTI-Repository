import { View, TouchableOpacity } from 'react-native';
import { useRouter, Href, useSegments } from 'expo-router';

import SearchBar from '@/components/SearchBar';
import CommunityTabBar from '@/components/CommunityTabBar'
import HiddenTab from '@/components/ui/HiddenTab';
import { CommunitySearchProvider } from '@/contexts/CommunitySearchContext';
import { STYLE } from '@/constants/styles';
import { IconSymbol } from '@/components/ui/IconSymbol';

export default function CommunityLayout() {
  const router = useRouter();
  const segments = useSegments(); // ['(tabs)', 'community', 'tip']
  const currentTab = segments[2] || 'tip';

  return (
    <CommunitySearchProvider>
      <View style={STYLE.BASE_CONTAINER}>
        <SearchBar />
        <CommunityTabBar />
        <HiddenTab />
        
        {/* 작성(등록) 버튼 */}
        <TouchableOpacity 
          activeOpacity={0.8} // 클릭 시 투명도 설정
          style={STYLE.FAD} 
          onPress={() => router.push(`/community/${currentTab}/create` as Href)}  // 작성(등록) 화면 이동
        >
          <IconSymbol name="plus.pen" size={28} color={'white'} />
        </TouchableOpacity>
      </View>
    </CommunitySearchProvider>
  );
}