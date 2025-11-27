import { ScrollView } from 'react-native';
import { useLocalSearchParams } from 'expo-router';
import { useState } from 'react';

import { updateDiscussionRoom } from '@/api/community/discussionApi';
import { EditorHeader } from '@/components/ui/Header';
import ConfirmModal from '@/components/ui/ConfirmModal';
import { DiscussionInputView } from '@/components/feature/community/TopicInputView';
import { useModal } from '@/hooks/useModal';
import { useNavigation } from '@/hooks/useNavigation';
import { showToast } from '@/utils/toast';
import { STYLE } from '@/constants/styles';

export default function DiscussionUpdateScreen() {
  const { goBackSafely } = useNavigation();
  const { roomId, initialTitle, initialContent, initialMaxParticipantCount, initialParticipantCount }
    = useLocalSearchParams();  // URL에서 토론방 정보 가져오기

  const [title, setTitle] = useState(String(initialTitle || ''));
  const [content, setContent] = useState(String(initialContent || ''));
  const [maxParticipantCount, setMaxParticipantCount] = useState(Number(initialMaxParticipantCount || 2));

  const { modalVisible, openModal, closeModal } = useModal();
  const participantCount = Number(initialParticipantCount || 1);

  /* API 호출 */
  const callUpdateDiscussionRoom = () => updateDiscussionRoom(Number(roomId), { title, content, maxParticipantCount });

  /* 이벤트 핸들러 */
  const handleSubmit = async () => {
    try {
      await callUpdateDiscussionRoom();
      goBackSafely();
    } catch (error) {
      showToast('수정 실패', 'error');
    }
  };

  const handleChangeMaxCount = (newCount: number) => {
    if (newCount < participantCount) openModal();
    else setMaxParticipantCount(newCount);
  };

  return (
    <ScrollView style={STYLE.CONTENT_CONTAINER}>
      <EditorHeader onSubmit={handleSubmit} />

      <DiscussionInputView
        title={title}
        content={content}
        maxParticipantCount={maxParticipantCount}
        onChangeTitle={setTitle}
        onChangeContent={setContent}
        onChangeMaxParticipantCount={handleChangeMaxCount}
      />
      
      <ConfirmModal
        visible={modalVisible}
        title={'현재 참여자보다 적은 인원입니다.'}
        onClose={closeModal}
      />
    </ScrollView>
  );
}