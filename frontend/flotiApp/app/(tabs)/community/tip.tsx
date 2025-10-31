import { View, Text, FlatList, Image, StyleSheet, TouchableOpacity } from 'react-native';
import { useRouter } from 'expo-router';
import { useEffect, useState } from 'react';

import { dummyPosts } from '@/__mocks__/tip';
import { LoadingView, EmptyView } from '@/components/CommunityStateView';
import SortDropdown, { SortOption } from '@/components/SortDropdown';
import { IconSymbol } from '@/components/ui/IconSymbol';
import COLOR from '@/constants/colors';
import { useCommunitySearch } from '@/contexts/CommunitySearchContext';
import { TipPostResponse } from '@/types/community/tip';
import { formatRelative } from '@/utils/time';
import { STYLE } from '@/constants/styles';

type SortType = 'latest' | 'registered' | 'likes';

const SORT_OPTIONS: SortOption[] = [
  { value: 'latest', label: '최신순' },
  { value: 'registered', label: '등록순' },
  { value: 'likes', label: '인기순' }
];

export default function TipListScreen() {
  const { searchTrigger } = useCommunitySearch(); // 실제 사용할 검색어 로드
  const [posts, setPosts] = useState<TipPostResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [sortType, setSortType] = useState<SortType>('latest');
  const router = useRouter();

  const fetchPosts = async () => {
    setLoading(true);
    
    // 더미 데이터 호출
    setTimeout(() => {
      const filtered = dummyPosts.filter(post => post.title.includes(searchTrigger));  // 검색어 필터링
      setPosts(filtered); // 서버에서 정렬 처리
      setLoading(false);
    }, 500);
  };

  // 검색 또는 정렬순 변경 시 fetchPosts 호출
  useEffect(() => {
    fetchPosts();
  }, [searchTrigger, sortType]);

  if (loading) return <LoadingView /> // 게시글 로딩
  if (posts.length === 0) return <EmptyView />  // 게시글 없음

  // 게시글 목록
  return (
    <View style={STYLE.CONTENT_CONTAINER}>
      <SortDropdown 
        options={SORT_OPTIONS}
        selectedValue={sortType}
        onSelect={(value) => setSortType(value as SortType)}
      />

      <FlatList
        data={posts}
        keyExtractor={(item) => item.id.toString()}
        contentContainerStyle={{ paddingBottom: 16 }}
        renderItem={({ item }) => (
          <TouchableOpacity activeOpacity={0.7} onPress={() => router.push(`../../community/tip/${item.id}`)}>
            <View style={[STYLE.CARD, STYLE.ROW]}>
              <View style={styles.info}>
                <Text style={styles.title} numberOfLines={1}>
                  {item.title}
                </Text>

                <Text style={styles.authorInfo}>
                  {item.author.nickname} • {formatRelative(item.createdAt)}
                </Text>
                
                <View style={styles.stats}>
                  <View style={styles.statItem}>
                    <IconSymbol name="comment" size={14} color={COLOR.ICON.GRAY_DARK} />
                    <Text style={styles.statText}>{item.commentCount}</Text>
                  </View>
                  <View style={styles.statItem}>
                    <IconSymbol name="thumbs" size={14} color={COLOR.ICON.GRAY_DARK} />
                    <Text style={styles.statText}>{item.likeCount}</Text>
                  </View>
                </View>
              </View>

              {/* 섬네일 */}
              {item.thumbnail ? (
                <Image source={{ uri: item.thumbnail ?? undefined }} style={styles.thumbnail} />
              ) : null}
            </View>
          </TouchableOpacity>
        )}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  info: {
    flex: 1,
    gap: 4,
    justifyContent: 'space-between'
  },
  title: {
    fontSize: 16,
    fontWeight: 'bold',
    color: 'black'
  },
  authorInfo: {
    marginBottom: 2,
    fontSize: 12,
    color: COLOR.TEXT.GRAY_MEDIUM
  },
  stats: { 
    flexDirection: 'row', 
    alignItems: 'center', 
    gap: 12 
  },
  statItem: { flexDirection: 'row', alignItems: 'center' },
  statText: {
    color: COLOR.TEXT.GRAY_MEDIUM,
    fontSize: 12,
    marginLeft: 4 // 아이콘과 숫자 사이 간격
  },
  thumbnail: {
    width: 80, height: 80,
    borderRadius: 4
  }
});