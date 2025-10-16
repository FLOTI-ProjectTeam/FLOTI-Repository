import { View, TextInput, StyleSheet, ActivityIndicator, Text } from 'react-native';
import { useLocalSearchParams, useRouter } from 'expo-router';
import { useEffect, useState } from 'react';

import { dummyPosts } from '@/__mocks__/tip';
import EditorHeader from '@/components/ui/EditorHeader';
import COLORS from '@/constants/colors';
import STYLES from '@/constants/styles';
import { TipPostResponse } from '@/types/community/tip';

export default function UpdatePost() {
  const { id } = useLocalSearchParams();  // URL에서 id 가져오기
  const router = useRouter();

  const [post, setPost] = useState<TipPostResponse | null>(null);
  const [loading, setLoading] = useState(true);

  // Controlled component용 state
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');

  useEffect(() => {
    // 더미 데이터 호출
    const found = dummyPosts.find((p) => p.id.toString() === id);
    if (found) {
      setPost(found);
      setTitle(found.title);
      setContent(found.content);
    }
    setLoading(false);
  }, [id]);

  const handleSubmit = () => {
    // 실제 API 호출
    console.log('수정 완료', { title, content });
    router.back(); // 글 작성 후 뒤로 이동
  };

  // 로딩 상태
  if (loading) {
    return (
      <View style={[styles.container, styles.center]}>
        <ActivityIndicator size="large" color={COLORS.TINT.SLATE} />
      </View>
    );
  }

  // 글이 없을 때
  if (!post) {
    return (
      <View style={[styles.container, styles.center]}>
        <Text style={styles.emptyText}>글을 찾을 수 없습니다.</Text>
      </View>
    );
  }

  return (
    <View style={STYLES.CONTAINER}>
      {/* 헤더 */}
      <EditorHeader onSubmit={handleSubmit} submitLabel="수정" />

      {/* 제목 입력 */}
      <TextInput
        style={styles.input}
        value={title}
        onChangeText={setTitle}
        placeholder="제목을 입력하세요"
        placeholderTextColor={COLORS.TEXT.GRAY_MEDIUM}
      />

      {/* 내용 입력 */}
      <TextInput
        style={[styles.input, styles.textArea]}
        value={content}
        onChangeText={setContent}
        placeholder="내용을 입력하세요"
        multiline
        textAlignVertical="top"
        placeholderTextColor={COLORS.TEXT.GRAY_MEDIUM}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 16,
  },
  center: {
    justifyContent: 'center',
    alignItems: 'center',
  },
  input: {
    borderWidth: 0,
    padding: 12,
    marginHorizontal: 16,
    fontSize: 18, // 제목은 18px
    fontWeight: 'bold',
    backgroundColor: 'white',
  },
  textArea: {
    fontSize: 16, // 본문은 16px
    fontWeight: 'normal',
    minHeight: 200
  },
  emptyText: {
    color: COLORS.TEXT.GRAY_MEDIUM,
    textAlign: 'center',
    marginTop: 20,
  },
});