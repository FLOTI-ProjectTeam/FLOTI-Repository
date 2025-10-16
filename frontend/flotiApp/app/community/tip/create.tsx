import { View, TextInput, StyleSheet } from 'react-native';
import { useRouter } from 'expo-router';

import EditorHeader from '@/components/ui/EditorHeader';
import COLORS from '@/constants/colors';
import STYLES from '@/constants/styles';

export default function CreatePost() {
  const router = useRouter();

  const handleSave = () => {
    console.log("임시저장 완료");
  };

  const handleSubmit = () => {
    console.log("등록 완료");
    router.back(); // 글 작성 후 뒤로 이동
  };

  return (
    <View style={STYLES.CONTAINER}>
      {/* 헤더 */}
      <EditorHeader onSave={handleSave} onSubmit={handleSubmit} />

      {/* 제목 입력 */}
      <TextInput
        style={styles.input}
        placeholder="제목을 입력하세요"
        placeholderTextColor={COLORS.TEXT.GRAY_MEDIUM}
      />

      {/* 내용 입력 */}
      <TextInput
        style={[styles.input, styles.textArea]}
        placeholder="내용을 입력하세요"
        multiline
        textAlignVertical="top"
        placeholderTextColor={COLORS.TEXT.GRAY_MEDIUM}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  input: {
    borderWidth: 0,
    padding: 12,
    marginHorizontal: 16,
    fontSize: 18, // 제목은 18px
    fontWeight: 'bold', // 굵게
    backgroundColor: 'white',
  },
  textArea: {
    fontSize: 16, // 본문은 16px
    fontWeight: 'normal'
  }
});