import { View } from 'react-native';
import { useLocalSearchParams, useRouter } from 'expo-router';
import { useState } from 'react';

import { updateTipPost } from '@/api/community/tipApi';
import EditorHeader from '@/components/ui/EditorHeader';
import InputView from '@/components/InputView';
import { showToast } from '@/utils/toast';
import { STYLE } from '@/constants/styles';

export default function TipUpdateScreen() {
  const router = useRouter();
  const { postId, title: initialTitle, content: initialContent } = useLocalSearchParams();  // URL에서 게시글 정보 가져오기
  
  const [title, setTitle] = useState(String(initialTitle || ''));
  const [content, setContent] = useState(String(initialContent || ''));
  const [file, setFile] = useState<File | undefined>(undefined);
  const [isDeleted, setIsDeleted] = useState(false);  // 섬네일 삭제 여부

  /* API 호출 */
  const callUpdateTipPost = () => updateTipPost(Number(postId), { title, content }, file, isDeleted);

  /* 이벤트 핸들러 */
  const handleSubmit = async () => {
    try {
      await callUpdateTipPost();
      router.back();
    } catch (error) {
      showToast('수정 중 오류가 발생했습니다.', 'error');
    }
  };

  return (
    <View style={STYLE.BASE_CONTAINER}>
      <EditorHeader onSubmit={handleSubmit} />
      <InputView title={title} content={content} onChangeTitle={setTitle} onChangeContent={setContent} />
    </View>
  );
}