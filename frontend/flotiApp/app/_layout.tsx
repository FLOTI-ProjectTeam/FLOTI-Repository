import './global.css';

import { SafeAreaProvider, useSafeAreaInsets } from 'react-native-safe-area-context';
import Toast, { ToastConfig } from 'react-native-toast-message';  // Android & iOS 공용 토스트 메시지
import { useFonts } from 'expo-font';
import { Stack } from 'expo-router';
import * as SplashScreen from 'expo-splash-screen';
import { StatusBar } from 'expo-status-bar';
import { View, Text, StyleSheet, Platform } from 'react-native';
import { useEffect } from 'react';

import { useKeyboardHeight } from '@/hooks/useKeyboardHeight';
import { UserProvider } from '@/contexts/UserContext';

// 리소스 로딩이 완료될 때까지 스플래시 화면 유지
SplashScreen.preventAutoHideAsync();

// 커스텀 토스트 메시지
const toastConfig: ToastConfig = {
  custom_success: ({ props }) => (
    <View style={styles.toastContainer}>
      <Text style={styles.toastText}>{props.message}</Text>
    </View>
  ),
  custom_error: ({ props }) => (
    <View style={[styles.toastContainer, styles.toastError]}>
      <Text style={styles.toastText}>{props.message}</Text>
    </View>
  )
};

export default function RootLayout() {
  const insets = useSafeAreaInsets(); // SafeArea 정보
  const keyboardHeight = useKeyboardHeight();

  const [loaded] = useFonts({
    SpaceMono: require('@assets/fonts/SpaceMono-Regular.ttf'),
  });

  useEffect(() => {
    if (loaded) SplashScreen.hideAsync();
  }, [loaded]);

  if (!loaded) return null;

  return (
    <SafeAreaProvider>
      <UserProvider>
        {/* 스테이터스 바 */}
        <StatusBar style='light' />
        <View style={[styles.safeAreaTop, { height: insets.top }]} />

        {/* 모든 화면에서 헤더 숨김 */}
        <Stack screenOptions={{ headerShown: false }}>
          <Stack.Screen name="(tabs)" />
        </Stack>

        {/* 토스트 메시지를 네이게이션 바 뒤에 표시 */}
        <Toast
          config={toastConfig}
          visibilityTime={1500}
          position='bottom'
          bottomOffset={keyboardHeight + 60} // 키보드가 나타나면 하단 여백 설정
        />

        {/* 내비게이션 바 배경 */}
        {Platform.OS === 'android' && insets.bottom > 0 &&
          <View style={[styles.safeAreaBottom, { height: insets.bottom }]} />
        }
      </UserProvider>
    </SafeAreaProvider>
  );
}

const styles = StyleSheet.create({
  safeAreaTop: { backgroundColor: 'black' },
  safeAreaBottom: {
    position: 'absolute',
    bottom: 0,
    left: 0, right: 0,
    backgroundColor: 'black'
  },
  toastContainer: {
    paddingVertical: 8, paddingHorizontal: 16,
    borderRadius: 20,
    backgroundColor: 'gray'
  },
  toastText: { fontSize: 14, color: 'white' },
  toastError: { backgroundColor: 'tomato' }
});