import { View } from 'react-native';
import { useLocalSearchParams } from 'expo-router';
import { useState } from 'react';

import { useNavigation } from '@/hooks/useNavigation';

import { updateQnaPost } from '@/api/community/qnaApi';

import { EditorHeader } from '@/components/ui/Header';
import { QnaInputView } from '@/components/feature/community/InputView';

import { showToast } from '@/utils/toast';
import { STYLE } from '@/constants/styles';

export default function QnaUpdateScreen() {
  const { goBackSafely } = useNavigation();
  const { postId, initialTitle, initialContent } = useLocalSearchParams();  // URL에서 게시글 정보 가져오기

  const [title, setTitle] = useState(String(initialTitle || ''));
  const [content, setContent] = useState(String(initialContent || ''));

  /* API 호출 */
  const callUpdateQnaPost = () => updateQnaPost(Number(postId), { title, content });

  /* 이벤트 핸들러 */
  const handleSubmit = async () => {
    try {
      await callUpdateQnaPost();
      goBackSafely();
    } catch (error) {
      showToast('수정 실패', 'error');
    }
  };

  return (
    <View style={STYLE.BASE_CONTAINER}>
      <EditorHeader onSubmit={handleSubmit} />
      <QnaInputView title={title} content={content} onChangeTitle={setTitle} onChangeContent={setContent} />
    </View>
  );
}