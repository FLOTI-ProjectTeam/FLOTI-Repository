import { View, Text, StyleSheet, Pressable, TouchableOpacity } from 'react-native';

import { useNavigation } from '@/hooks/useNavigation';

import { IconSymbol } from '@/components/ui/IconSymbol';

import COLOR from '@/constants/colors';

export function Header({
  title, commentCount
}: {
  title: string;
  commentCount?: number;
}) {
  const { goBackSafely } = useNavigation();

  return (
    <View style={styles.headerContainer}>
      <Pressable onPress={() => goBackSafely()} style={styles.iconWrapper}>
        <IconSymbol name="chevron.left" size={32} color={COLOR.TINT.GRAY_DARK} />
      </Pressable>
      <View style={styles.titleContainer}>
        <Text style={styles.title}>{title}</Text>
        <Text style={[styles.title, styles.commentCount]}>{commentCount}</Text>
      </View>
      <View style={styles.rightPlaceholder} />
    </View>
  );
}

export function EditorHeader({
  onSave, onSubmit
}: {
  onSave?: () => void;
  onSubmit: () => void;
}) {
  const { goBackSafely } = useNavigation();

  return (
    <View style={styles.headerContainer}>
      {/* 취소 버튼 */}
      <Pressable onPress={() => goBackSafely()} style={styles.iconWrapper}>
        <IconSymbol name="x" size={28} color={COLOR.TINT.GRAY_DARK} />
      </Pressable>

      {/* 임시저장·저장 버튼 */}
      <View style={styles.rightWrapper}>
        {onSave && (
          <TouchableOpacity activeOpacity={0.5} onPress={onSave}>
            <Text style={styles.saveButtonText}>임시저장</Text>
          </TouchableOpacity>
        )}
        {onSubmit && (
          <TouchableOpacity activeOpacity={0.8} onPress={onSubmit} style={styles.submitButton}>
            <Text style={styles.submitButtonText}>{onSave ? '등록' : '수정'}</Text>
          </TouchableOpacity>
        )}
      </View>
    </View>
  );
}

export const styles = StyleSheet.create({
  headerContainer: {
    height: 56,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: 10
  },
  iconWrapper: { width: 32, height: 32, justifyContent: 'center' },
  titleContainer: { flexDirection: 'row', alignItems: 'center', gap: 8 },
  title: { fontWeight: 700, fontSize: 20 },
  commentCount: { color: 'skyblue' },
  rightPlaceholder: { width: 24 },
  rightWrapper: { flexDirection: 'row', alignItems: 'center' },
  saveButtonText: { fontSize: 14, color: COLOR.TEXT.GRAY_MEDIUM },
  submitButton: {
    marginLeft: 12,
    backgroundColor: COLOR.BUTTON.NAVY,
    borderRadius: 16,
    paddingVertical: 6, paddingHorizontal: 14
  },
  submitButtonText: { fontSize: 15, color: 'white', fontWeight: 600 }
});