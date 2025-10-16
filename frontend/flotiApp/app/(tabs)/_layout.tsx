import { Platform } from 'react-native';
import { Tabs } from 'expo-router';

import { HapticTab } from '@/components/HapticTab';
import { IconSymbol } from '@/components/ui/IconSymbol';
import COLORS from '@/constants/colors';

export default function TabLayout() {
  return (
    <Tabs
      initialRouteName="index"  // 홈 화면으로 시작
      screenOptions={{
        tabBarInactiveTintColor: COLORS.ICON.SLATE_LIGHT, // 기본 탭 색상
        tabBarActiveTintColor: COLORS.ICON.GRAY_DARK,  // 활성화 탭 색상
        headerShown: false,
        tabBarButton: HapticTab,
        tabBarStyle: Platform.select({
          android: {
            borderTopWidth: 0,  // 구분선 제거
            elevation: 0 // 그림자 제거
          },
          ios: {
            position: 'absolute', // 투명 배경을 사용하여 블러 효과 표시
            borderTopWidth: 0,
            shadowOpacity: 0 // 그림자 제거
          }
        }),
        tabBarLabel: () => null,  // 라벨 제거
      }}
    >
      <Tabs.Screen
        name="community"
        options={{
          tabBarIcon: ({ color }) => <IconSymbol size={26} name="community" color={color} />,
        }}
      />
      <Tabs.Screen
        name="mind-map"
        options={{
          tabBarIcon: ({ color }) => <IconSymbol size={24} name="mindMap" color={color} />,
        }}
      />
      <Tabs.Screen
        name="index"
        options={{
          tabBarIcon: ({ color }) => <IconSymbol size={24} name="house" color={color} />,
        }}
      />
      <Tabs.Screen
        name="report"
        options={{
          tabBarIcon: ({ color }) => <IconSymbol size={28} name="report" color={color} />,
        }}
      />
      <Tabs.Screen
        name="mypage"
        options={{
          tabBarIcon: ({ color }) => <IconSymbol size={26} name="mypage" color={color} />,
        }}
      />
    </Tabs>
  );
}