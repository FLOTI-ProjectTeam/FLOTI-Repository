import { View, Text, Pressable, StyleSheet } from 'react-native';
import { useSegments } from 'expo-router';
import { useEffect } from 'react';

import { useNavigation } from '@/hooks/useNavigation';
import COLOR from '@/constants/colors';

const TAB_ITEMS = [
  { name: 'tip', title: '💡 TIP', path: '/community/tip' },
  { name: 'challenge', title: '🔥 챌린지', path: '/community/challenge' },
  { name: 'discussion', title: '💬 토론', path: '/community/discussion' },
  { name: 'qna', title: '❓ Q&A', path: '/community/qna' },
];

export default function CommunityTabBar() {
  const { navigateTo } = useNavigation();
  const segments = useSegments(); // ['(tabs)', 'community', 'tip']
  const currentTab = segments[segments.length - 1] || 'tip';

  // 최초 진입 시 TIP 탭으로 이동
  useEffect(() => {
    if (currentTab === 'community') navigateTo('/community/tip');
  }, [segments]);

  /* 이벤트 핸들러 */
  const handleGoToTab = (path: string) => navigateTo(path);

  return (
    <View style={styles.tabContainer}>
      {TAB_ITEMS.map(({ name, title, path }) => {
        const isFocused = currentTab === name;

        return (
          <Pressable key={name} style={styles.tabItem} onPress={() => handleGoToTab(path)}>
            <View style={[styles.tabLabelContainer, isFocused && styles.tabLabelContainerActive]}>
              <Text style={[styles.tabLabel, isFocused && styles.tabLabelActive]}>{title}</Text>
            </View>
          </Pressable>
        );
      })}
    </View>
  );
}

const styles = StyleSheet.create({
  tabContainer: {
    flexDirection: 'row',
    height: 48,
    marginTop: 10,
    marginHorizontal: 10
  },
  tabItem: { flex: 1, justifyContent: 'center', alignItems: 'center' },
  tabLabelContainer: { paddingHorizontal: 12, paddingVertical: 8 },
  tabLabelContainerActive: { backgroundColor: COLOR.TINT.SLATE_LIGHT, borderRadius: 8 },
  tabLabel: { fontSize: 14, color: 'black' },
  tabLabelActive: { fontWeight: 700 }
});