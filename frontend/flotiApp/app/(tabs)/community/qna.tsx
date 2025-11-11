import { View, Text, FlatList, StyleSheet, TouchableOpacity } from 'react-native';
import { useRouter } from 'expo-router';
import { useEffect, useState } from 'react';

import { dummyPosts } from '@/__mocks__/qna';
import FilterBar, { SortType, SortOption } from '@/components/FilterBar';
import { LoadingView, EmptyView } from '@/components/CommunityStateView';
import { STYLE } from '@/constants/styles';
import COLOR from '@/constants/colors';
import { useCommunitySearch } from '@/contexts/CommunitySearchContext';
import { QnaPostResponse } from '@/types/community/qna';
import { formatRelativeTime } from '@/utils/time';

const SORT_OPTIONS: SortOption[] = [
  { value: 'latest', label: '최신순' },
  { value: 'registered', label: '등록순' },
  { value: 'comments', label: '답변순' }
];

export default function QnaListScreen() {
  const router = useRouter();
  const { searchTrigger } = useCommunitySearch(); // 실제 사용할 검색어 로드
  const [loading, setLoading] = useState(true);
  const [posts, setPosts] = useState<QnaPostResponse[]>([]);
  const [sortType, setSortType] = useState<SortType>('latest');
  const [isChecked, setIsChecked] = useState(false);  // 체크 여부

  const fetchPosts = async () => {
    setLoading(true);
    
    // 더미 데이터 호출
    setTimeout(() => {
      const filtered = dummyPosts.filter(post => post.title.includes(searchTrigger));
      setPosts(filtered);
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
      <FilterBar
        sort={{ options: SORT_OPTIONS, value: sortType, onChange: setSortType }}
        check={{ label: '미채택 글만 보기', value: isChecked, onChange: setIsChecked }}
      />

      <FlatList
        data={posts}
        keyExtractor={(item) => item.id.toString()}
        contentContainerStyle={{ paddingBottom: 16 }}
        renderItem={({ item }) => (
          <TouchableOpacity 
            activeOpacity={0.7} // 클릭 시 투명도 설정
            onPress={() => router.push(`../../community/qna/${item.id}`)} // 상세 화면 이동
          >
            <View style={[STYLE.CARD, STYLE.ROW]}>
              <View style={styles.info}>
                {/* 제목 */}
                <Text style={styles.title} numberOfLines={1}>{item.title}</Text>

                {/* 작성자 & 시간 */}
                <Text style={styles.authorInfo}>
                  {item.author.nickname} • {formatRelativeTime(item.createdAt)}
                </Text>
              </View>

              {/* 답변수 */}
              <View style={styles.answerBadge}>
                <Text style={styles.answerCount}>{item.answerCount}</Text>
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
  authorInfo: { marginBottom: 2, fontSize: 12, color: COLOR.TEXT.GRAY_MEDIUM },
  answerBadge: {
    justifyContent: 'center',
    alignItems: 'center',
    width: 28, height: 28,
    borderWidth: 1,
    borderColor: COLOR.TINT.GRAY,
    borderRadius: 14
  },
  answerCount: { fontSize: 12, fontWeight: 'bold', color: COLOR.TEXT.NAVY }
});