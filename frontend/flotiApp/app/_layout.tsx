import './global.css';

import { SafeAreaProvider, useSafeAreaInsets } from 'react-native-safe-area-context';
import Toast, { ToastConfig } from 'react-native-toast-message';  // Android & iOS 공용 토스트 메시지
import { useFonts } from 'expo-font';
import { Stack } from 'expo-router';
import * as SplashScreen from 'expo-splash-screen';
import { StatusBar } from 'expo-status-bar';
import { useEffect } from 'react';
import { View, Text, StyleSheet, Platform } from 'react-native';
import 'react-native-reanimated';

import COLOR from '@/constants/colors';

// 리소스 로딩이 완료될 때까지 스플래시 화면 유지
SplashScreen.preventAutoHideAsync();

export default function RootLayout() {
  const insets = useSafeAreaInsets(); // SafeArea 정보
  const [loaded] = useFonts({
    SpaceMono: require('../assets/fonts/SpaceMono-Regular.ttf'),
  });

  useEffect(() => {
    if (loaded) SplashScreen.hideAsync();
  }, [loaded]);

  if (!loaded) return null;

  return (
    <SafeAreaProvider>
      {/* 스테이터스 바 */}
      <StatusBar style='light' />
      <View style={[styles.topSafeArea, { height: insets.top }]} />

      {/* 모든 화면에서 헤더 숨김 */}
      <Stack screenOptions={{ headerShown: false }}>
        <Stack.Screen name="(tabs)" />
      </Stack>

      {/* 네비게이션 바 배경 */}
      {Platform.OS === 'android' && insets.bottom > 0 && (
        <View style={[styles.bottomSafeArea, { height: insets.bottom }]} />
      )}

      {/* 토스트 메시지 */}
      <Toast config={toastConfig} />
    </SafeAreaProvider>
  );
}

// 커스텀 토스트 메시지
const toastConfig: ToastConfig = {
  custom_success: ({ props }) => (
    <View style={styles.toastContainer}>
      <Text style={styles.toastText}>{props.message}</Text>
    </View>
  ),
};

const styles = StyleSheet.create({
  topSafeArea: { backgroundColor: 'black' },
  bottomSafeArea: {
    position: 'absolute',
    bottom: 0,
    left: 0, right: 0,
    backgroundColor: 'black'
  },
  toastContainer: {
    marginBottom: 30,
    paddingVertical: 8, paddingHorizontal: 16,
    alignItems: 'center',
    borderRadius: 20,
    backgroundColor: COLOR.OVERLAY
  },
  toastText: { fontSize: 14, color: 'white' }
});