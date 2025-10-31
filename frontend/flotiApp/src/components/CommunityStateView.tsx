import { View, Text, ActivityIndicator } from 'react-native';
import COLOR from '@/constants/colors';
import { STYLE } from '@/constants/styles';

// 로딩 화면
export function LoadingView() {
  return (
    <View style={[STYLE.CONTENT_CONTAINER, STYLE.CENTER]}>
      <ActivityIndicator size='large' color={COLOR.TINT.SLATE} />
    </View>
  );
}

// 빈 화면
export function EmptyView({ text = '게시글이 없습니다.' }: {
  text?: string;
}) {
  return (
    <View style={[STYLE.CONTENT_CONTAINER, STYLE.CENTER]}>
      <Text style={STYLE.EMPTY_TEXT}>{text}</Text>
    </View>
  );
}