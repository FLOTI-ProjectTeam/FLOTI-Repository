import { View, FlatList, Text, StyleSheet, Pressable } from 'react-native';
import { useLocalSearchParams } from 'expo-router';
import { useContext, useEffect, useState } from 'react';

import { UserContext } from '@/contexts/UserContext';
import { useMenuInteraction } from '@/hooks/useMenuInteraction';
import { useModal } from '@/hooks/useModal';
import { useNavigation } from '@/hooks/useNavigation';

import { dummyRoomDetails } from '@/__mocks__/discussion';
import { deleteDiscussionRoom, getDiscussionRoom, toggleJoinDiscussion } from '@/api/community/discussionApi';
import { createMessage, deleteMessage, toggleLikeMessage } from '@/api/community/discussionSocket';
import { DiscussionRoomResponse, MessageResponse } from '@/types/community/discussion';
import { UserResponse } from '@/types/community/common';

import { IconSymbol } from '@/components/ui/IconSymbol';
import ConfirmModal from '@/components/ui/ConfirmModal';
import { styles as headerStyles } from '@/components/ui/Header';
import InputBar from '@/components/feature/community/InputBar';
import { LoadingView } from '@/components/feature/community/CommunityStateView';
import MessageItem from '@/components/feature/community/discussion/MessageItem';
import { SideMenu } from '@/components/feature/community/discussion/SideMenu';

import { showToast } from '@/utils/toast';
import { insertDateLabels } from '@/utils/time';
import { STYLE } from '@/constants/styles';
import COLOR from '@/constants/colors';

