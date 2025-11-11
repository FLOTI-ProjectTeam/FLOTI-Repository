import { View, Text, StyleSheet, Pressable, Modal } from 'react-native';
import { router } from 'expo-router';
import { useState } from 'react';

import { deleteTipPost } from '@/api/community/tipApi';
import { IconSymbol } from '@/components/ui/IconSymbol';
import DeleteConfirmModal from '@/components/ui/DeleteConfirmModal';
import MorePopup from '@/components/ui/MorePopup';
import { showToast } from '@/utils/toast';
import { TipPostResponse } from '@/types/community/tip';
import COLOR from '@/constants/colors';

export default function BottomBar({ 
  post, onToggleLike
}: {
  post: TipPostResponse;
  onToggleLike: () => void;
}) {
  const [menuVisible, setMenuVisible] = useState(false);
  const [confirmVisible, setConfirmVisible] = useState(false);

  /* API 호출 */
  const callDeleteTipPost = () => deleteTipPost(post.id);

  /* 이벤트 핸들러 */
  const handleGoToTipUpdate = () => {
    router.push({
      pathname: '/community/tip/update/[postId]',
      params: { 
        postId: post.id,
        title: post.title, 
        content: post.content
      }
    });
    setMenuVisible(false);
  };

  const handleGoToCommentList = () => {
    router.push({
      pathname: '/community/tip/[postId]/comment',
      params: { 
        postId: post.id,
        commentCount: post.commentCount
      }
    });
  }
  
  const handleShowDeleteConfirm = () => {
    setMenuVisible(false);
    setConfirmVisible(true);
  };

  const handleDelete = async () => {
    try {
      setConfirmVisible(false);
      await callDeleteTipPost();
      router.back();
    } catch (error) {
      showToast('삭제 중 오류가 발생했습니다.', 'error');
    }
  };

  return (
    <View style={styles.bottomBar}>
      {/* 좋아요수 & 댓글수 */}
      <View style={styles.leftActions}>
        <Pressable style={styles.actionButton} onPress={onToggleLike}>
          <IconSymbol 
            name={post.liked ? "heart.fill" : "heart"} // 좋아요 여부에 따라 아이콘 변경
            color={post.liked ? 'tomato' : COLOR.TINT.GRAY_DARK} // 좋아요 여부에 따라 색상 변경
          />
          <Text style={styles.bottomText}>{post.likeCount}</Text>
        </Pressable>

        <Pressable style={styles.actionButton} onPress={handleGoToCommentList}>
          <IconSymbol name="comment" color={COLOR.TINT.GRAY_DARK} />
          <Text style={styles.bottomText}>{post.commentCount}</Text>
        </Pressable>
      </View>

      {/* 더보기 버튼 */}
      <View>
        <Pressable onPress={() => setMenuVisible(!menuVisible)}>
          <IconSymbol name="more.horizontal" color={COLOR.TINT.GRAY_DARK} />
        </Pressable>

        {/* 더보기 팝업 */}
        {menuVisible && (
          <Modal transparent visible={menuVisible} animationType='fade' onRequestClose={() => setMenuVisible(false)}>
            <Pressable style={styles.overlay} onPress={() => setMenuVisible(false)} />
            <MorePopup
              actions={[
                { label: '수정', onPress: handleGoToTipUpdate },
                { label: '삭제', onPress: handleShowDeleteConfirm },
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
        onDelete={handleDelete}
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