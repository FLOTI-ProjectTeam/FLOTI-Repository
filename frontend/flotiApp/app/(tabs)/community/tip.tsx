import { View, Text, FlatList, Image, StyleSheet, TouchableOpacity } from 'react-native';
import { useRouter } from 'expo-router';
import { useEffect, useState } from 'react';

import { dummyPosts } from '@/__mocks__/tip';
import { getTipPosts } from '@/api/community/tipApi';
import { IconSymbol } from '@/components/ui/IconSymbol';
import { LoadingView, EmptyView } from '@/components/feature/community/CommunityStateView';
import FilterBar, { SortType, SortOption } from '@/components/feature/community/FilterBar';
import { useCommunitySearch } from '@/contexts/CommunitySearchContext';
import { formatRelativeTime } from '@/utils/time';
import { TipPostResponse } from '@/types/community/tip';
import { STYLE } from '@/constants/styles';
import COLOR from '@/constants/colors';

const SORT_OPTIONS: SortOption[] = [
  { value: 'latest', label: '최신순' },
  { value: 'registered', label: '등록순' },
  { value: 'likes', label: '좋아요순' }
];

export default function TipListScreen() {
  const router = useRouter();

  const [loading, setLoading] = useState(true);
  const [posts, setPosts] = useState<TipPostResponse[]>([]);
  
  const { searchTrigger } = useCommunitySearch(); // 실제 사용할 검색어 로드
  const [sortType, setSortType] = useState<SortType>('latest'); // 정렬순

  /* API 호출 */
  const fetchPosts = async () => {  
    try {
      setLoading(true);
      const response = await getTipPosts(searchTrigger);
      setPosts(response.data.content);
    } catch (error) {
      // 테스트용
      const filtered = dummyPosts.filter(post => post.title.includes(searchTrigger));
      setPosts(filtered);
    } finally {
      setLoading(false);
    }
  };

  // 검색하거나 정렬순 변경 시 실행
  useEffect(() => {
    fetchPosts();
  }, [searchTrigger, sortType]);

  /* 이벤트 핸들러 */
  const handleGoToTipDetail = (postId: number) => router.push(`/community/tip/${postId}`);

  if (loading) return <LoadingView />
  if (posts.length === 0) return <EmptyView />

  return (
    <View style={STYLE.CONTENT_CONTAINER}>
      <FilterBar
        sort={{ options: SORT_OPTIONS, value: sortType, onChange: setSortType }}
      />

      <FlatList
        data={posts}
        keyExtractor={(item) => item.id.toString()}
        style={STYLE.WRAPPER}
        contentContainerStyle={{ paddingBottom: 8 }}
        renderItem={({ item }) => (
          <TouchableOpacity 
            activeOpacity={0.7} // 클릭 시 투명도 설정
            onPress={() => handleGoToTipDetail(item.id)}
          >
            <View style={[STYLE.CARD, STYLE.ROW]}>
              <View style={styles.info}>
                {/* 제목 */}
                <Text style={styles.title} numberOfLines={1}>{item.title}</Text>

                {/* 작성자, 작성일 */}
                <Text style={styles.authorInfo}>
                  {item.author.nickname} • {formatRelativeTime(item.createdAt)}
                </Text>

                {/* 좋아요·댓글수 */}  
                <View style={styles.stats}>
                  <View style={styles.statItem}>
                    <IconSymbol name="heart" size={16} color={COLOR.TINT.GRAY_DARK} />
                    <Text style={styles.statText}>{item.likeCount}</Text>
                  </View>
                  <View style={styles.statItem}>
                    <IconSymbol name="comment" size={16} color={COLOR.TINT.GRAY_DARK} />
                    <Text style={styles.statText}>{item.commentCount}</Text>
                  </View>
                </View>
              </View>

              {/* 섬네일 */}
              {item.thumbnail 
                ? <Image source={{ uri: item.thumbnail }} style={styles.thumbnail} />
                : <View style={[styles.thumbnail, { backgroundColor: 'lightgray' }]} />
              }
            </View>
          </TouchableOpacity>
        )}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  info: { flex: 1, justifyContent: 'space-between', gap: 4 },
  title: { fontSize: 16, fontWeight: 700, color: 'black' },
  authorInfo: { marginBottom: 2, fontSize: 12, color: COLOR.TEXT.GRAY_MEDIUM },
  stats: { flexDirection: 'row', alignItems: 'center', gap: 12 },
  statItem: { flexDirection: 'row', alignItems: 'center' },
  statText: { marginLeft: 4, fontSize: 12, color: COLOR.TEXT.GRAY_MEDIUM },
  thumbnail: { width: 80, height: 80, borderRadius: 4 }
});