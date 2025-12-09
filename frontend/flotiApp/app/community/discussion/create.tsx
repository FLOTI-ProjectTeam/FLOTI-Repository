import { ScrollView } from 'react-native';
import { useState } from 'react';

import { useNavigation } from '@/hooks/useNavigation';
import useDraft from '@/hooks/useDraft';

import { createDiscussionRoom } from '@/api/community/discussionApi';

import { EditorHeader } from '@/components/ui/Header';
import { DiscussionInputView } from '@/components/feature/community/TopicInputView';

import { showToast } from '@/utils/toast';
import { STYLE } from '@/constants/styles';

export default function DiscussionCreateScreen() {
  const { navigateTo } = useNavigation();

  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [maxParticipantCount, setMaxParticipantCount] = useState(2);

  const { saveDraft, clearDraft } = useDraft({
    storageKey: '@discussuin_save',
    onLoad: (data) => {
      setTitle(data.title!);
      setContent(data.content);
      setMaxParticipantCount(data.maxParticipantCount!);
    },
  });

  /* API 호출 */
  const callCreateDiscussionRoom = () => createDiscussionRoom({ title, content, maxParticipantCount });

  /* 이벤트 핸들러 */
  const handleSave = () => saveDraft({ title, content, maxParticipantCount });

  const handleSubmit = async () => {
    try {
      const response = await callCreateDiscussionRoom();
      await clearDraft(); // 등록 성공 시 임시저장 삭제
      navigateTo(`/community/discussion/${response.data.id}`)
    } catch (error) {
      showToast('등록 실패', 'error');
    }
  };

  return (
    <ScrollView style={STYLE.CONTENT_CONTAINER}>
      <EditorHeader onSave={handleSave} onSubmit={handleSubmit} />

      <DiscussionInputView
        title={title}
        content={content}
        maxParticipantCount={maxParticipantCount}
        onChangeTitle={setTitle}
        onChangeContent={setContent}
        onChangeMaxParticipantCount={setMaxParticipantCount}
      />
    </ScrollView>
  );
}