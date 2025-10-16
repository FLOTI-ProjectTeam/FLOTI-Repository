// [app/community/tip/[id]/comments.tsx]
import React, { useEffect, useState } from "react";
import { View, Text, FlatList, ActivityIndicator, StyleSheet } from "react-native";
import { useLocalSearchParams } from "expo-router";

import { dummyComments } from "@/__mocks__/tip";
import Header from "@/components/ui/Header";
import { CommentResponse } from "@/types/community/tip";
import STYLES from "@/constants/styles";
import COLORS from "@/constants/colors";

export default function CommentListScreen() {
  const { id } = useLocalSearchParams(); // URL에서 글 ID 가져오기
  const [comments, setComments] = useState<CommentResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const commentCount = dummyComments.filter(c => c.postId.toString() === id).length;

  useEffect(() => {
    // 더미 데이터 호출
    const filtered = dummyComments.filter(c => c.postId.toString() === id);
    setComments(filtered);
    setLoading(false);
  }, [id]);

  if (loading) {
    return (
      <View style={[STYLES.CONTAINER, styles.center]}>
        <Header title={`댓글 (${commentCount})`} />
        <ActivityIndicator size="large" color={COLORS.TINT.SLATE} />
      </View>
    );
  }

  if (comments.length === 0) {
    return (
      <View style={[STYLES.CONTAINER, styles.center]}>
        <Header title={`댓글 (${commentCount})`} />
        <Text style={styles.emptyText}>댓글이 없습니다.</Text>
      </View>
    );
  }

  return (
    <View style={STYLES.CONTAINER}>
        <Header title={`댓글 (${commentCount})`} />

        <FlatList
            data={comments}
            keyExtractor={(item) => item.id.toString()}
            renderItem={({ item }) => (
                <View style={styles.commentItem}>
                    <Text style={styles.author}>{item.author.nickname}</Text>
                    <Text style={styles.content}>{item.content}</Text>
                    <Text style={styles.time}>방금 전</Text>
                </View>
            )}
        />
    </View>
  );
}

const styles = StyleSheet.create({
  center: { flex: 1, justifyContent: "center", alignItems: "center" },
  emptyText: { color: COLORS.TEXT.GRAY_MEDIUM, fontSize: 16 },
  commentItem: {
    padding: 12,
    borderBottomWidth: 1,
    borderBottomColor: COLORS.TINT.GRAY_LIGHT,
  },
  author: { fontWeight: "bold", marginBottom: 4 },
  content: { fontSize: 16 },
  time: { fontSize: 12, color: COLORS.TEXT.GRAY_MEDIUM, marginTop: 4 },
});
