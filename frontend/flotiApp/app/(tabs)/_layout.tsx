import { Tabs } from 'expo-router';
import React from 'react';
import { Platform } from 'react-native';

import { HapticTab } from '@/src/components/HapticTab';
import { IconSymbol } from '@/src/components/ui/IconSymbol';
import TabBarBackground from '@/src/components/ui/TabBarBackground';
import { Colors } from '@/src/constants/Colors';
import { useColorScheme } from '@/src/hooks/useColorScheme';

export default function TabLayout() {
  const colorScheme = useColorScheme();

  return (
    <Tabs
      screenOptions={{
        tabBarInactiveTintColor: '#B0C4DE', // 기본 탭 색상
        tabBarActiveTintColor: '#153257',   // 활성화된 탭 색상
        headerShown: false,
        tabBarButton: HapticTab,
        tabBarBackground: TabBarBackground,
        tabBarStyle: Platform.select({
          ios: {
            // Use a transparent background on iOS to show the blur effect
            position: 'absolute',
          },
          default: {},
        }),
        tabBarLabel: () => null,  // 라벨 제거
      }}
      >
      <Tabs.Screen
        name="community"
        options={{
          tabBarIcon: ({ color }) => <IconSymbol size={28} name="community.fill" color={color} />,
        }}
      />
      <Tabs.Screen
        name="mindmap"
        options={{
          tabBarIcon: ({ color }) => <IconSymbol size={28} name="mindmap.fill" color={color} />,
        }}
      />
      <Tabs.Screen
        name="home"
        options={{
          tabBarIcon: ({ color }) => <IconSymbol size={28} name="house.fill" color={color} />,
        }}
      />
      <Tabs.Screen
        name="report"
        options={{
          tabBarIcon: ({ color }) => <IconSymbol size={28} name="report.fill" color={color} />,
        }}
      />
      <Tabs.Screen
        name="mypage"
        options={{
          tabBarIcon: ({ color }) => <IconSymbol size={28} name="mypage.fill" color={color} />,
        }}
      />
    </Tabs>
  );
}
