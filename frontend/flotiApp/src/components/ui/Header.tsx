import { View, Text, StyleSheet, Pressable } from 'react-native';
import { useRouter } from 'expo-router';

import { IconSymbol } from '@/components/ui/IconSymbol';
import COLOR from '@/constants/colors';

export default function Header({
  title, commentCount, backgroundColor = 'white'
}: {
  title: string;
  commentCount?: number;
  backgroundColor?: string;
}) {
  const router = useRouter();

  return (
    <View style={[styles.headerContainer, {backgroundColor}]}>
      <Pressable onPress={() => router.back()} style={styles.iconWrapper}>
        <IconSymbol name="chevron.left" size={32} color={COLOR.ICON.GRAY_DARK} />
      </Pressable>
      <View style={styles.titleContainer}>
        <Text style={styles.title}>{title}</Text>
        <Text style={[styles.title, styles.commentCount]}>{commentCount}</Text>
      </View>
      <View style={styles.rightPlaceholder} />
    </View>
  );
}

const styles = StyleSheet.create({
  headerContainer: {
    height: 56,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: 10
  },
  iconWrapper: { width: 32, height: 32, justifyContent: 'center' },
  titleContainer: { flexDirection: 'row', alignItems: 'center', gap: 8 },
  title: { fontWeight: 'bold', fontSize: 20 },
  commentCount: { color: COLOR.TEXT.SKY },
  rightPlaceholder: { width: 24 }
});