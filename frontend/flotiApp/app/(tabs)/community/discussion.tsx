import { View, Text, FlatList, StyleSheet, TouchableOpacity } from 'react-native';
import { useRouter } from 'expo-router';
import { useEffect, useState } from 'react';

import { dummyPosts } from '@/__mocks__/tip';
import { IconSymbol } from '@/components/ui/IconSymbol';
import FilterBar, { SortType, SortOption } from '@/components/FilterBar';
import { LoadingView, EmptyView } from '@/components/CommunityStateView';
import { useCommunitySearch } from '@/contexts/CommunitySearchContext';
import { TipPostResponse } from '@/types/community/tip';
import COLOR from '@/constants/colors';
import { STYLE } from '@/constants/styles';

const SORT_OPTIONS: SortOption[] = [
  { value: 'recentActivity', label: '최근활동순' },
  { value: 'latest', label: '최신순' },
  { value: 'registered', label: '등록순' }
];

export default function DiscussionListScreen() {
  const router = useRouter();

  const [loading, setLoading] = useState(true);
  const [posts, setPosts] = useState<TipPostResponse[]>([]);

  const { searchTrigger } = useCommunitySearch(); // 실제 사용할 검색어 로드
  const [sortType, setSortType] = useState<SortType>('recentActivity'); // 정렬순
  const [isChecked, setIsChecked] = useState(false);  // 체크 여부

  /* API 호출 */
  const fetchRooms = async () => {
    setLoading(true);
    
    // 더미 데이터 호출
    setTimeout(() => {
      const filtered = dummyPosts.filter(post => post.title.includes(searchTrigger));
      setPosts(filtered);
      setLoading(false);
    }, 500);
  };

  // 검색하거나 정렬순 또는 체크 상태 변경 시 실행
  useEffect(() => {
    fetchRooms();
  }, [searchTrigger, sortType, isChecked]);

  /* 이벤트 핸들러 */
  const handleGoToDiscussionDetail = (postId: number) => router.push(`/community/discussion/${postId}`);

  if (loading) return <LoadingView />
  if (posts.length === 0) return <EmptyView />

  return (
    <View style={STYLE.FLEX}>
      <FilterBar
        sort={{ options: SORT_OPTIONS, value: sortType, onChange: setSortType }}
        check={{ label: '참여 중인 토론', value: isChecked, onChange: setIsChecked }}
      />

      <FlatList
        data={posts}
        keyExtractor={(item) => item.id.toString()}
        style={STYLE.CONTENT_CONTAINER}
        contentContainerStyle={{ paddingBottom: 8 }}
        renderItem={({ item }) => (
          <TouchableOpacity
            activeOpacity={0.7} // 클릭 시 투명도 설정
            onPress={() => handleGoToDiscussionDetail(item.id)}
          >
            <View style={STYLE.CARD}>
              <View style={styles.info}>
                {/* 제목 */}
                <Text style={styles.title} numberOfLines={1}>
                  {item.title}
                </Text>

                {/* 소개 */}
                <Text style={styles.content}>{item.content}</Text>
                
                {/* 활동일 & 인원 */}
                <View style={styles.stats}>
                  <View style={styles.statItem}>
                    <IconSymbol name="time" size={14} color={COLOR.TINT.GRAY_DARK} />
                    <Text style={styles.statText}>{item.commentCount}분 전</Text>
                  </View>
                  <View style={styles.statItem}>
                    <IconSymbol name="people" size={14} color={COLOR.TINT.GRAY_DARK} />
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
  info: { flex: 1, justifyContent: 'space-between', gap: 4 },
  title: { fontSize: 16, fontWeight: 'bold', color: 'black' },
  content: { marginBottom: 2, fontSize: 12, color: COLOR.TEXT.GRAY_MEDIUM },
  stats: { flexDirection: 'row', alignItems: 'center', gap: 12 },
  statItem: { flexDirection: 'row', alignItems: 'center' },
  statText: { marginLeft: 4, fontSize: 12, color: COLOR.TEXT.GRAY_MEDIUM }
});
