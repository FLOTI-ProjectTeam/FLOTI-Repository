import { View, Text, Pressable, StyleSheet } from 'react-native';
import { useRouter, useSegments } from 'expo-router';

import COLORS from '@/constants/colors';

const TAB_ITEMS = [
  { name: 'tip', title: '💡 TIP', path: '/community/tip' },
  { name: 'challenge', title: '🔥 챌린지', path: '/community/challenge' },
  { name: 'discussion', title: '💬 토론', path: '/community/discussion' },
  { name: 'qna', title: '❓ Q&A', path: '/community/qna' },
];

function CommunityTabBar() {
  const router = useRouter();
  const segments = useSegments();
  
  const currentTab = segments[segments.length - 1] || 'tip';

  const handleTabPress = (tabName: string, path: string) => {
    router.push(path as any);
  };

  return (
    <View style={styles.tabBarContainer}>
      {TAB_ITEMS.map((tab) => {
        const isFocused = currentTab === tab.name;

        return (
          <Pressable
            key={tab.name}
            onPress={() => handleTabPress(tab.name, tab.path)}
            style={styles.tabItem}
          >
            <View style={[
              styles.tabLabelContainer,
              isFocused && styles.tabLabelContainerActive
            ]}>
              <Text
                style={[
                  styles.tabLabel,
                  isFocused && styles.tabLabelActive
                ]}
              >
                {tab.title}
              </Text>
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
    alignItems: 'center',
  },
  tabLabelContainer: {
    paddingHorizontal: 12,
    paddingVertical: 8,
    borderRadius: 16
  },
  tabLabelContainerActive: {
    backgroundColor: COLORS.TINT.LIGHT_POWDER,
  },
  tabLabel: {
    fontSize: 14,
    fontWeight: '500',
    color: COLORS.BLACK,
  },
  tabLabelActive: {
    fontWeight: '800',
  },
});

export default CommunityTabBar;