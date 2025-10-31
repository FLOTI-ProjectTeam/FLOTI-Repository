import { View, Text, StyleSheet } from 'react-native';
import { useLocalSearchParams } from 'expo-router';
import { useEffect, useState } from 'react';

import { dummyPosts } from '@/__mocks__/tip';
import COLOR from '@/constants/colors';
import { STYLE } from '@/constants/styles';
import { TipPostResponse } from '@/types/community/tip';
import BottomBar from '@/components/BottomBar';
import { LoadingView, EmptyView } from '@/components/CommunityStateView';

export default function TipDetailScreen() {
  const { id } = useLocalSearchParams();  // URL에서 id 가져오기
  const [post, setPost] = useState<TipPostResponse>();
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // 더미 데이터 호출
    const found = dummyPosts.find((p) => p.id.toString() === id);
    setPost(found);
    setLoading(false);
  }, [id]);

  if (loading) return <LoadingView />
  if (!post) return <EmptyView text='게시글을 찾을 수 없습니다.' />;

  return (
    <View style={STYLE.BASE_CONTAINER}>
      <View style={STYLE.WRAPPER}>
        {/* 제목 */}
        <Text style={styles.title}>{post.title}</Text>

        {/* 작성자 & 시간 */}
        <Text style={styles.author}>{post.author.nickname}</Text>
        <Text style={styles.time}>{post.createdAt}</Text>

        {/* 본문 */}
        <View style={styles.contentContainer}>
          <Text style={styles.content}>
            {post.content ?? '본문 내용이 여기에 표시됩니다.'}
          </Text>
        </View>
      </View>

      <BottomBar post={post}/>
    </View>
  );
}

const styles = StyleSheet.create({
  title: {
    fontSize: 20,
    fontWeight: 'bold',
    marginBottom: 8,
    color: 'black',
  },
  author: { fontSize: 14, color: COLOR.TEXT.GRAY_DARK, fontWeight: 'bold' },
  time: { fontSize: 12, color: COLOR.TEXT.GRAY_MEDIUM, marginBottom: 12 },
  contentContainer: {
    flex: 1,
    padding: 16,
    borderRadius: 12,
    backgroundColor: COLOR.BACKGROUND.SLATE_LIGHT
  },
  content: {
    fontSize: 15,
    lineHeight: 22,
    color: COLOR.TEXT.GRAY_DARK,
    marginBottom: 24,
  }
});
