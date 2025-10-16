import { SafeAreaView } from 'react-native-safe-area-context';
import { Stack } from 'expo-router';
import 'react-native-reanimated';

import Header from '@/components/ui/Header';
import STYLES from '@/constants/styles';

export default function CommunityLayout() {
  return (
    <SafeAreaView style={STYLES.CONTAINER} edges={['bottom']}>
      <Stack screenOptions={{headerShown: false}}>
        {/* 헤더 적용 */}
        <Stack.Screen name="tip/[id]" 
          options={{
            headerShown: true,
            header: () => <Header title="TIP" />
          }}
        />
        <Stack.Screen name="qna/[id]" 
          options={{
            headerShown: true,
            header: () => <Header title="Q&A" />
          }}
        />
      </Stack>
    </SafeAreaView>
  );
}