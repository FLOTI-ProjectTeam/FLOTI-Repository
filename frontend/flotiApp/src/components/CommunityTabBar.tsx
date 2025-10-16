import { View, Text, Pressable, StyleSheet } from 'react-native';
import { useRouter, useSegments } from 'expo-router';
import { useEffect } from 'react';

import COLORS from '@/constants/colors';

const TAB_ITEMS = [
  { name: 'tip', title: '💡 TIP', path: '/community/tip' },
  { name: 'challenge', title: '🔥 챌린지', path: '/community/challenge' },
  { name: 'discussion', title: '💬 토론', path: '/community/discussion' },
  { name: 'qna', title: '❓ Q&A', path: '/community/qna' },
];

export default function CommunityTabBar() {
  const router = useRouter();
  const segments = useSegments();
  
  // 현재 탭 계산
  const currentTab = segments[segments.length - 1] || "tip";

  // 첫 진입 시 TIP 게시판으로 이동
  useEffect(() => {
    if (currentTab === "community") {
      router.replace("/community/tip");
    }
  }, [segments]);

  // 탭 클릭 이벤트
  const handleTabPress = (path: string) => router.push(path as any);

  return (
    <View style={styles.tabBarContainer}>
      {TAB_ITEMS.map(({ name, title, path }) => {
        const isFocused = currentTab === name;

        return (
          <Pressable key={name} onPress={() => handleTabPress(path)} style={styles.tabItem}>
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
  tabBarContainer: {
    flexDirection: 'row',
    height: 48,
    marginTop: 10,
    marginHorizontal: 10
  },
  tabItem: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center'
  },
  tabLabelContainer: {
    paddingHorizontal: 12,
    paddingVertical: 8
  },
  tabLabelContainerActive: {
    backgroundColor: COLORS.TINT.SLATE_LIGHT,
    borderRadius: 8
  },
  tabLabel: {
    fontSize: 14,
    color: 'black'
  },
  tabLabelActive: {
    fontWeight: 'bold'
  }
});