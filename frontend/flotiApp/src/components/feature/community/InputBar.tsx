import { View, Text, TextInput, TouchableOpacity, StyleSheet } from 'react-native';

import { IconSymbol } from '@/components/ui/IconSymbol';
import { useKeyboardHeight } from '@/hooks/useKeyboardHeight';
import COLOR from '@/constants/colors';

export type ReplyTo = {
  commentId: number;
  nickname: string
};

export default function InputBar({ 
  content, onChangeText, onSubmit, placeholder, replyTo, onCancelReply
}: {
  content: string;
  onChangeText: (text: string) => void;
  onSubmit: () => void;
  placeholder: string;
  replyTo?: ReplyTo | null;
  onCancelReply?: () => void;
}) {
  const marginBottom = useKeyboardHeight(); // 키보드 높이를 하단 여백으로 사용

  return (
    <>
      {replyTo && (
        <View style={styles.replyBanner}>
          <View style={styles.replyTextRow}>
            <Text style={[styles.replyText, styles.replyToText]}>{replyTo.nickname}</Text>
            <Text style={styles.replyText}> 님에 대한 답글</Text>
          </View>
          <TouchableOpacity activeOpacity={0.7} onPress={onCancelReply} style={styles.replyCancelButton}>
            <IconSymbol name="x" size={20} color={COLOR.TINT.GRAY_DARK}/>
          </TouchableOpacity>
        </View>
      )}

      <View style={[styles.inputContainer, { marginBottom }]}>
        <TextInput
          placeholder={placeholder}
          placeholderTextColor={COLOR.TEXT.GRAY_LIGHT}
          value={content}
          onChangeText={onChangeText}
          style={styles.input}
          multiline
        />
        <TouchableOpacity activeOpacity={0.7} style={styles.sendButton} onPress={onSubmit}>
          <IconSymbol name='send' color={COLOR.TINT.GRAY_DARK} />
        </TouchableOpacity>
      </View>
    </>
  );
};

const styles = StyleSheet.create({
  replyBanner: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: COLOR.BACKGROUND.POWDER_LIGHT,
    paddingHorizontal: 12
  },
  replyText: { color: COLOR.TEXT.GRAY_CHARCOAL },
  replyToText: { fontWeight: 600 },
  replyCancelButton: { paddingVertical: 8 },
  replyTextRow: { flex: 1, flexDirection: 'row' },
  inputContainer: {
    flexDirection: 'row',
    alignItems: 'flex-end',
    paddingVertical: 12, paddingHorizontal: 10,
    backgroundColor: 'white',
    gap: 8
  },
  input: {
    flex: 1,
    height: 45,
    borderRadius: 8,
    paddingHorizontal: 12,
    paddingVertical: 8,
    fontSize: 15,
    backgroundColor: COLOR.TINT.GRAY_LIGHT
  },
  sendButton: {
    width: 45, height: 45,
    borderRadius: 22,
    backgroundColor: COLOR.BUTTON.GRAY_LIGHT,
    justifyContent: 'center',
    alignItems: 'center'
  }
});