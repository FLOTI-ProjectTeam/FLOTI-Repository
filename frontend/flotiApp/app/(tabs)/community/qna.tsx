import { View, Text, FlatList, StyleSheet, TouchableOpacity } from 'react-native';
import { useRouter } from 'expo-router';
import { useEffect, useState } from 'react';

import { dummyPosts } from '@/__mocks__/tip';
import SortDropdown, { SortOption } from '@/components/SortDropdown';
import COLOR from '@/constants/colors';
import { useCommunitySearch } from '@/contexts/CommunitySearchContext';
import { TipPostResponse } from '@/types/community/tip';
import { LoadingView, EmptyView } from '@/components/CommunityStateView';
import { formatRelative } from '@/utils/time';
import { STYLE } from '@/constants/styles';

type SortType = 'latest' | 'registered' | 'comments';

const SORT_OPTIONS: SortOption[] = [
  { value: 'latest', label: '최신순' },
  { value: 'registered', label: '등록순' },
  { value: 'comments', label: '답변순' }
];

export default function QnaListScreen() {
  const { searchTrigger } = useCommunitySearch();
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

  useEffect(() => {
    fetchPosts();
  }, [searchTrigger, sortType]);

  if (loading) return <LoadingView />
  if (posts.length === 0) return <EmptyView />

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
          <TouchableOpacity activeOpacity={0.7} onPress={() => router.push(`../../community/qna/${item.id}`)}>
            <View style={[STYLE.CARD, STYLE.ROW]}>
              <View style={styles.info}>
                <Text style={styles.title} numberOfLines={1}>
                  {item.title}
                </Text>

                <Text style={styles.authorInfo}>
                  {item.author.nickname} • {formatRelative(item.createdAt)}
                </Text>
                
                <View style={styles.commentBadge}>
                  <Text style={styles.commentCount}>{item.commentCount}</Text>
                </View>
              </View>
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
  commentBadge: {
    position: 'absolute',
    top: 0,
    right: 0,
    borderColor: COLOR.TINT.GRAY,
    borderWidth: 1,
    borderRadius: 28,
    width: 28, height: 28,
    justifyContent: 'center',
    alignItems: 'center',
  },
  commentCount: {
    color: COLOR.TEXT.NAVY,
    fontSize: 12,
    fontWeight: 600,
  }
});