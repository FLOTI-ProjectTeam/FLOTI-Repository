import { SafeAreaView } from 'react-native-safe-area-context';
import { Stack } from 'expo-router';

import { STYLE } from '@/constants/styles';

export default function CommunityLayout() {
  return (
    <SafeAreaView style={STYLE.BASE_CONTAINER} edges={['bottom']}>
      <Stack screenOptions={{headerShown: false}} />
    </SafeAreaView>
  );
}