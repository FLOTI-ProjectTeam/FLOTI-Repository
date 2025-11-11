import { View, Text, TextInput, TouchableOpacity, StyleSheet, Platform, Keyboard } from 'react-native';
import { useEffect, useState } from 'react';

import { IconSymbol } from '@/components/ui/IconSymbol';
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
  const [keyboardHeight, setKeyboardHeight] = useState(0);

  useEffect(() => {
    // 키보드가 나타나면 키보드 높이 저장
    const showSub = Keyboard.addListener(
      Platform.OS === 'ios' ? 'keyboardWillShow' : 'keyboardDidShow',
      (e) => setKeyboardHeight(e.endCoordinates.height)
    );

    // 키보드가 닫히면 0으로 초기화
    const hideSub = Keyboard.addListener(
      Platform.OS === 'ios' ? 'keyboardWillHide' : 'keyboardDidHide',
      () => setKeyboardHeight(0)
    );

    return () => {
      showSub.remove();
      hideSub.remove();
    };
  }, []);

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

      <View style={[
        styles.inputContainer, 
        { marginBottom: keyboardHeight ? keyboardHeight : 0 } // 키보드가 나타나면 하단 여백 설정
      ]}>
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
  replyToText: { fontWeight: 'bold' },
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
    alignItems: 'center',
    justifyContent: 'center'
  }
});