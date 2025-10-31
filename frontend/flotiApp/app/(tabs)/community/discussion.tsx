import { View, Text, FlatList, StyleSheet, TouchableOpacity } from 'react-native';
import { useRouter } from 'expo-router';
import { useEffect, useState } from 'react';

import { dummyPosts } from '@/__mocks__/tip';
import SortDropdown, { SortOption } from '@/components/SortDropdown';
import { IconSymbol } from '@/components/ui/IconSymbol';
import { LoadingView, EmptyView } from '@/components/CommunityStateView';
import COLOR from '@/constants/colors';
import { STYLE } from '@/constants/styles';
import { useCommunitySearch } from '@/contexts/CommunitySearchContext';
import { TipPostResponse } from '@/types/community/tip';

type SortType = 'latest' | 'registered' | 'recentActivity';

const SORT_OPTIONS: SortOption[] = [
  { value: 'recentActivity', label: '최근활동순' },
  { value: 'latest', label: '최신순' },
  { value: 'registered', label: '등록순' }
];

export default function DiscussionListScreen() {
  const { searchTrigger } = useCommunitySearch();
  const [posts, setPosts] = useState<TipPostResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [sortType, setSortType] = useState<SortType>('recentActivity');
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

  // 토론 게시글 목록
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
          <TouchableOpacity activeOpacity={0.7} onPress={() => router.push(`../../community/discussion/${item.id}`)}>
            <View style={STYLE.CARD}>
              <View style={styles.info}>
                <Text style={styles.title} numberOfLines={1}>
                  {item.title}
                </Text>

                <Text style={styles.content}>{item.content}</Text>
                
                <View style={styles.stats}>
                  <View style={styles.statItem}>
                    <IconSymbol name="time" size={14} color={COLOR.ICON.GRAY_DARK} />
                    <Text style={styles.statText}>{item.commentCount}분 전</Text>
                  </View>
                  <View style={styles.statItem}>
                    <IconSymbol name="people" size={14} color={COLOR.ICON.GRAY_DARK} />
                    <Text style={styles.statText}>{item.likeCount}/5명</Text>
                  </View>
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
    color: 'black',
    marginBottom: 4
  },
  content: {
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
    marginLeft: 4
  }
});
