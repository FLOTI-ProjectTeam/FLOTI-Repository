import { View, Text, StyleSheet, ScrollView, Dimensions } from 'react-native';
import { useLocalSearchParams } from 'expo-router';
import { useEffect, useState } from 'react';

import { dummyPosts } from '@/__mocks__/tip';
import BreakAllText from '@/components/ui/BreakAllText';
import BottomBar from '@/components/BottomBar';
import { LoadingView, EmptyView } from '@/components/CommunityStateView';
import COLOR from '@/constants/colors';
import { STYLE } from '@/constants/styles';
import { TipPostResponse } from '@/types/community/tip';

export default function TipDetailScreen() {
  const { postId } = useLocalSearchParams();  // URL에서 게시글 ID 가져오기
  const [loading, setLoading] = useState(true);
  const [post, setPost] = useState<TipPostResponse>();
  const [isScrollable, setIsScrollable] = useState(false);  // 스크롤 가능 여부
  const screenHeight = Dimensions.get('window').height; // 화면에 표시되는 영역의 높이

  // ScrollView 콘텐츠 높이가 화면보다 크면 스크롤 가능 상태로 설정
  const handleContentSizeChange = (contentWidth: number, contentHeight: number) => {
    setIsScrollable(contentHeight > screenHeight);
  };

  useEffect(() => {
    // 더미 데이터 호출
    const found = dummyPosts.find((p) => p.id.toString() === postId);
    setPost(found);
    setLoading(false);
  }, [postId]);

  if (loading) return <LoadingView />
  if (!post) return <EmptyView text='게시글을 찾을 수 없습니다.' />;

  return (
    <View style={STYLE.BASE_CONTAINER}>
      <ScrollView 
        style={STYLE.WRAPPER} 
        onContentSizeChange={handleContentSizeChange}
        contentContainerStyle={{ 
          flexGrow: 1, // ScrollView가 화면 전체 높이 차지
          paddingBottom: isScrollable ? 32 : 0 // 스크롤 가능한 경우에만 하단 여백 추가
        }}
      >
        {/* 제목 */}
        <BreakAllText style={styles.title}>{post.title}</BreakAllText>

        {/* 작성자 & 시간 */}
        <Text style={styles.author}>{post.author.nickname}</Text>
        <Text style={styles.time}>{post.createdAt}</Text>

        {/* 본문 */}
        <View style={styles.contentContainer}>
          <BreakAllText style={styles.content}>{post.content}</BreakAllText>
        </View>
      </ScrollView>

      <BottomBar post={post}/>
    </View>
  );
}

const styles = StyleSheet.create({
  title: {
    marginBottom: 8,
    fontSize: 20,
    fontWeight: 'bold',
    color: 'black'
  },
  author: { fontSize: 14, fontWeight: 'bold', color: COLOR.TEXT.GRAY_DARK },
  time: { fontSize: 12, color: COLOR.TEXT.GRAY_MEDIUM },
  contentContainer: {
    flex: 1,
    marginTop: 12,
    padding: 16,
    borderRadius: 12,
    backgroundColor: COLOR.BACKGROUND.SLATE_LIGHT
  },
  content: { fontSize: 15, lineHeight: 22, color: COLOR.TEXT.GRAY_DARK }
});
