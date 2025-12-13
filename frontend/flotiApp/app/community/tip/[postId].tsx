import { View, Text, StyleSheet, ScrollView } from 'react-native';
import { useLocalSearchParams } from 'expo-router';
import { useContext, useEffect, useState } from 'react';

import { UserContext } from '@/contexts/UserContext';
import { useModal } from '@/hooks/useModal';

import { dummyPosts } from '@/__mocks__/tip';
import { getTipPost, toggleLikeTipPost } from '@/api/community/tipApi';
import { TipPostResponse } from '@/types/community/tip';

import BreakAllText from '@/components/ui/BreakAllText';
import { Header } from '@/components/ui/Header';
import ConfirmModal from '@/components/ui/ConfirmModal';
import BottomBar from '@/components/feature/community/tip/BottomBar';
import { LoadingView } from '@/components/feature/community/CommunityStateView';

import { showToast } from '@/utils/toast';
import { formatDetailTime } from '@/utils/time';
import COLOR from '@/constants/colors';
import { STYLE } from '@/constants/styles';

export default function TipDetailScreen() {
  const { postId } = useLocalSearchParams();  // URL에서 게시글 ID 가져오기
  const { modalVisible, openModal, closeModal } = useModal();

  const [loading, setLoading] = useState(true);
  const [post, setPost] = useState<TipPostResponse>();

  const userContext = useContext(UserContext);  // 사용자 상태
  const isAuthor = (post?.author.username === userContext?.username);

  /* API 호출 */
  const callToggleLikeTipPost = () => toggleLikeTipPost(post!.id);

  /* 사이드 이펙트 */
  useEffect(() => {
    loadPost();
  }, [postId]);

  /* API 호출 */
  const loadPost = async () => {
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

  if (!post) return;

  /* 이벤트 핸들러 */
  const handleToggleLike = async () => {
    if (isAuthor) {
      openModal();
      return;
    }

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
      if (post.liked) showToast('좋아요 취소 실패', 'error');
      else showToast('좋아요 실패', 'error');
    }
  }

  if (loading) return <LoadingView />;

  return (
    <View style={STYLE.BASE_CONTAINER}>
      <Header title='TIP' />

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
        <Text style={styles.time}>{formatDetailTime(post.createdAt)}</Text>

        {/* 본문 */}
        <View style={styles.contentContainer}>
          <BreakAllText style={styles.content}>{post.content}</BreakAllText>
        </View>
      </ScrollView>

      <BottomBar post={post} onToggleLike={handleToggleLike} />

      <ConfirmModal
        visible={modalVisible}
        title='내 게시글은 좋아요 할 수 없습니다.'
        onClose={closeModal}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  title: { marginBottom: 8, fontSize: 20, fontWeight: 700 },
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