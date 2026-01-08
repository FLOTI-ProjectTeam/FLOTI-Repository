import { View } from 'react-native';
import { useState } from 'react';

import { useNavigation } from '@/hooks/useNavigation';
import useDraft from '@/hooks/useDraft';

import { createTipPost } from '@/api/community/tipApi';

import { EditorHeader } from '@/components/ui/Header';
import { TipInputView } from '@/components/feature/community/InputView';

import { showToast } from '@/utils/toast';
import { STYLE } from '@/constants/styles';

export default function TipCreateScreen() {
  const { navigateTo } = useNavigation();

  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [file, setFile] = useState<any>(undefined);

  const { saveDraft, clearDraft } = useDraft({
    storageKey: '@tip_save',
    onLoad: (data) => {
      setTitle(data.title!);
      setContent(data.content);
      if (data.file) setFile(data.file);
    },
  });

  /* API 호출 */
  const callCreateTipPost = () => createTipPost({ title, content }, file);

  /* 이벤트 핸들러 */
  const handleSave = () => saveDraft({ title, content, file });

  const handleSubmit = async () => {
    try {
      const response = await callCreateTipPost();
      await clearDraft(); // 등록 성공 시 임시저장 삭제
      navigateTo(`/community/tip/${response.data.id}`);
    } catch (error) {
      showToast('등록 실패', 'error');
    }
  };

  return (
    <View style={STYLE.BASE_CONTAINER}>
      <EditorHeader onSave={handleSave} onSubmit={handleSubmit} />
      <TipInputView
        title={title}
        content={content}
        file={file}
        onChangeTitle={setTitle}
        onChangeContent={setContent}
        onChangeFile={setFile}
      />
    </View>
  );
}