import { View, Text, FlatList, StyleSheet, TouchableOpacity } from 'react-native';
import { useEffect, useState, useCallback } from 'react';
import { useFocusEffect } from 'expo-router';

import { useCommunitySearch } from '@/contexts/CommunitySearchContext';
import { useNavigation } from '@/hooks/useNavigation';
import { useUser } from '@/contexts/UserContext';
import { getChallengePosts } from '@/api/community/challengeApi';
import { ChallengeSummaryResponse } from '@/types/community/challenge';

import FilterBar, { SortType, SortOption } from '@/components/feature/community/FilterBar';
import { LoadingView, EmptyView } from '@/components/feature/community/CommunityStateView';
import ChallengeItem from '@/components/feature/community/challenge/ChallengeItem';

import { STYLE } from '@/constants/styles';
import COLOR from '@/constants/colors';

const SORT_OPTIONS: SortOption[] = [
  { value: 'latest', label: '최신순' },
  { value: 'popular', label: '인기순' },
  { value: 'imminent', label: '마감임박' }
];

export default function ChallengeListScreen() {
  const { navigateTo } = useNavigation();
  const { searchTrigger } = useCommunitySearch();
  const { user } = useUser();

  const [loading, setLoading] = useState(true);
  const [challenges, setChallenges] = useState<ChallengeSummaryResponse[]>([]);
  const [sortType, setSortType] = useState<SortType>('latest');
  const [showMyChallenges, setShowMyChallenges] = useState(false); // 참여 중인 챌린지 보기 토글

  /* 사이드 이펙트: 화면 포커스 시(뒤로가기 포함) & 필터 변경 시 데이터 갱신 */
  // Fix: useFocusEffect must be inside the component and use useCallback
  useFocusEffect(
    useCallback(() => {
      loadChallenges();
    }, [searchTrigger, sortType, showMyChallenges])
  );

  /* API 호출 */
  const loadChallenges = async () => {
    try {
      setLoading(true);
      // NOTE: 백엔드 API 구조상 mine/all 구분이 되어 있어 분기 처리
      const response = await getChallengePosts(searchTrigger, sortType, 0);
      if (response.data.content.length > 0) {
        setChallenges(response.data.content);
      } else {
        setChallenges([]); // 데이터 없음
      }
    } catch (error) {
      console.error(error);
      setChallenges([]); // 에러 시 빈 리스트
    } finally {
      setLoading(false);
    }
  };

  /* 이벤트 핸들러 */
  const handleGoToDetail = (id: number) => navigateTo(`/community/challenge/${id}`);

  // 상세 페이지나 필터바 옆의 버튼으로 이동하는 로직은 _layout.tsx의 헤더 버튼에서 처리하거나,
  // 여기서는 리스트 내의 버튼으로 처리할 수 있음. 
  // 기획상 헤더의 + 버튼이 생성 페이지로 가고, 여기서는 필터와 태그만 보여줌. (User Request 2번 사진)
  // 다만 4번 사진(목록)에는 + 버튼이 안보이므로 리스트 상단에는 태그만 배치합니다.

  if (loading) return <LoadingView />

  return (
    <View style={STYLE.CONTENT_CONTAINER}>
      {/* 상단 필터 */}
      <View style={styles.actionRow}>
        <FilterBar
          style={{ flex: 1, borderBottomWidth: 0 }}
          sort={{ options: SORT_OPTIONS, value: sortType, onChange: setSortType }}
          check={{ label: '참여 중인 챌린지 보기', value: showMyChallenges, onChange: setShowMyChallenges }}
        />
      </View>

      {/* 태그 리스트 */}


      {challenges.length === 0 ? <EmptyView /> : (
        <FlatList
          data={challenges}
          keyExtractor={(item) => item.id.toString()}
          style={STYLE.WRAPPER}
          contentContainerStyle={{ paddingBottom: 80, paddingHorizontal: 4 }}
          renderItem={({ item }) => (
            <ChallengeItem
              item={item}
              onPress={() => handleGoToDetail(item.id)}
            />
          )}
          ItemSeparatorComponent={() => <View style={{ height: 12 }} />}
        />
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  actionRow: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginBottom: 0
  },
  createButtonContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: 16,
    paddingVertical: 12,
    gap: 12,
    backgroundColor: 'white',
    marginBottom: 8
  },
  tagFilter: { flexDirection: 'row', gap: 8, flex: 1 },
  filterTag: {
    backgroundColor: COLOR.BACKGROUND.SLATE_LIGHT,
    paddingHorizontal: 10,
    paddingVertical: 4,
    borderRadius: 16
  },
  filterTagText: { fontSize: 13, color: COLOR.TEXT.GRAY_CHARCOAL, fontWeight: '500' },
  editText: { color: '#5C9DFF', fontWeight: '600' },
});
