import { View } from 'react-native';
import { useLocalSearchParams } from 'expo-router';
import { useEffect, useState } from 'react';
import AsyncStorage from '@react-native-async-storage/async-storage';

import { useNavigation } from '@/hooks/useNavigation';

import { createAnswer } from '@/api/community/qnaApi';

import { EditorHeader } from '@/components/ui/Header';
import InputView from '@/components/feature/community/InputView';
import QnaInfo from '@/components/feature/community/qna/QnaInfo';

import { showToast } from '@/utils/toast';
import { STYLE } from '@/constants/styles';


export default function AnswerCreateScreen() {
  const { goBackSafely } = useNavigation();
  const { postId, postTitle, postContent } = useLocalSearchParams();  // URL에서 게시글 정보 가져오기

  const [content, setContent] = useState('');

  const STORAGE_KEY = `@answer_save_${postId}`;

  /* 사이드 이펙트 */
  useEffect(() => {
    loadDraft();
  }, []);

  /* API 호출 */
  const loadDraft = async () => {
    try {
      const saved = await AsyncStorage.getItem(STORAGE_KEY);
      if (saved) {
        const { content: savedContent } = JSON.parse(saved);
        setContent(savedContent);
      }
    } catch (error) {
      showToast('임시저장 불러오기 실패', 'error');
    }
  };

  const callCreateAnswer = () => createAnswer(Number(postId), { content });

  /* 이벤트 핸들러 */
  const handleSave = async () => {
    try {
      await AsyncStorage.setItem(
        STORAGE_KEY,
        JSON.stringify({ content })
      );
      showToast('임시저장 성공');
    } catch (error) {
      showToast('임시저장 실패', 'error');
    }
  };

  const handleSubmit = async () => {
    try {
      await callCreateAnswer();
      await AsyncStorage.removeItem(STORAGE_KEY); // 등록 성공 시 임시저장 삭제
      goBackSafely();
    } catch (error) {
      showToast('등록 실패', 'error');
    }
  };

  return (
    <View style={STYLE.BASE_CONTAINER}>
      <EditorHeader onSave={handleSave} onSubmit={handleSubmit} />
      <QnaInfo title={String(postTitle)} content={String(postContent)} />
      <InputView content={content} onChangeContent={setContent} />
    </View>
  );
}