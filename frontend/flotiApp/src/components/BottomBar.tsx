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
      pathname: '/community/tip/update/[id]',
      params: { 
        id: String(post.id),
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
      {/* 댓글 & 추천 정보 */}
      <View style={styles.leftActions}>
        <Pressable 
          style={styles.actionButton} 
          onPress={() => router.push(`/community/tip/${post.id}/comments`)}
        >
          <IconSymbol name="comment" color={COLOR.ICON.GRAY_DARK} />
          <Text style={styles.bottomText}>{post.commentCount}</Text>
        </Pressable>

        <Pressable style={styles.actionButton}>
          <IconSymbol name="thumbs" color={COLOR.ICON.GRAY_DARK} />
          <Text style={styles.bottomText}>{post.likeCount}</Text>
        </Pressable>
      </View>

      {/* 더보기 버튼 */}
      <View>
        <Pressable onPress={() => setMenuVisible(!menuVisible)}>
          <IconSymbol name="more.horizontal" color={COLOR.ICON.GRAY_DARK} />
        </Pressable>

        {menuVisible && (
          <Modal transparent visible={menuVisible} animationType='fade' onRequestClose={() => setMenuVisible(false)}>
            <Pressable style={styles.overlay} onPress={() => setMenuVisible(false)} />

            {/* 더보기 팝업 */}
            <MorePopup
              actions={[
                { label: '수정하기', onPress: goToPostUpdateScreen },
                { label: '삭제하기', onPress: showDeleteConfirmModal },
              ]}
              popupStyle={{ bottom: 46, right: 8, width: 100, gap: 14 }}
            />
          </Modal>
        )}
      </View>

      {/* 삭제 확인 모달 */}
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
  bottomText: {
    fontSize: 14,
    marginLeft: 8,
    color: COLOR.TEXT.GRAY_MEDIUM
  },
  leftActions: { flexDirection: 'row' },
  actionButton: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    marginRight: 16
  },
  overlay: { 
    flex: 1,
    justifyContent: 'flex-end',
    alignItems: 'flex-end'
  }
});