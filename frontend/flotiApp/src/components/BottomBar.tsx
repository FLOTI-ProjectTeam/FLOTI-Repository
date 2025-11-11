import { View, Text, StyleSheet, Pressable, Alert, Modal } from 'react-native';
import { router } from 'expo-router';
import { useState } from 'react';

import { IconSymbol } from '@/components/ui/IconSymbol';
import DeleteConfirmModal from '@/components/ui/DeleteConfirmModal';
import MorePopup from '@/components/MorePopup';
import COLOR from '@/constants/colors';
import { TipPostResponse } from '@/types/community/tip';

export default function BottomBar({ post }: {
  post: TipPostResponse;
}) {
  const [menuVisible, setMenuVisible] = useState(false);
  const [confirmVisible, setConfirmVisible] = useState(false);

  // 수정 화면으로 이동
  const goToPostUpdateScreen = () => {
    router.push({
      pathname: '/community/tip/update/[postId]',
      params: { 
        postId: String(post.id),
        title: post.title, 
        content: post.content 
      }
    });
    setMenuVisible(false);
  };
  
  // 삭제 확인 모달 표시
  const showDeleteConfirmModal = () => {
    setMenuVisible(false);
    setConfirmVisible(true);
  };

  // 삭제 처리
  const handleDelete = async (id: number) => {
    try {
      setConfirmVisible(false);
      router.back();
    } catch (error) {
      console.error(error);
      Alert.alert('삭제 실패', '잠시 후 다시 시도해주세요.');
    }
  };

  return (
    <View style={styles.bottomBar}>
      {/* 좋아요 & 댓글 정보 */}
      <View style={styles.leftActions}>
        <Pressable style={styles.actionButton}>
          <IconSymbol name="heart" color={COLOR.ICON.GRAY_DARK} />
          <Text style={styles.bottomText}>{post.likeCount}</Text>
        </Pressable>

        <Pressable 
          style={styles.actionButton} 
          onPress={() => router.push(`/community/tip/${post.id}/comment`)}
        >
          <IconSymbol name="comment" color={COLOR.ICON.GRAY_DARK} />
          <Text style={styles.bottomText}>{post.commentCount}</Text>
        </Pressable>
      </View>

      {/* 더보기 버튼 */}
      <View>
        <Pressable onPress={() => setMenuVisible(!menuVisible)}>
          <IconSymbol name="more.horizontal" color={COLOR.ICON.GRAY_DARK} />
        </Pressable>

        {/* 더보기 팝업 */}
        {menuVisible && (
          <Modal transparent visible={menuVisible} animationType='fade' onRequestClose={() => setMenuVisible(false)}>
            <Pressable style={styles.overlay} onPress={() => setMenuVisible(false)} />
            <MorePopup
              actions={[
                { label: '수정', onPress: goToPostUpdateScreen },
                { label: '삭제', onPress: showDeleteConfirmModal },
              ]}
              style={{ bottom: 46, right: 8 }}
            />
          </Modal>
        )}
      </View>

      <DeleteConfirmModal
        visible={confirmVisible}
        title='게시글을 삭제하시겠습니까?'
        onCancel={() => setConfirmVisible(false)}
        onDelete={() => handleDelete(post.id)}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  bottomBar: {
    height: 50,
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingHorizontal: 18,
    backgroundColor: 'white',
    borderTopWidth: 1,
    borderColor: COLOR.TINT.GRAY
  },
  bottomText: { marginLeft: 6, fontSize: 16, color: COLOR.TEXT.GRAY_MEDIUM },
  leftActions: { flexDirection: 'row' },
  actionButton: {
    marginRight: 16,
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center'
  },
  overlay: { flex: 1, justifyContent: 'flex-end', alignItems: 'flex-end' }
});