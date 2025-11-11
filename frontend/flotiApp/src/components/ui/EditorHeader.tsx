import { View, Text, Pressable, StyleSheet, TouchableOpacity } from 'react-native';
import { useRouter } from 'expo-router';

import { IconSymbol } from '@/components/ui/IconSymbol';
import COLOR from '@/constants/colors';

export default function EditorHeader({
  onSave, onSubmit, submitText = '등록'
}: {
  onSave?: () => void;
  onSubmit: () => void;
  submitText?: string;
}) {
  const router = useRouter();

  return (
    <View style={styles.headerContainer}>
      {/* 취소 버튼 */}
      <Pressable onPress={() => router.back()} style={styles.iconWrapper}>
        <IconSymbol name="x" size={28} color={COLOR.ICON.GRAY_DARK} />
      </Pressable>

      {/* 임시저장 & 저장 버튼 */}
      <View style={styles.rightWrapper}>
        {onSave && (
          <TouchableOpacity activeOpacity={0.5} onPress={onSave}>
            <Text style={styles.saveButtonText}>임시저장</Text>
          </TouchableOpacity>
        )}
        {onSubmit && (
          <TouchableOpacity activeOpacity={0.8} onPress={onSubmit} style={styles.submitButton}>
            <Text style={styles.submitButtonText}>{submitText}</Text>
          </TouchableOpacity>
        )}
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  headerContainer: {
    height: 56,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: 16
  },
  iconWrapper: { width: 32, height: 32, justifyContent: 'center' },
  rightWrapper: { flexDirection: 'row', alignItems: 'center' },
  saveButtonText: { fontSize: 14, color: COLOR.TEXT.GRAY_CHARCOAL },
  submitButton: {
    marginLeft: 12,
    backgroundColor: COLOR.BUTTON.NAVY,
    borderRadius: 16,
    paddingVertical: 6, paddingHorizontal: 14
  },
  submitButtonText: { fontSize: 15, color: 'white', fontWeight: 600 }
});