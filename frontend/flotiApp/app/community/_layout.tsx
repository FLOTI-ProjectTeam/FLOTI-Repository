import { SafeAreaView } from 'react-native-safe-area-context';
import { Stack } from 'expo-router';

import Header from '@/components/ui/Header';
import { STYLE } from '@/constants/styles';

export default function CommunityLayout() {
  return (
    <SafeAreaView style={STYLE.BASE_CONTAINER} edges={['bottom']}>
      <Stack screenOptions={{headerShown: false}}>
        {/* 헤더 적용 */}
        <Stack.Screen name="tip/[postId]" 
          options={{
            headerShown: true,
            header: () => <Header title="TIP" />
          }}
        />
        <Stack.Screen name="qna/[postId]" 
          options={{
            headerShown: true,
            header: () => <Header title="Q&A" />
          }}
        />
      </Stack>
    </SafeAreaView>
  );
}