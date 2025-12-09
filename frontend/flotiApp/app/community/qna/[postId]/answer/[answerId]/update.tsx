import { View } from 'react-native';
import { useLocalSearchParams } from 'expo-router';
import { useState } from 'react';

import { useNavigation } from '@/hooks/useNavigation';

import { updateAnswer } from '@/api/community/qnaApi';

import { EditorHeader } from '@/components/ui/Header';
import InputView from '@/components/feature/community/InputView';
import QnaInfo from '@/components/feature/community/qna/QnaInfo';

import { showToast } from '@/utils/toast';
import { STYLE } from '@/constants/styles';

export default function AnswerUpdateScreen() {
  const { goBackSafely } = useNavigation();
  const { postId, answerId, postTitle, postContent, initialContent } = useLocalSearchParams();  // URL에서 게시글 정보 가져오기

  const [content, setContent] = useState(String(initialContent || ''));

  /* API 호출 */
  const callUpdateAnswer = () => updateAnswer(Number(postId), Number(answerId), { content });

  /* 이벤트 핸들러 */
  const handleSubmit = async () => {
    try {
      await callUpdateAnswer();
      goBackSafely();
    } catch (error) {
      showToast('수정 실패', 'error');
    }
  };

  return (
    <View style={STYLE.BASE_CONTAINER}>
      <EditorHeader onSubmit={handleSubmit} />
      <QnaInfo title={String(postTitle)} content={String(postContent)} />
      <InputView content={content} onChangeContent={setContent} />
    </View>
  );
}