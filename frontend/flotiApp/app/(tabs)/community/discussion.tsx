import { View, Text, FlatList, StyleSheet, TouchableOpacity } from 'react-native';
import { useEffect, useState } from 'react';

import { dummyRoomDetails } from '@/__mocks__/discussion';
import { getDiscussionRooms, toggleJoinDiscussion } from '@/api/community/discussionApi';
import { IconSymbol } from '@/components/ui/IconSymbol';
import BreakAllText from '@/components/ui/BreakAllText';
import ConfirmModal from '@/components/ui/ConfirmModal';
import FilterBar, { SortType, SortOption } from '@/components/feature/community/FilterBar';
import { LoadingView, EmptyView } from '@/components/feature/community/CommunityStateView';
import { useCommunitySearch } from '@/contexts/CommunitySearchContext';
import { useModal } from '@/hooks/useModal';
import { useNavigation } from '@/hooks/useNavigation';
import { formatRelativeTime } from '@/utils/time';
import { showToast } from '@/utils/toast';
import { DiscussionRoomResponse } from '@/types/community/discussion';
import COLOR from '@/constants/colors';
import { STYLE } from '@/constants/styles';

const SORT_OPTIONS: SortOption[] = [
  { value: 'recentActivity', label: '최근활동순' },
  { value: 'latest', label: '최신순' },
  { value: 'registered', label: '등록순' }
];

export default function DiscussionListScreen() {
  const { navigateTo } = useNavigation();

  const [loading, setLoading] = useState(true);
  const [rooms, setRooms] = useState<DiscussionRoomResponse[]>([]);

  const { searchTrigger } = useCommunitySearch(); // 실제 사용할 검색어 로드
  const [sortType, setSortType] = useState<SortType>('recentActivity'); // 정렬순
  const [isChecked, setIsChecked] = useState(false);  // 체크 여부

  const [modalTitle, setModalTitle] = useState('');
  const [targetRoom, setTargetRoom] = useState<DiscussionRoomResponse | null>(null);
  const [cannotJoin, setCannotJoin] = useState(false); // 참여 불가 여부

  const { modalVisible, openModal, closeModal } = useModal();

  /* API 호출 */
  const fetchRooms = async () => {
    try {
      setLoading(true);
      const response = await getDiscussionRooms(searchTrigger, sortType, 0, isChecked);
      setRooms(response.data.content);
    } catch (error) {
      // 테스트용
      const filtered = dummyRoomDetails.filter(room => room.title.includes(searchTrigger));
      setRooms(filtered);
    } finally {
      setLoading(false);
    }
  };

  const callToggleJoinDiscussion = (roomId: number) => toggleJoinDiscussion(roomId);

  // 검색하거나 정렬순 또는 체크 상태 변경 시 실행
  useEffect(() => {
    fetchRooms();
  }, [searchTrigger, sortType, isChecked]);

  /* 이벤트 핸들러 */
  const handleSetModal = (target: DiscussionRoomResponse) => {
    setTargetRoom(target);
  
    if (target.participantCount >= target.maxParticipantCount) {
      setModalTitle('토론방이 가득 찼습니다.');
      setCannotJoin(true);
    } else {
      setModalTitle('토론방에 참여하시겠습니까?');
      setCannotJoin(false);
    }
  
    openModal();
  };
  
  const handleToggleJoin = async () => {
    try {
      if (!targetRoom || cannotJoin) return;
      closeModal();
      // await callToggleJoinDiscussion(targetRoom.id);
      navigateTo(`/community/discussion/${targetRoom.id}`);
    } catch (error) {
      showToast('참여 실패', 'error');
    }
  };

  if (loading) return <LoadingView />
  if (rooms.length === 0) return <EmptyView text='토론방이 없습니다.' />

  return (
    <View style={STYLE.CONTENT_CONTAINER}>
      <FilterBar
        sort={{ options: SORT_OPTIONS, value: sortType, onChange: setSortType }}
        check={{ label: '참여 중인 토론', value: isChecked, onChange: setIsChecked }}
      />

      <FlatList
        data={rooms}
        keyExtractor={(item) => item.id.toString()}
        style={STYLE.WRAPPER}
        contentContainerStyle={{ paddingBottom: 8 }}
        renderItem={({ item }) => (
          <TouchableOpacity
            activeOpacity={0.7} // 클릭 시 투명도 설정
            onPress={() => handleSetModal(item)}
          >
            <View style={STYLE.CARD}>
              <View style={styles.info}>
                {/* 제목 */}
                <Text style={styles.title} numberOfLines={1}>{item.title}</Text>

                {/* 소개 */}
                <BreakAllText style={styles.content}>{item.content}</BreakAllText>
                
                {/* 활동일, 인원 */}
                <View style={styles.meta}>
                  <View style={styles.metaItem}>
                    <IconSymbol name="time" size={14} color={COLOR.TINT.GRAY_DARK} />
                    <Text style={styles.metaText}>{formatRelativeTime(item.recentActivityAt)}</Text>
                  </View>
                  <View style={styles.metaItem}>
                    <IconSymbol name="people" size={14} color={COLOR.TINT.GRAY_DARK} />
                    <Text style={styles.metaText}>{item.participantCount}/{item.maxParticipantCount}명</Text>
                  </View>
                </View>
              </View>
            </View>
          </TouchableOpacity>
        )}
      />

      <ConfirmModal
        visible={modalVisible}
        title={modalTitle}
        onClose={closeModal}
        onAction={cannotJoin ? undefined : handleToggleJoin}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  info: { flex: 1, justifyContent: 'space-between', gap: 4 },
  title: { fontSize: 16, fontWeight: 700, color: 'black' },
  content: { marginBottom: 2, fontSize: 12, color: COLOR.TEXT.GRAY_MEDIUM },
  meta: { flexDirection: 'row', alignItems: 'center', gap: 12 },
  metaItem: { flexDirection: 'row', alignItems: 'center' },
  metaText: { marginLeft: 4, fontSize: 12, color: COLOR.TEXT.GRAY_MEDIUM }
});