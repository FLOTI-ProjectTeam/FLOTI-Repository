import { View } from 'react-native';
import { useLocalSearchParams, useRouter } from 'expo-router';
import { useState } from 'react';

import { updateQnaPost } from '@/api/community/qnaApi';
import { EditorHeader } from '@/components/ui/Header';
import InputView from '@/components/feature/community/InputView';
import { showToast } from '@/utils/toast';
import { STYLE } from '@/constants/styles';

export default function QnaUpdateScreen() {
  const router = useRouter();
  const { postId, initialTitle, initialContent } = useLocalSearchParams();  // URL에서 게시글 정보 가져오기

  const [title, setTitle] = useState(String(initialTitle || ''));
  const [content, setContent] = useState(String(initialContent || ''));

  /* API 호출 */
  const callUpdateQnaPost = () => updateQnaPost(Number(postId), { title, content });

  /* 이벤트 핸들러 */
  const handleSubmit = async () => {
    try {
      await callUpdateQnaPost();
      router.back();
    } catch (error) {
      showToast('수정 실패', 'error');
    }
  };

  return (
    <View style={STYLE.BASE_CONTAINER}>
      <EditorHeader onSubmit={handleSubmit} />
      <InputView title={title} content={content} onChangeTitle={setTitle} onChangeContent={setContent} />
    </View>
  );
}