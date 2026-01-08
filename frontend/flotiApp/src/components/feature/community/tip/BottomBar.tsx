import { View, Text, StyleSheet, Pressable, Modal } from 'react-native';
import { useContext, useState } from 'react';

import { useModal } from '@/hooks/useModal';
import { useNavigation } from '@/hooks/useNavigation';
import { UserContext } from '@/contexts/UserContext';

import { deleteTipPost } from '@/api/community/tipApi';
import { TipPostResponse } from '@/types/community/tip';

import MorePopup from '@/components/MorePopup';
import ConfirmModal from '@/components/ui/ConfirmModal';
import { IconSymbol } from '@/components/ui/IconSymbol';

import { showToast } from '@/utils/toast';
import COLOR from '@/constants/colors';
import { STYLE } from '@/constants/styles';

export default function BottomBar({
  post, onToggleLike
}: {
  post: TipPostResponse;
  onToggleLike: () => void;
}) {
  const { goBackSafely, navigateWithParams } = useNavigation();
  const { modalVisible, openModal, closeModal } = useModal();

  const [menuVisible, setMenuVisible] = useState(false);

  const userContext = useContext(UserContext);  // 사용자 상태
  const isAuthor = (post.author.username === userContext?.username);

  /* API 호출 */
  const callDeleteTipPost = () => deleteTipPost(post.id);

  /* 이벤트 핸들러 */
  const handleGoToTipUpdate = () => {
    navigateWithParams('/community/tip/update/[postId]', {
      postId: post.id,
      initialTitle: post.title,
      initialContent: post.content,
      thumbnail: post.thumbnail || ''
    });
    setMenuVisible(false);
  };

  const handleGoToCommentList = () => {
    navigateWithParams('/community/tip/[postId]/comment', {
      postId: post.id,
      initialCommentCount: post.commentCount
    });
  }

  const handleShowDeleteConfirm = () => {
    setMenuVisible(false);
    openModal();
  };

  const handleDelete = async () => {
    try {
      closeModal();
      await callDeleteTipPost();
      goBackSafely();
    } catch (error) {
      showToast('삭제 실패', 'error');
    }
  };

  return (
    <View style={styles.bottomBar}>
      {/* 좋아요·댓글수 */}
      <View style={styles.leftActions}>
        <Pressable style={styles.actionButton} onPress={onToggleLike}>
          <IconSymbol
            name={post.liked ? "heart.fill" : "heart"} // 좋아요 여부에 따라 아이콘 변경
            color={post.liked ? 'tomato' : COLOR.TINT.GRAY_DARK} // 좋아요 여부에 따라 색상 변경
          />
          <Text style={styles.actionButtonText}>{post.likeCount}</Text>
        </Pressable>

        <Pressable style={styles.actionButton} onPress={handleGoToCommentList}>
          <IconSymbol name="comment" color={COLOR.TINT.GRAY_DARK} />
          <Text style={styles.actionButtonText}>{post.commentCount}</Text>
        </Pressable>
      </View>

      {/* 더보기 버튼 */}
      <View>
        {isAuthor && (
          <Pressable onPress={() => setMenuVisible(!menuVisible)}>
            <IconSymbol name="more.horizontal" color={COLOR.TINT.GRAY_DARK} />
          </Pressable>
        )}

        {/* 더보기 팝업 */}
        {menuVisible && (
          <Modal transparent visible={menuVisible} animationType='fade' onRequestClose={() => setMenuVisible(false)}>
            <Pressable style={STYLE.FLEX} onPress={() => setMenuVisible(false)} />
            <MorePopup
              actions={[
                { label: '수정', onPress: handleGoToTipUpdate },
                { label: '삭제', onPress: handleShowDeleteConfirm },
              ]}
              style={{ bottom: 46, right: 16 }}
            />
          </Modal>
        )}
      </View>

      <ConfirmModal
        visible={modalVisible}
        title='게시글을 삭제하시겠습니까?'
        onClose={closeModal}
        onAction={handleDelete}
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
    borderTopColor: COLOR.TINT.GRAY
  },
  leftActions: { flexDirection: 'row' },
  actionButton: {
    marginRight: 16,
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center'
  },
  actionButtonText: { marginLeft: 6, fontSize: 16, color: COLOR.TEXT.GRAY_MEDIUM },
});