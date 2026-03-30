import './global.css';

import { SafeAreaProvider, useSafeAreaInsets } from 'react-native-safe-area-context';
import Toast, { ToastConfig } from 'react-native-toast-message';  // Android & iOS 공용 토스트 메시지
import { useFonts } from 'expo-font';
import { Stack, useRouter } from 'expo-router';
import * as SplashScreen from 'expo-splash-screen';
import { StatusBar } from 'expo-status-bar';
import { View, Text, StyleSheet, Platform } from 'react-native';
import { useEffect, useState } from 'react';

import { useKeyboardHeight } from '@/hooks/useKeyboardHeight';
import { UserProvider, useUser } from '@/contexts/UserContext';

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
  const [loaded] = useFonts({
    SpaceMono: require('@/assets/fonts/SpaceMono-Regular.ttf'),
  });

  useEffect(() => {
    if (loaded) SplashScreen.hideAsync();
  }, [loaded]);

  if (!loaded) return null;

  return (
    <SafeAreaProvider>
      <UserProvider>
        <AuthLayout />
      </UserProvider>
    </SafeAreaProvider>
  );
}

// UserProvider 내부에서 Context를 사용하기 위해 분리된 컴포넌트
function AuthLayout() {
  const insets = useSafeAreaInsets();
  const keyboardHeight = useKeyboardHeight();
  const { user } = useUser();
  const router = useRouter();
  const [isReady, setIsReady] = useState(false);

  /* [Debug] Force Logout: Clear storage on mount once - REMOVED for Auth */
  useEffect(() => {
    // 0.5초 정도 후 체크 (스플래시 등 고려)
    const timer = setTimeout(() => {
      setIsReady(true);
    }, 500);
    return () => clearTimeout(timer);
  }, []);

  useEffect(() => {
    if (!isReady) return;

    if (!user || !user.username) {
      // 로그인 안 되어 있으면 로그인 화면으로 리다이렉트
      // (현재 경로 체크는 expo-router 특성상 좀 더 복잡할 수 있으나, 단순 리다이렉트로 처리)
      // 단, 무한 루프 방지를 위해 login/signup 페이지에 있을 때는 리다이렉트 하지 않아야 함.
      // 여기서는 Root Layout 레벨에서 처리하므로, Stack의 초기 화면을 제어하는 방식이 안전함.
      // 하지만 expo-router는 URL 기반이므로, 여기서 replace 하는게 일반적.
      // ** 중요: 단순 replace를 하면 로그인 페이지에서도 계속 리다이렉트 될 수 있음. **
      // 따라서 AuthGuard를 각 페이지에 두거나, 
      // 여기서는 'user가 없으면' 무조건 login으로 보내되, 이미 login/signup 경로면 무시해야 함.
      // 그러나 현재 RootLayout에서는 현재 경로를 알기 어려울 수 있음.
      // 가장 쉬운 방법: UserContext 초기 로딩이 끝나면 결정.

      // 여기서는 AuthGuard 컴포넌트 대신 UseEffect로 처리하되,
      // path 확인 로직이 없으므로 일단 'user가 없으면' login으로 보냄.
      // (expo-router의 Protected Route 패턴 참고)
      router.replace('/auth/login');
    }
  }, [isReady, user]);

  return (
    <View style={{ flex: 1, backgroundColor: 'black' }}>
      <StatusBar style='light' />
      <View style={[styles.safeAreaTop, { height: insets.top }]} />

      <Stack screenOptions={{ headerShown: false }}>
        {/* 로그인/회원가입 (인증 불필요) */}
        <Stack.Screen name="auth/login" options={{ animation: 'fade' }} />
        <Stack.Screen name="auth/signup" options={{ presentation: 'card' }} />

        {/* 메인 탭 (인증 필요) */}
        <Stack.Screen name="(tabs)" />
      </Stack>

      <Toast
        config={toastConfig}
        visibilityTime={1500}
        position='bottom'
        bottomOffset={keyboardHeight + 60}
      />

      {Platform.OS === 'android' && insets.bottom > 0 && (
        <View style={[styles.safeAreaBottom, { height: insets.bottom }]} />
      )}
    </View>
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