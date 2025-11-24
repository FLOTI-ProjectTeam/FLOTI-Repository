import { View, Text, StyleSheet, ScrollView } from 'react-native';
import { useLocalSearchParams } from 'expo-router';
import { useEffect, useState } from 'react';

import { dummyPosts } from '@/__mocks__/tip';
import { getTipPost, toggleLikeTipPost } from '@/api/community/tipApi';
import BreakAllText from '@/components/ui/BreakAllText';
import { Header } from '@/components/ui/Header';
import BottomBar from '@/components/feature/community/tip/BottomBar';
import { LoadingView, EmptyView } from '@/components/feature/community/CommunityStateView';
import { showToast } from '@/utils/toast';
import { TipPostResponse } from '@/types/community/tip';
import COLOR from '@/constants/colors';
import { STYLE } from '@/constants/styles';

export default function TipDetailScreen() {
  const [loading, setLoading] = useState(true);
  const { postId } = useLocalSearchParams();  // URL에서 게시글 ID 가져오기
  const [post, setPost] = useState<TipPostResponse>();

  /* API 호출 */
  const fetchPost = async () => {
    try {
      const response = await getTipPost(Number(postId));
      setPost(response.data);
    } catch (error) {
      // 테스트용
      const filtered = dummyPosts.find((post) => post.id.toString() === postId);
      setPost(filtered);
    } finally {
      setLoading(false);
    }
  }

  const callToggleLikeTipPost = () => toggleLikeTipPost(post!.id);

  // 게시글 ID 변경 시 실행
  useEffect(() => {
    fetchPost();
  }, [postId]);

  /* 이벤트 핸들러 */
  const handleToggleLike = async () => {
    try {
      await callToggleLikeTipPost();

      // 좋아요 갱신
      setPost(prev =>
        prev
          ? { 
              ...prev,
              liked: !prev.liked,
              likeCount: prev.likeCount + (prev.liked ? -1 : 1)
            }
          : prev
      );
    } catch (error) {
      if (post!.liked) showToast('좋아요 취소 실패', 'error');
      else showToast('좋아요 실패', 'error');
    }
  }

  if (loading) return <LoadingView />
  if (!post) return <EmptyView text='게시글을 찾을 수 없습니다.' />;

  return (
    <View style={STYLE.BASE_CONTAINER}>
      <Header title="TIP" />
      
      <ScrollView 
        style={STYLE.WRAPPER} 
        contentContainerStyle={{ 
          flexGrow: 1, // 화면 전체 높이 차지
          paddingBottom: 16
        }}
      >
        {/* 제목 */}
        <BreakAllText style={styles.title}>{post.title}</BreakAllText>

        {/* 작성자, 작성일 */}
        <Text style={styles.author}>{post.author.nickname}</Text>
        <Text style={styles.time}>{post.createdAt}</Text>

        {/* 본문 */}
        <View style={styles.contentContainer}>
          <BreakAllText style={styles.content}>{post.content}</BreakAllText>
        </View>
      </ScrollView>

      <BottomBar post={post} onToggleLike={handleToggleLike} />
    </View>
  );
}

const styles = StyleSheet.create({
  title: {
    marginBottom: 8,
    fontSize: 20,
    fontWeight: 700,
    color: 'black'
  },
  author: { fontSize: 14, fontWeight: 600, color: COLOR.TEXT.GRAY_DARK },
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