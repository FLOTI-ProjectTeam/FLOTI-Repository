import { View, StyleSheet, TouchableOpacity } from 'react-native';
import Toast from 'react-native-toast-message';
import { useLocalSearchParams, useRouter } from 'expo-router';
import { useState } from 'react';

import EditorHeader from '@/components/ui/EditorHeader';
import { IconSymbol } from '@/components/ui/IconSymbol';
import { CreateInputView } from '@/components/InputView';
import COLOR from '@/constants/colors';
import { STYLE } from '@/constants/styles';
import BreakAllText from '@/components/ui/BreakAllText';

export default function QnaCreateScreen() {
  const router = useRouter();
  const { postId, title, content } = useLocalSearchParams();
  const [expanded, setExpanded] = useState(false);  // 펼침 여부

  const handleSave = () => {
    Toast.show({
      type: 'custom_success',
      position: 'bottom',
      visibilityTime: 1500,
      props: { message: '임시저장 되었습니다.' }
    });
  };

  const handleSubmit = () => {
    console.log('등록 완료');
    router.back();
  };

  return (
    <View style={STYLE.BASE_CONTAINER}>
      <EditorHeader onSave={handleSave} onSubmit={handleSubmit} />

      {/* 게시글 정보 */}
      <View style={styles.postContainer}>
        <BreakAllText style={styles.postTitle}>{title as string}</BreakAllText>
        {expanded && <BreakAllText style={styles.postContent}>{content as string}</BreakAllText>}

        <TouchableOpacity
          activeOpacity={0.7} // 클릭 시 투명도 설정
          style={styles.iconContainer}
          onPress={() => setExpanded(prev => !prev)}
        >
          <View style={styles.iconWrapper}>
            <IconSymbol
              name={expanded ? 'chevron.up' : 'chevron.down'} // 펼침 여부에 따라 아이콘 변경
              size={20}
              color={COLOR.ICON.GRAY_DARK}
            />
          </View>
        </TouchableOpacity>
      </View>

      <CreateInputView isAnswer={true} />
    </View>
  );
}

const styles = StyleSheet.create({
  postContainer: {
    position: 'relative',
    paddingTop: 12, paddingBottom: 20,
    paddingHorizontal: 16,
    backgroundColor: COLOR.BACKGROUND.SLATE_LIGHT,
    marginBottom: 20,
    gap: 8
  },
  postTitle: { fontSize: 18, fontWeight: 'bold', color: COLOR.TEXT.GRAY_DARK },
  postContent: { fontSize: 15, lineHeight: 22, color: COLOR.TEXT.GRAY_DARK },
  iconContainer: { 
    position: 'absolute', 
    bottom: -16, 
    left: 0, right: 0, 
    alignItems: 'center' 
  },
  iconWrapper: {
    width: 32, height: 32,
    borderRadius: 16,
    borderWidth: 1,
    borderColor: COLOR.TINT.SLATE_SOFT,
    backgroundColor: 'white',
    justifyContent: 'center',
    alignItems: 'center'
  }
});
