import { View, Text, FlatList, StyleSheet, TouchableOpacity } from 'react-native';
import { useEffect, useState } from 'react';

import { dummyPostDetails } from '@/__mocks__/qna';
import { getQnaPosts } from '@/api/community/qnaApi';
import FilterBar, { SortType, SortOption } from '@/components/feature/community/FilterBar';
import { LoadingView, EmptyView } from '@/components/feature/community/CommunityStateView';
import { useCommunitySearch } from '@/contexts/CommunitySearchContext';
import { useNavigation } from '@/hooks/useNavigation';
import { formatRelativeTime } from '@/utils/time';
import { QnaPostResponse } from '@/types/community/qna';
import { STYLE } from '@/constants/styles';
import COLOR from '@/constants/colors';

const SORT_OPTIONS: SortOption[] = [
  { value: 'latest', label: '최신순' },
  { value: 'registered', label: '등록순' },
  { value: 'comments', label: '답변순' }
];

export default function QnaListScreen() {
  const { navigateTo } = useNavigation();

  const [loading, setLoading] = useState(true);
  const [posts, setPosts] = useState<QnaPostResponse[]>([]);
  
  const { searchTrigger } = useCommunitySearch(); // 실제 사용할 검색어 로드
  const [sortType, setSortType] = useState<SortType>('latest'); // 정렬순
  const [isChecked, setIsChecked] = useState(false);  // 체크 여부

  /* API 호출 */
  const fetchPosts = async () => {
    try {
      setLoading(true);
      const response = await getQnaPosts(searchTrigger, sortType, 0, isChecked);
      setPosts(response.data.content);
    } catch (error) {
      // 테스트용
      const filtered = dummyPostDetails.filter(post => post.title.includes(searchTrigger));
      setPosts(filtered);
    } finally {
      setLoading(false);
    }
  };

  // 검색하거나 정렬순 또는 체크 상태 변경 시 실행
  useEffect(() => {
    fetchPosts();
  }, [searchTrigger, sortType, isChecked]);

  /* 이벤트 핸들러 */
  const handleGoToQnaDetail = (postId: number) => navigateTo(`/community/qna/${postId}`);

  if (loading) return <LoadingView />
  if (posts.length === 0) return <EmptyView />

  return (
    <View style={STYLE.CONTENT_CONTAINER}>
      <FilterBar
        sort={{ options: SORT_OPTIONS, value: sortType, onChange: setSortType }}
        check={{ label: '미채택 질문', value: isChecked, onChange: setIsChecked }}
      />

      <FlatList
        data={posts}
        keyExtractor={(item) => item.id.toString()}
        style={STYLE.WRAPPER}
        contentContainerStyle={{ paddingBottom: 8 }}
        renderItem={({ item }) => (
          <TouchableOpacity 
            activeOpacity={0.7} // 클릭 시 투명도 설정
            onPress={() => handleGoToQnaDetail(item.id)}
          >
            <View style={[STYLE.CARD, STYLE.ROW]}>
              <View style={styles.info}>
                {/* 제목 */}
                <Text style={styles.title} numberOfLines={1}>{item.title}</Text>

                {/* 작성자, 작성일 */}
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
  title: { fontSize: 16, fontWeight: 700, color: 'black' },
  authorInfo: { marginBottom: 2, fontSize: 12, color: COLOR.TEXT.GRAY_MEDIUM },
  answerBadge: {
    justifyContent: 'center',
    alignItems: 'center',
    width: 28, height: 28,
    borderWidth: 1,
    borderColor: COLOR.TINT.GRAY,
    borderRadius: 14
  },
  answerCount: { fontSize: 12, fontWeight: 700, color: COLOR.TEXT.NAVY }
});