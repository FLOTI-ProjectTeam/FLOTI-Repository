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
  const segments = useSegments(); // ["(tabs)", "community", "tip"]
  const currentTab = segments[2] || 'tip';

  // 작성 화면 이동
  const goToPostCreateScreen = () => router.push(`/community/${currentTab}/create` as Href);

  return (
    <CommunitySearchProvider>
      <View style={STYLE.BASE_CONTAINER}>
        <SearchBar />
        <CommunityTabBar />
        <HiddenTab />
        
        {/* 작성 버튼 */}
        <TouchableOpacity activeOpacity={0.8} style={STYLE.FAD} onPress={goToPostCreateScreen}>
          <IconSymbol name="plus.pen" size={28} color={'white'} />
        </TouchableOpacity>
      </View>
    </CommunitySearchProvider>
  );
}