import { View, Text, FlatList, ActivityIndicator, Image, StyleSheet, TouchableOpacity, Modal } from 'react-native';
import { useRouter } from "expo-router";
import { useEffect, useState } from "react";

import { dummyPosts } from '@/__mocks__/tip';
import SortDropdown, { SortOption } from '@/components/SortDropdown';
import { IconSymbol } from '@/components/ui/IconSymbol';
import COLORS from '@/constants/colors'
import { useCommunitySearch } from '@/contexts/CommunitySearchContext';
import { TipPostResponse } from '@/types/community/tip';

type SortType = 'latest' | 'registered' | 'likes';

const SORT_OPTIONS: SortOption[] = [
  { value: 'latest', label: '최신순' },
  { value: 'registered', label: '등록순' },
  { value: 'likes', label: '인기순' },
];

export default function TipListScreen() {
  const { search } = useCommunitySearch(); // 검색창에서 입력한 검색어 로드
  const [posts, setPosts] = useState<TipPostResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [sortType, setSortType] = useState<SortType>('latest');
  const [showSortModal, setShowSortModal] = useState(false);
  const router = useRouter();

  const sortPosts = (postsToSort: TipPostResponse[], type: SortType) => {
    const sorted = [...postsToSort];
    
    switch (type) {
      case 'latest':
        // 최신순 (id 내림차순)
        return sorted.sort((a, b) => b.id - a.id);
      case 'registered':
        // 등록순 (id 오름차순)
        return sorted.sort((a, b) => a.id - b.id);
      case 'likes':
        // 인기순 (좋아요 수 내림차순)
        return sorted.sort((a, b) => b.likeCount - a.likeCount);
      default:
        return sorted;
    }
  };

  const fetchPosts = async () => {
    setLoading(true);
    
    // 더미 데이터 호출
    setTimeout(() => {
      const filtered = dummyPosts.filter(post => post.title.includes(search));  // 검색어 필터링
      setPosts(filtered); // 서버에서 정렬 처리
      setLoading(false);
    }, 500);
  };

  // 검색어 또는 정렬순 변경 시 fetchPosts 호출
  useEffect(() => {
    fetchPosts();
  }, [search, sortType]);

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
          <TouchableOpacity onPress={() => router.push(`../../community/tip/${item.id}`)}>
            <View style={styles.card}>
              <View style={styles.info}>
                <Text style={styles.title} numberOfLines={1}>
                  {item.title}
                </Text>
                <Text style={styles.meta}>
                  {item.author.nickname} • {item.id}분 전
                </Text>
                <View style={styles.metaRow}>
                  <View style={styles.metaItem}>
                    <IconSymbol name="comment" size={14} color={COLORS.ICON.GRAY_DARK} />
                    <Text style={styles.metaText}>{item.commentCount}</Text>
                  </View>
                  <View style={[styles.metaItem, { marginLeft: 12 }]}>
                    <IconSymbol name="thumbs" size={14} color={COLORS.ICON.GRAY_DARK} />
                    <Text style={styles.metaText}>{item.likeCount}</Text>
                  </View>
                </View>
              </View>

              <Image source={{ uri: item.thumbnail }} style={styles.thumbnail} />
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
    backgroundColor: COLORS.BACKGROUND.SLATE_LIGHT
  },
  center: {
    justifyContent: 'center',
    alignItems: 'center'
  },
  card: {
    flexDirection: 'row',
    padding: 12,
    marginBottom: 8,
    borderWidth: 0,
    borderRadius: 10,
    backgroundColor: 'white'
  },
  info: {
    flex: 1,
    justifyContent: 'space-between'
  },
  title: {
    fontSize: 16,
    fontWeight: 'bold',
    color: 'black',
    marginBottom: 4
  },
  meta: {
    marginBottom: 2,
    fontSize: 12,
    color: COLORS.TEXT.GRAY_MEDIUM
  },
  metaRow: {
    flexDirection: 'row',
    alignItems: 'center'
  },
  metaItem: {
    flexDirection: 'row',
    alignItems: 'center'
  },
  metaText: {
    color: COLORS.TEXT.GRAY_MEDIUM,
    fontSize: 12,
    marginLeft: 4 // 아이콘과 숫자 사이 간격
  },
  emptyText: {
    fontSize: 14,
    color: COLORS.TEXT.GRAY_MEDIUM
  },
  thumbnail: {
    width: 80,
    height: 80,
    marginRight: 12,
    borderRadius: 4
  }
});