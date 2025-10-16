import { View, Text, StyleSheet, Pressable } from 'react-native';
import { useRouter } from 'expo-router';

import { IconSymbol } from '@/components/ui/IconSymbol';
import COLORS from '@/constants/colors';

type Props = {
    title: string;
    backgroundColor?: string;
};

export default function Header({ title, backgroundColor = "white" }: Props) {
    const router = useRouter();

    // 아이콘 클릭 이벤트
    const handlePress = () => router.back();

    return (
        <View style={[styles.container, { backgroundColor }]}>
            <Pressable onPress={handlePress} style={styles.iconWrapper}>
                <IconSymbol name="chevron.left" size={32} color={COLORS.ICON.GRAY_DARK} />
            </Pressable>
            <Text style={styles.title}>{title}</Text>
            <View style={styles.rightPlaceholder} />
        </View>
    );
}

const styles = StyleSheet.create({
  container: {
    height: 56,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: 16
  },
  iconWrapper: {
    width: 32,
    height: 32,
    justifyContent: 'center',
    alignItems: 'center'
  },
  title: {
    fontWeight: 'bold',
    fontSize: 20,
    textAlign: 'center',
    flex: 1 // 가운데 정렬 유지
  },
  rightPlaceholder: {
    width: 24 // 아이콘과 균형 맞추기
  },
});