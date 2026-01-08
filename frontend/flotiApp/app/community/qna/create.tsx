import { View } from 'react-native';
import { useState } from 'react';

import { useNavigation } from '@/hooks/useNavigation';
import useDraft from '@/hooks/useDraft';

import { createQnaPost } from '@/api/community/qnaApi';

import { EditorHeader } from '@/components/ui/Header';
import { QnaInputView } from '@/components/feature/community/InputView';

import { showToast } from '@/utils/toast';
import { STYLE } from '@/constants/styles';

export default function QnaCreateScreen() {
  const { navigateTo } = useNavigation();

  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');

  const { saveDraft, clearDraft } = useDraft({
    storageKey: '@qna_save',
    onLoad: (data) => {
      setTitle(data.title!);
      setContent(data.content);
    },
  });

  /* API 호출 */
  const callCreateQnaPost = () => createQnaPost({ title, content });

  /* 이벤트 핸들러 */
  const handleSave = () => saveDraft({ title, content });

  const handleSubmit = async () => {
    try {
      const response = await callCreateQnaPost();
      await clearDraft(); // 등록 성공 시 임시저장 삭제
      navigateTo(`/community/qna/${response.data.id}`)
    } catch (error) {
      showToast('등록 실패', 'error');
    }
  };

  return (
    <View style={STYLE.BASE_CONTAINER}>
      <EditorHeader onSave={handleSave} onSubmit={handleSubmit} />
      <QnaInputView title={title} content={content} onChangeTitle={setTitle} onChangeContent={setContent} />
    </View>
  );
}