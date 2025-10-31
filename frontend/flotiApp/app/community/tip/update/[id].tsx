import { View } from 'react-native';
import { useLocalSearchParams, useRouter } from 'expo-router';
import { useState } from 'react';

import EditorHeader from '@/components/ui/EditorHeader';
import { STYLE } from '@/constants/styles';
import { UpdateInputView } from '@/components/InputView';

export default function TipUpdatePost() {
  const router = useRouter();
  const { id, title: initialTitle, content: initialContent } = useLocalSearchParams();
  const [title, setTitle] = useState(String(initialTitle || ''));
  const [content, setContent] = useState(String(initialContent || ''));

  const handleUpdateSubmit = () => {
    console.log('수정 완료', { title, content });
    router.back(); // 글 작성 후 뒤로 이동
  };

  return (
    <View style={STYLE.BASE_CONTAINER}>
      <EditorHeader onSubmit={handleUpdateSubmit} submitLabel='수정' />
      <UpdateInputView 
        title={title}
        content={content}
        onChangeTitle={setTitle}
        onChangeContent={setContent}
      />
    </View>
  );
}