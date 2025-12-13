import { View, Text, FlatList, Image, StyleSheet, TouchableOpacity } from 'react-native';
import { useEffect, useState } from 'react';

import { useCommunitySearch } from '@/contexts/CommunitySearchContext';
import { useNavigation } from '@/hooks/useNavigation';

import { dummyPosts } from '@/__mocks__/tip';
import { getTipPosts } from '@/api/community/tipApi';
import { TipPostResponse } from '@/types/community/tip';

import { IconSymbol } from '@/components/ui/IconSymbol';
import { LoadingView, EmptyView } from '@/components/feature/community/CommunityStateView';
import FilterBar, { SortType, SortOption } from '@/components/feature/community/FilterBar';

import { formatRelativeTime } from '@/utils/time';
import { STYLE } from '@/constants/styles';
import COLOR from '@/constants/colors';

const SORT_OPTIONS: SortOption[] = [
  { value: 'latest', label: '최신순' },
  { value: 'registered', label: '등록순' },
  { value: 'likes', label: '좋아요순' }
];

export default function TipListScreen() {
  const { navigateTo } = useNavigation();
  const { searchTrigger } = useCommunitySearch(); // 실제 사용할 검색어 로드

  const [loading, setLoading] = useState(true);
  const [posts, setPosts] = useState<TipPostResponse[]>([]);
  const [sortType, setSortType] = useState<SortType>('latest'); // 정렬순

  /* 사이드 이펙트 */
  useEffect(() => {
    loadPosts();
  }, [searchTrigger, sortType]);

  /* API 호출 */
  const loadPosts = async () => {
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

  if (loading) return <LoadingView />

  return (
    <View style={STYLE.CONTENT_CONTAINER}>
      <FilterBar
        sort={{ options: SORT_OPTIONS, value: sortType, onChange: setSortType }}
      />

      <FlatList
        data={posts}
        keyExtractor={(item) => item.id.toString()}
        style={STYLE.WRAPPER}
        contentContainerStyle={{
          flexGrow: 1, // ScrollView가 화면 전체 높이 차지
          paddingBottom: 8
        }}
        ListEmptyComponent={<EmptyView />}
        renderItem={({ item }) => (
          <TouchableOpacity
            activeOpacity={0.7} // 클릭 시 투명도 설정
            onPress={() => navigateTo(`/community/tip/${item.id}`)}
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
                : <Image source={require('@assets/images/no-thumbnail.jpg')} style={styles.thumbnail} />
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
  title: { fontSize: 16, fontWeight: 700 },
  authorInfo: { marginBottom: 2, fontSize: 12, color: COLOR.TEXT.GRAY_MEDIUM },
  stats: { flexDirection: 'row', alignItems: 'center', gap: 12 },
  statItem: { flexDirection: 'row', alignItems: 'center' },
  statText: { marginLeft: 4, fontSize: 12, color: COLOR.TEXT.GRAY_MEDIUM },
  thumbnail: { width: 80, height: 80, borderRadius: 4 }
});