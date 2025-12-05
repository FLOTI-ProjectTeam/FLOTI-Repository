import { ScrollView } from 'react-native';
import { useEffect, useState } from 'react';
import AsyncStorage from '@react-native-async-storage/async-storage';

import { createDiscussionRoom } from '@/api/community/discussionApi';
import { EditorHeader } from '@/components/ui/Header';
import { DiscussionInputView } from '@/components/feature/community/TopicInputView';
import { useNavigation } from '@/hooks/useNavigation';
import { showToast } from '@/utils/toast';
import { STYLE } from '@/constants/styles';

export default function DiscussionCreateScreen() {
  const { navigateTo } = useNavigation();

  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [maxParticipantCount, setMaxParticipantCount] = useState(2);

  const STORAGE_KEY = '@discussuin_save';

  /* API 호출 */
  const callCreateDiscussionRoom= () => createDiscussionRoom({ title, content, maxParticipantCount });

  // 화면 로드 시 임시저장 로드
  useEffect(() => {
    const loadDraft = async () => {
      try {
        const saved = await AsyncStorage.getItem(STORAGE_KEY);
        if (saved) {
          const { title: savedTitle, content: savedContent, maxParticipantCount: savedMaxParticipantCount } = JSON.parse(saved);
          setTitle(savedTitle);
          setContent(savedContent);
          setMaxParticipantCount(savedMaxParticipantCount);
        }
      } catch (error) {
        showToast('임시저장 불러오기 실패', 'error');
      }
    };
    loadDraft();
  }, []);

  /* 이벤트 핸들러 */
  const handleSave = async () => {
    try {
      await AsyncStorage.setItem(
        STORAGE_KEY,
        JSON.stringify({ title, content, maxParticipantCount })
      );
      showToast('임시저장 성공');
    } catch (error) {
      showToast('임시저장 실패', 'error');
    }
  };

  const handleSubmit = async () => {
    try {
      const response = await callCreateDiscussionRoom();
      await AsyncStorage.removeItem(STORAGE_KEY); // 등록 성공 시 임시저장 삭제
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