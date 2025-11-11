import { View, Text, StyleSheet, ScrollView } from 'react-native';
import { useLocalSearchParams } from 'expo-router';
import { useEffect, useState } from 'react';

import { dummyPosts } from '@/__mocks__/tip';
import { getTipPost, toggleLikeTipPost } from '@/api/community/tipApi';
import BreakAllText from '@/components/ui/BreakAllText';
import BottomBar from '@/components/BottomBar';
import { LoadingView, EmptyView } from '@/components/CommunityStateView';
import { TipPostResponse } from '@/types/community/tip';
import COLOR from '@/constants/colors';
import { STYLE } from '@/constants/styles';
import { showToast } from '@/utils/toast';

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
      // 서버 호출 실패 시 더미 데이터로 대체
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

  const handleToggleLike = async () => {
    try {
      await callToggleLikeTipPost();

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
      const action = post!.liked ? '좋아요 취소' : '좋아요';
      showToast(`${action} 중 오류가 발생했습니다.`, 'error');
    }
  }

  if (loading) return <LoadingView />
  if (!post) return <EmptyView text='게시글을 찾을 수 없습니다.' />;

  return (
    <View style={STYLE.BASE_CONTAINER}>
      <ScrollView 
        style={STYLE.WRAPPER} 
        contentContainerStyle={{ 
          flexGrow: 1, // 화면 전체 높이 차지
          paddingBottom: 16
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

      <BottomBar post={post} onToggleLike={handleToggleLike} />
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
