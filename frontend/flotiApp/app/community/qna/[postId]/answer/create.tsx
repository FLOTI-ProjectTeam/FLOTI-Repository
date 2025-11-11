import { View, StyleSheet, TouchableOpacity } from 'react-native';
import { useLocalSearchParams, useRouter } from 'expo-router';
import { useState } from 'react';

import { createAnswer } from '@/api/community/qnaApi';
import EditorHeader from '@/components/ui/EditorHeader';
import { IconSymbol } from '@/components/ui/IconSymbol';
import BreakAllText from '@/components/ui/BreakAllText';
import InputView from '@/components/InputView';
import { showToast } from '@/utils/toast';
import COLOR from '@/constants/colors';
import { STYLE } from '@/constants/styles';

export default function AnswerCreateScreen() {
  const router = useRouter();
  const { postId, postTitle, postContent } = useLocalSearchParams();  // URL에서 게시글 정보 가져오기

  const [content, setContent] = useState('');
  const [expanded, setExpanded] = useState(false);  // 펼침 여부

  /* API 호출 */
  const callCreateAnswer = () => createAnswer(Number(postId), { content });

  /* 이벤트 핸들러 */
  const handleSave = () => {
    showToast('임시저장 되었습니다.');
  };

  const handleSubmit = async () => {
    try {
      await callCreateAnswer();
      router.back();
    } catch (error) {
      showToast('등록 중 오류가 발생했습니다.', 'error');
    }
  };

  return (
    <View style={STYLE.BASE_CONTAINER}>
      <EditorHeader onSave={handleSave} onSubmit={handleSubmit} />

      {/* 게시글 정보 */}
      <View style={styles.postContainer}>
        <BreakAllText style={styles.postTitle}>{String(postTitle)}</BreakAllText>
        {expanded && <BreakAllText style={styles.postContent}>{String(postContent)}</BreakAllText>}

        <TouchableOpacity
          activeOpacity={0.7} // 클릭 시 투명도 설정
          style={styles.iconContainer}
          onPress={() => setExpanded(prev => !prev)}
        >
          <View style={styles.iconWrapper}>
            <IconSymbol
              name={expanded ? "chevron.up" : "chevron.down"} // 펼침 여부에 따라 아이콘 변경
              size={20}
              color={COLOR.TINT.GRAY_DARK}
            />
          </View>
        </TouchableOpacity>
      </View>

      <InputView onChangeContent={setContent} />
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
