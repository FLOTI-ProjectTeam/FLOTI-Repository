import './global.css';

import { SafeAreaProvider, SafeAreaView } from 'react-native-safe-area-context';
import { useFonts } from 'expo-font';
import { Stack } from 'expo-router';
import * as SplashScreen from 'expo-splash-screen';
import { StatusBar } from 'expo-status-bar';
import { useEffect } from 'react';
import 'react-native-reanimated';

import STYLES from '@/constants/styles';

// 리소스 로딩이 완료될 때까지 스플래시 화면 유지
SplashScreen.preventAutoHideAsync();

export default function RootLayout() {
  const [loaded] = useFonts({
    SpaceMono: require('../assets/fonts/SpaceMono-Regular.ttf'),
  });

  useEffect(() => {
    if (loaded) SplashScreen.hideAsync();
  }, [loaded]);

  if (!loaded) return null;

  return (
    <SafeAreaProvider>
      {/* 상태바 글자를 검은색으로 설정 */}
      <StatusBar style="dark" />
      {/* 상단 영역 확보 */}
      <SafeAreaView style={STYLES.CONTAINER} edges={['top']}>
        {/* 모든 화면에서 헤더 숨김 */}
        <Stack screenOptions={{headerShown: false}}>
          <Stack.Screen name="(tabs)" />
        </Stack>
      </SafeAreaView>
    </SafeAreaProvider>
  );
}