import { View } from 'react-native';
import { useLocalSearchParams } from 'expo-router';
import { useState } from 'react';

import { useNavigation } from '@/hooks/useNavigation';

import { updateTipPost } from '@/api/community/tipApi';

import { EditorHeader } from '@/components/ui/Header';
import InputView from '@/components/feature/community/InputView';

import { showToast } from '@/utils/toast';
import { STYLE } from '@/constants/styles';

export default function TipUpdateScreen() {
  const { goBackSafely } = useNavigation();
  const { postId, initialTitle, initialContent } = useLocalSearchParams();  // URL에서 게시글 정보 가져오기
  
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
      goBackSafely();
    } catch (error) {
      showToast('수정 실패', 'error');
    }
  };

  return (
    <View style={STYLE.BASE_CONTAINER}>
      <EditorHeader onSubmit={handleSubmit} />
      <InputView 
        title={title} 
        content={content}
        onChangeTitle={setTitle} 
        onChangeContent={setContent} 
      />
    </View>
  );
}