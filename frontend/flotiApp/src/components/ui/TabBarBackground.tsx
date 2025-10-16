import { View } from 'react-native';

export default function TabBarBackground() {
  return (
    <View
      style={{
        flex: 1,
        backgroundColor: 'white',
      }}
    />
  );
}

// overflow 함수는 그대로 두어도 됨
export function useBottomTabOverflow() {
  return 0;
}