import { View } from 'react-native';
import { useEffect, useState } from 'react';
import AsyncStorage from '@react-native-async-storage/async-storage';

import { createTipPost } from '@/api/community/tipApi';
import { EditorHeader } from '@/components/ui/Header';
import InputView from '@/components/feature/community/InputView';
import { useNavigation } from '@/hooks/useNavigation';
import { showToast } from '@/utils/toast';
import { STYLE } from '@/constants/styles';

export default function TipCreateScreen() {
  const { navigateTo } = useNavigation();

  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [file, setFile] = useState<File | undefined>(undefined);

  const STORAGE_KEY = '@tip_save';

  /* API 호출 */
  const callCreateTipPost = () => createTipPost({ title, content }, file);

  // 화면 로드 시 임시저장 로드
  useEffect(() => {
    const loadDraft = async () => {
      try {
        const saved = await AsyncStorage.getItem(STORAGE_KEY);
        if (saved) {
          const { title: savedTitle, content: savedContent } = JSON.parse(saved);
          setTitle(savedTitle);
          setContent(savedContent);
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
        JSON.stringify({ title, content })
      );
      showToast('임시저장 성공');
    } catch (error) {
      showToast('임시저장 실패', 'error');
    }
  };

  const handleSubmit = async () => {
    try {
      const response = await callCreateTipPost();
      await AsyncStorage.removeItem(STORAGE_KEY); // 등록 성공 시 임시저장 삭제
      navigateTo(`/community/tip/${response.data.id}`);
    } catch (error) {
      showToast('등록 실패', 'error');
    }
  };

  return (
    <View style={STYLE.BASE_CONTAINER}>
      <EditorHeader onSave={handleSave} onSubmit={handleSubmit} />
      <InputView 
        title={title} 
        content={content} 
        onChangeTitle={setTitle} 
        onChangeContent={setContent}
      />
    </View>
  );
}