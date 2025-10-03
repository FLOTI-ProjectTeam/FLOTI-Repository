import { useEffect, useState } from "react";
import { View, Text, FlatList, ActivityIndicator, Image, StyleSheet, TouchableOpacity } from "react-native";
import { useRouter } from "expo-router";

import { dummyPosts } from "@/__mocks__/tip";
import COLORS from "@/constants/colors"
import { useCommunitySearch } from "@/contexts/CommunitySearchContext";
import { TipPostResponse } from "@/types/community/tip";

export default function TipScreen() {
  const { search } = useCommunitySearch(); // 검색창에서 입력한 검색어 로드
  const [posts, setPosts] = useState<TipPostResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const router = useRouter();

  const fetchPosts = async () => {
    setLoading(true);

  //   try {
  //     const res = await getTipPosts(search); // 실제 API 호출
  //     setPosts(res.data);
  //   } catch (err) {
  //     console.error(err);
  //   } finally {
  //     setLoading(false);
  //   }
    
  // 더미 데이터 호출
    setTimeout(() => {
      setPosts(dummyPosts.filter(post => post.title.includes(search))); // 검색어 필터링
      setLoading(false);
    }, 500);
  };

  // 검색어 변경 시 fetchPosts 호출
  useEffect(() => {
    fetchPosts();
  }, [search]);

  // 로딩 상태
  if (loading) {
    return (
      <View style={[styles.container, styles.center]}>
        <ActivityIndicator size="large" color={COLORS.TINT.SLATE} />
      </View>
    );
  }

  // 글이 없을 때
  if (posts.length === 0) {
    return (
      <View style={[styles.container, styles.center]}>
        <Text style={styles.emptyText}>글이 없습니다.</Text>
      </View>
    );
  }

  // 글 리스트
  return (
    <View style={styles.container}>
      <FlatList
        data={posts}
        keyExtractor={(item) => item.id.toString()}
        contentContainerStyle={{ paddingBottom: 16 }}
        renderItem={({ item }) => (
          <TouchableOpacity onPress={() => router.push(`../../community/tip/${item.id}`)}>

            <View style={styles.card}>
              <Image source={{ uri: item.thumbnail }} style={styles.thumbnail} />
              <View style={styles.info}>
                <Text style={styles.title} numberOfLines={1}>
                  {item.title}
                </Text>
                <Text style={styles.meta}>
                  {item.author.nickname} • {item.id}분 전
                </Text>
                <Text style={styles.meta}>
                  댓글 {item.commentCount} • 추천 {item.likeCount}
                </Text>
              </View>
            </View>
          </TouchableOpacity>
        )}
      />
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
  card: {
    flexDirection: "row",
    padding: 12,
    borderWidth: 0,
    backgroundColor: COLORS.WHITE,
    marginBottom: 8,
    borderRadius: 8,
  },
  thumbnail: {
    width: 80,
    height: 80,
    marginRight: 12,
    borderRadius: 4,
  },
  info: {
    flex: 1,
    justifyContent: "space-between",
  },
  title: {
    fontWeight: "bold",
    color: COLORS.BLACK,
    marginBottom: 4,
  },
  meta: {
    color: COLORS.TEXT.MEDIUM_GRAY,
    fontSize: 12,
    marginBottom: 2,
  },
  emptyText: {
    color: COLORS.TEXT.MEDIUM_GRAY,
    fontSize: 14,
  },
});