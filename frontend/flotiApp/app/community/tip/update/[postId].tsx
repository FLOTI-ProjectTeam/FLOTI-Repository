import { View } from 'react-native';
import { useLocalSearchParams, useRouter } from 'expo-router';
import { useState } from 'react';

import EditorHeader from '@/components/ui/EditorHeader';
import { UpdateInputView } from '@/components/InputView';
import { STYLE } from '@/constants/styles';

export default function TipUpdatePost() {
  const router = useRouter();
  const { postId, title: initialTitle, content: initialContent } = useLocalSearchParams();
  const [title, setTitle] = useState(String(initialTitle || ''));
  const [content, setContent] = useState(String(initialContent || ''));

  const handleUpdateSubmit = () => {
    console.log('수정 완료', { title, content });
    router.back();
  };

  return (
    <View style={STYLE.BASE_CONTAINER}>
      <EditorHeader onSubmit={handleUpdateSubmit} submitText='수정' />
      <UpdateInputView title={title} content={content} onChangeTitle={setTitle} onChangeContent={setContent} />
    </View>
  );
}