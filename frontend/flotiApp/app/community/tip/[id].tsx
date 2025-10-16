import { View, Text, StyleSheet, ActivityIndicator } from 'react-native';
import { useLocalSearchParams } from 'expo-router';
import { useEffect, useState } from 'react';

import { dummyPosts } from '@/__mocks__/tip';
import COLORS from '@/constants/colors';
import { TipPostResponse } from '@/types/community/tip';
import BottomBar from '@/components/BottomBar';
import STYLES from '@/constants/styles';

export default function TipDetailScreen() {
  const { id } = useLocalSearchParams();  // URL에서 id 가져오기
  const [post, setPost] = useState<TipPostResponse | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // 더미 데이터 호출
    const found = dummyPosts.find((p) => p.id.toString() === id);
    setPost(found ?? null);
    setLoading(false);
  }, [id]);

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
      <View style={styles.container}>
        {/* 제목 */}
        <Text style={styles.title}>{post.title}</Text>

        {/* 작성자 및 시간 */}
        <Text style={styles.meta}>
          {post.author.nickname} • {post.id}분 전
        </Text>

        {/* 본문 */}
        <View style={styles.contentContainer}>
          <Text style={styles.content}>
            {post.content ?? "본문 내용이 여기에 표시됩니다."}
          </Text>
        </View>
      </View>

      <BottomBar post={post}/>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 16
  },
  center: {
    justifyContent: 'center',
    alignItems: 'center',
  },
  title: {
    fontSize: 20,
    fontWeight: 'bold',
    marginBottom: 8,
    color: 'black',
  },
  meta: {
    fontSize: 12,
    color: COLORS.TEXT.GRAY_MEDIUM,
    marginBottom: 12,
  },
  contentContainer: {
    flex: 1,
    padding: 16,
    borderRadius: 12,
    backgroundColor: COLORS.BACKGROUND.SLATE_LIGHT,
  },
  content: {
    fontSize: 14,
    lineHeight: 20,
    color: COLORS.TEXT.GRAY_DARK,
    marginBottom: 24,
  },
  emptyText: {
    color: COLORS.TEXT.GRAY_MEDIUM,
    textAlign: 'center',
    marginTop: 20,
  }
});
