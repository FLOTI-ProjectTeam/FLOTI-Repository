import { View } from 'react-native';
import { useLocalSearchParams } from 'expo-router';
import { useState } from 'react';

import { useNavigation } from '@/hooks/useNavigation';
import useDraft from '@/hooks/useDraft';

import { createAnswer } from '@/api/community/qnaApi';

import { EditorHeader } from '@/components/ui/Header';
import { QnaInputView } from '@/components/feature/community/InputView';
import QnaInfo from '@/components/feature/community/qna/QnaInfo';

import { showToast } from '@/utils/toast';
import { STYLE } from '@/constants/styles';

export default function AnswerCreateScreen() {
  const { goBackSafely } = useNavigation();
  const { postId, postTitle, postContent } = useLocalSearchParams();  // URL에서 게시글 정보 가져오기

  const [content, setContent] = useState('');

  const { saveDraft, clearDraft } = useDraft({
    storageKey: `@answer_save_${postId}`,
    onLoad: (data) => {
      setContent(data.content);
    },
  });

  /* API 호출 */
  const callCreateAnswer = () => createAnswer(Number(postId), { content });

  /* 이벤트 핸들러 */
  const handleSave = () => saveDraft({ content });

  const handleSubmit = async () => {
    try {
      await callCreateAnswer();
      await clearDraft(); // 등록 성공 시 임시저장 삭제
      goBackSafely();
    } catch (error) {
      showToast('등록 실패', 'error');
    }
  };

  return (
    <View style={STYLE.BASE_CONTAINER}>
      <EditorHeader onSave={handleSave} onSubmit={handleSubmit} />
      <QnaInfo title={String(postTitle)} content={String(postContent)} />
      <QnaInputView content={content} onChangeContent={setContent} />
    </View>
  );
}