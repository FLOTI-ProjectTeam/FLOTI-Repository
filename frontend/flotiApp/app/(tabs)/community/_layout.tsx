import { Tabs } from 'expo-router';

export default function CommunityLayout() {
  return (
    <Tabs
      initialRouteName="tip"
      screenOptions={{ 
        headerShown: false,
        tabBarIcon: () => null, // 아이콘 제거
        tabBarPosition: 'top', // 상단 탭으로 변경
    }}>
      <Tabs.Screen name="tip" options={{ title: '💡 TIP' }} />
      <Tabs.Screen name="challenge" options={{ title: '🔥 챌린지' }} />
      <Tabs.Screen name="discussion" options={{ title: '💬 토론' }} />
      <Tabs.Screen name="qna" options={{ title: '❓ Q&A' }} />
    </Tabs>
  );
}
