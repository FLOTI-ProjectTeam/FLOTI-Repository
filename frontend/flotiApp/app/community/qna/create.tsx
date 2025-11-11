import { View } from 'react-native';
import Toast from 'react-native-toast-message';
import { useRouter } from 'expo-router';

import EditorHeader from '@/components/ui/EditorHeader';
import { CreateInputView } from '@/components/InputView';
import { STYLE } from '@/constants/styles';

export default function QnaCreateScreen() {
  const router = useRouter();

  const handleSave = () => {
    Toast.show({
      type: 'custom_success',
      position: 'bottom',
      visibilityTime: 1500,
      props: { message: '임시저장 되었습니다.' }
    });
  };

  const handleSubmit = () => {
    console.log('등록 완료');
    router.back();
  };

  return (
    <View style={STYLE.BASE_CONTAINER}>
      <EditorHeader onSave={handleSave} onSubmit={handleSubmit} />
      <CreateInputView />
    </View>
  );
}