import { View } from 'react-native';
import { useRouter } from 'expo-router';
import { useState } from 'react';

import { createTipPost } from '@/api/community/tipApi';
import EditorHeader from '@/components/ui/EditorHeader';
import InputView from '@/components/InputView';
import { showToast } from '@/utils/toast';
import { STYLE } from '@/constants/styles';

export default function TipCreateScreen() {
  const router = useRouter();

  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [file, setFile] = useState<File | undefined>(undefined);

  /* API 호출 */
  const callCreateTipPost = () => createTipPost({ title, content }, file);

  /* 이벤트 핸들러 */
  const handleSave = () => {
    showToast('임시저장 되었습니다.');
  };

  const handleSubmit = async () => {
    try {
      const response = await callCreateTipPost();
      router.push(`/community/tip/${response.data.id}`);
    } catch (error) {
      showToast('등록 중 오류가 발생했습니다.', 'error');
    }
  };

  return (
    <View style={STYLE.BASE_CONTAINER}>
      <EditorHeader onSave={handleSave} onSubmit={handleSubmit} />
      <InputView onChangeTitle={setTitle} onChangeContent={setContent} />
    </View>
  );
}