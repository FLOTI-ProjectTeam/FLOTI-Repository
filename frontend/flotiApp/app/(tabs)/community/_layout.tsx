import { Pressable, View, StyleSheet } from 'react-native';
import { usePathname, useRouter, Href } from 'expo-router';

import SearchBar from '@/components/SearchBar';
import CommunityTabBar from '@/components/CommunityTabBar'
import HiddenTab from '@/components/HiddenTab';
import { CommunitySearchProvider } from '@/contexts/CommunitySearchContext';
import STYLES from '@/constants/styles';
import COLORS from '@/constants/colors';
import { IconSymbol } from '@/components/ui/IconSymbol';

export default function CommunityLayout() {
  const router = useRouter();
  const pathname = usePathname();

  // 현재 탭 경로 추출
  const segments = pathname.split("/");
  const currentBoard = segments[2] ?? "tip";

  // 글 작성 화면 이동
  const handleCreate = () => router.push(`/community/${currentBoard}/create` as Href);

  return (
    <CommunitySearchProvider>
      <View style={STYLES.CONTAINER}>
        <SearchBar />
        <CommunityTabBar />
        <HiddenTab />
        
        {/* 글 작성 버튼 */}
        <Pressable style={styles.fab} onPress={handleCreate}>
          <IconSymbol name="pen.plus" size={28} color={'white'} />
        </Pressable>
      </View>
    </CommunitySearchProvider>
  );
}

const styles = StyleSheet.create({
  fab: {
    position: "absolute",
    bottom: 24, // 하단 여백
    right: 20,  // 오른쪽 여백
    width: 56,
    height: 56,
    borderRadius: 28,
    backgroundColor: COLORS.BUTTON.NAVY,
    justifyContent: 'center',
    alignItems: 'center',

    /* 그림자 효과 */
    shadowColor: 'black',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.3,
    shadowRadius: 4.5,
    elevation: 6,
  },
});