export default function DiscussionDetailScreen() {
  const { goBackSafely, navigateWithParams } = useNavigation();
  const { roomId } = useLocalSearchParams();  // URL에서 토론방 ID 가져오기
  const { target: targetMessageId, ...messageTools } = useMenuInteraction(); // 선택 메시지 처리
  const { modalVisible, type, openModal, closeModal } = useModal<'roomDelete' | 'roomLeave' | 'messageDelete' | 'likeError'>();

  const [loading, setLoading] = useState(true);
  const [room, setRoom] = useState<DiscussionRoomResponse>();
  const [participants, setParticipants] = useState<UserResponse[]>([]);
  const [messages, setMessages] = useState<MessageResponse[]>([]);
  const [menuVisible, setMenuVisible] = useState(false);
  const [content, setContent] = useState(''); // 작성 중인 메시지 내용

  const userContext = useContext(UserContext);  // 사용자 상태

  /* 사이드 이펙트 */
  useEffect(() => {
    loadRoom();
  }, [roomId]);

  /* API 호출 */
  const loadRoom = async () => {
    try {
      const response = await getDiscussionRoom(Number(roomId));
      setRoom(response.data);
      setParticipants(response.data.participants);
      setMessages(response.data.messages);
    } catch (error) {
      // 테스트용
      const filtered = dummyRoomDetails.find((room) => room.id.toString() === roomId);
      setRoom(filtered);
      setParticipants(filtered!.participants);
      setMessages(filtered!.messages);
    } finally {
      setLoading(false);
    }
  }

  const callDeleteDiscussionRoom = () => deleteDiscussionRoom(room!.id);
  const callToggleJoinDiscussion = () => toggleJoinDiscussion(room!.id);

  if (!room) return;

  /* 이벤트 핸들러 */
  const handleGoToUpdate = () => {
    navigateWithParams('/community/discussion/update/[roomId]', {
      roomId: room.id,
      initialTitle: room.title,
      initialContent: room.content,
      initialMaxParticipantCount: room.maxParticipantCount,
      initialParticipantCount: room.participantCount
    });
  };

  const handleSubmit = () => {
    try {
      if (!content.trim()) return;

      // 테스트용
      const tempMessage: MessageResponse = {
        id: Date.now(),
        roomId: room.id,
        author: { username: 'alice', nickname: '앨리스', profileImage: null },
        content,
        likeCount: 0,
        liked: false,
        createdAt: new Date().toISOString(),
      };

      createMessage(room.id, { content }); // 서버로 메시지 전송
      setMessages(prev => [...prev, tempMessage]);  // 메시지 추가
      setContent('');
    } catch (error) {
      showToast('전송 실패', 'error');
    }
  };

  const handleShowMessageDeleteConfirm = (targetId: number) => {
    messageTools.selectTarget(targetId);
    openModal('messageDelete');
  };

  const handleDelete = async () => {
    try {
      closeModal();
      await callDeleteDiscussionRoom();
      goBackSafely();
    } catch (error) {
      showToast('삭제 실패', 'error');
    }
  };

  const handleLeave = async () => {
    try {
      closeModal();
      await callToggleJoinDiscussion();
      goBackSafely();
    } catch (error) {
      showToast('나가기 실패', 'error');
    }
  };

  const handleMessageDelete = async () => {
    try {
      if (!targetMessageId) return;
      deleteMessage(room.id, targetMessageId); // 서버로 메시지 삭제 요청
      setMessages(prev => prev.filter(message => message.id !== targetMessageId)); // 메시지 삭제
    } catch (error) {
      showToast('삭제 실패', 'error');
    } finally {
      closeModal();
      messageTools.clearTarget();
    }
  }

  const handleToggleLike = async ({ id, liked, author }: MessageResponse) => {
    if (author?.username === userContext?.username) {
      openModal('likeError');
      return;
    }

    try {
      toggleLikeMessage(room.id, id);

      // 좋아요 갱신
      setMessages(prev =>
        prev.map(answer => {
          if (answer.id === id) {
            return {
              ...answer,
              liked: !answer.liked,
              likeCount: answer.likeCount + (answer.liked ? -1 : 1)
            };
          }

          return answer;
        })
      );
    } catch (error) {
      if (liked) showToast('좋아요 취소 실패', 'error');
      else showToast('좋아요 실패', 'error');
    }
  }

  /* 모달 정보 */
  const modalTitle = {
    roomDelete: '토론방을 삭제하시겠습니까?',
    roomLeave: '토론방을 나가시겠습니까?',
    messageDelete: '메시지를 삭제하시겠습니까?',
    likeError: '내 메시지는 좋아요 할 수 없습니다.'
  };

  const modalAction = {
    roomDelete: handleDelete,
    roomLeave: handleLeave,
    messageDelete: handleMessageDelete,
    likeError: undefined
  };

  if (loading) return <LoadingView />;

  return (
    <View style={STYLE.CONTENT_CONTAINER}>
      {/* 헤더 */}
      <View style={headerStyles.headerContainer}>
        <Pressable onPress={() => goBackSafely()} style={headerStyles.iconWrapper}>
          <IconSymbol name="chevron.left" size={32} color={COLOR.TINT.GRAY_DARK} />
        </Pressable>
        <View style={headerStyles.titleContainer}>
          <Text style={headerStyles.title}>{room.title}</Text>
        </View>
        <Pressable onPress={() => setMenuVisible(true)} style={headerStyles.iconWrapper}>
          <IconSymbol name="menu" size={28} color={COLOR.TINT.GRAY_DARK} />
        </Pressable>
      </View>

      <FlatList
        data={insertDateLabels(messages)}
        keyExtractor={(item) => item.id.toString()}
        style={STYLE.WRAPPER}
        contentContainerStyle={{ flexGrow: 1 }}
        renderItem={({ item }) => {
          // 날짜 라벨 렌더링
          if (item.type === 'label') {
            return (
              <View style={styles.dateLabelContainer}>
                <View style={styles.line} />
                <Text style={styles.dateLabelText}>{item.dateLabel}</Text>
                <View style={styles.line} />
              </View>
            );
          }

          // 메시지 렌더링
          return (
            <MessageItem
              message={item.message!}
              menuId={messageTools.openMenuId}
              onChangeMenuId={messageTools.setOpenMenuId}
              onDeleteConfirm={handleShowMessageDeleteConfirm}
              onToggleLike={handleToggleLike}
            />
          );
        }}
      />

      <InputBar
        content={content}
        onChangeText={setContent}
        onSubmit={() => handleSubmit()}
        placeholder='메시지를 입력하세요'
      />

      <ConfirmModal
        visible={modalVisible}
        title={modalTitle[type!]}
        onClose={closeModal}
        onAction={modalAction[type!]}
      />

      <SideMenu
        visible={menuVisible}
        room={room}
        participants={participants}
        onClose={() => setMenuVisible(false)}
        onUpdate={handleGoToUpdate}
        onDelete={() => openModal('roomDelete')}
        onLeave={() => openModal('roomLeave')}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  dateLabelContainer: { flexDirection: 'row', alignItems: 'center', marginBottom: 16 },
  line: { flex: 1, height: 1, backgroundColor: COLOR.TINT.GRAY },
  dateLabelText: {
    marginHorizontal: 10,
    fontSize: 12,
    fontWeight: 500,
    color: COLOR.TEXT.GRAY_CHARCOAL
  }
})