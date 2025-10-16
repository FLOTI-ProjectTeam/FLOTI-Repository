import { View, Text, Pressable, StyleSheet } from 'react-native';
import { useRouter } from 'expo-router';

import { IconSymbol } from '@/components/ui/IconSymbol';
import COLORS from '@/constants/colors';

type Props = {
  onSave?: () => void;
  onSubmit: () => void;
  submitLabel?: string;
};

export default function EditorHeader({ onSave, onSubmit, submitLabel = "등록" }: Props) {
    const router = useRouter();

    return (
      <View style={styles.container}>
        {/* 취소 버튼 */}
        <Pressable onPress={() => router.back()} style={styles.iconWrapper}>
          <IconSymbol name="x" size={28} color={COLORS.ICON.DARK_GRAY} />
        </Pressable>
  
        {/* 임시저장 & 저장 버튼 */}
        <View style={styles.rightWrapper}>
          {onSave && (
            <Pressable onPress={onSave}>
              <Text style={styles.saveButtonText}>임시저장</Text>
            </Pressable>
          )}
          {onSubmit && (
            <Pressable onPress={onSubmit} style={styles.submitButton}>
              <Text style={styles.submitButtonText}>{submitLabel}</Text>
            </Pressable>
          )}
        </View>
      </View>
    );
}

const styles = StyleSheet.create({
  container: {
    height: 56,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: 16,
    backgroundColor: 'white'
  },
  iconWrapper: {
    width: 32,
    height: 32,
    justifyContent: 'center',
    alignItems: 'center',
  },
  rightWrapper: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  saveButtonText: {
    fontSize: 14,
    color: COLORS.TEXT.GRAY_CHARCOAL
  },
  submitButton: {
    marginLeft: 12,
    backgroundColor: COLORS.BUTTON.NAVY,
    borderRadius: 20,
    paddingVertical: 6,
    paddingHorizontal: 14,
  },
  submitButtonText: {
    fontSize: 16,
    color: 'white',
    fontWeight: 'bold'
  }
  });