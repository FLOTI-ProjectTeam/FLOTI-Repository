import { useEffect, useState } from "react";
import { View, Text, StyleSheet, ActivityIndicator, Image, FlatList, TextInput, TouchableOpacity } from "react-native";
import { useLocalSearchParams } from "expo-router";

import { dummyPosts } from "@/__mocks__/tip";
import COLORS from "@/constants/colors";
import { TipPostResponse } from "@/types/community/tip";

export default function TipDetailScreen() {
  const { id } = useLocalSearchParams();    // URL에서 id 가져오기
  const [post, setPost] = useState<TipPostResponse | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // 여기서는 더미 데이터 사용
    const found = dummyPosts.find((p) => p.id.toString() === id);
    setPost(found ?? null);
    setLoading(false);

    // 실제 API 호출 시:
    // getTipPostById(id).then(res => setPost(res.data)).finally(() => setLoading(false));
  }, [id]);

  if (loading) {
    return (
      <View style={[styles.container, styles.center]}>
        <ActivityIndicator size="large" color={COLORS.TINT.SLATE} />
      </View>
    );
  }

  if (!post) {
    return (
      <View style={[styles.container, styles.center]}>
        <Text style={styles.emptyText}>글을 찾을 수 없습니다.</Text>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      {/* 제목 */}
      <Text style={styles.title}>{post.title}</Text>

      {/* 작성자 + 시간 */}
      <Text style={styles.meta}>
        {post.author.nickname} • {post.id}분 전
      </Text>

      {/* 이미지 */}
      {post.thumbnail && (
        <Image source={{ uri: post.thumbnail }} style={styles.thumbnail} />
      )}

      {/* 본문 */}
      <Text style={styles.content}>
        {post.content ?? "본문 내용이 여기에 표시됩니다."}
      </Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 16,
    backgroundColor: COLORS.BACKGROUND.LIGHT_SLATE,
  },
  center: {
    justifyContent: "center",
    alignItems: "center",
  },
  title: {
    fontSize: 20,
    fontWeight: "bold",
    marginBottom: 8,
    color: COLORS.BLACK,
  },
  meta: {
    fontSize: 12,
    color: COLORS.TEXT.MEDIUM_GRAY,
    marginBottom: 12,
  },
  thumbnail: {
    width: "100%",
    height: 200,
    borderRadius: 8,
    marginBottom: 16,
  },
  content: {
    fontSize: 14,
    lineHeight: 20,
    color: COLORS.TEXT.DARK_GRAY,
    marginBottom: 24,
  },
  sectionTitle: {
    fontSize: 16,
    fontWeight: "bold",
    marginBottom: 8,
    color: COLORS.BLACK,
  },
  commentCard: {
    backgroundColor: COLORS.WHITE,
    padding: 12,
    borderRadius: 8,
    marginBottom: 8,
  },
  commentAuthor: {
    fontWeight: "bold",
    marginBottom: 4,
    color: COLORS.TEXT.NAVY,
  },
  commentContent: {
    color: COLORS.TEXT.DARK_GRAY,
  },
  commentInputContainer: {
    flexDirection: "row",
    alignItems: "center",
    marginTop: 12,
    borderTopWidth: 1,
    borderTopColor: COLORS.TINT.LIGHT_GRAY,
    paddingTop: 8,
  },
  commentInput: {
    flex: 1,
    backgroundColor: COLORS.WHITE,
    paddingHorizontal: 12,
    paddingVertical: 8,
    borderRadius: 8,
    marginRight: 8,
  },
  commentButton: {
    backgroundColor: COLORS.BUTTON.NAVY,
    paddingHorizontal: 16,
    paddingVertical: 10,
    borderRadius: 8,
  },
  commentButtonText: {
    color: COLORS.WHITE,
    fontWeight: "bold",
  },
  emptyText: {
    color: COLORS.TEXT.MEDIUM_GRAY,
    textAlign: "center",
    marginTop: 20,
  },
});
