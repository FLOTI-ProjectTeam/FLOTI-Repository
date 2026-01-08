import { Dispatch, SetStateAction, useContext, useRef, useState } from 'react';
import { View, Text, Pressable, Modal, StyleSheet, FlatList, TextInput, TouchableOpacity } from 'react-native';

import { UserContext } from '@/contexts/UserContext';

import { CommentResponse } from '@/types/community/tip';

import MorePopup from '@/components/MorePopup';
import { IconSymbol } from '@/components/ui/IconSymbol';
import BreakAllText from '@/components/ui/BreakAllText';

import { formatSmartTime } from '@/utils/time';
import { showToast } from '@/utils/toast';
import COLOR from '@/constants/colors';
import { STYLE } from '@/constants/styles';

export default function CommentItem({
  comment, menuId, onChangeMenuId, onUpdate, onDeleteConfirm, onReply
}: {
  comment: CommentResponse;
  menuId: number | null;
  onChangeMenuId: Dispatch<SetStateAction<number | null>>;
  onUpdate: (comment: CommentResponse) => void;
  onDeleteConfirm: (comment: CommentResponse) => void;
  onReply?: (comment: CommentResponse) => void;
}) {
  const [menuPosition, setMenuPosition] = useState({ x: 0, y: 0 });
  const [editing, setEditing] = useState(false);  // 수정 모드 여부
  const [editText, setEditText] = useState(comment.content);  // 수정 중인 댓글 내용

  const buttonRef = useRef<View>(null); // 컴포넌트의 레퍼런스 저장
  const userContext = useContext(UserContext);  // 사용자 상태
  const isAuthor = (comment.author?.username === userContext?.username);

  /* 이벤트 핸들러 */
  const handleOpenMenu = () => {
    if (buttonRef.current) {
      buttonRef.current.measure((x, y, width, height, pageX, pageY) => {
        setMenuPosition({ x: pageX - 90, y: pageY });  // 컴포넌트의 레퍼런스에 따라 메뉴 위치 변경
        onChangeMenuId(comment.id);
      });
    }
  };

  const handleSubmit = async () => {
    try {
      onChangeMenuId(null);
      await onUpdate({ ...comment, content: editText });
      setEditing(false);
    } catch (error) {
      showToast('수정 실패', 'error');
    }
  };

  const handleCancel = () => {
    setEditText(comment.content);
    setEditing(false);
    onChangeMenuId(null);
  };

  return (
    <View style={[
      onReply ? styles.commentItem : styles.replyItem,  // 답글 가능 여부에 따라 스타일 변경
      STYLE.FLEX
    ]}>
      {/* 작성자 정보, 더보기 버튼 */}
      <View style={styles.itemRowHeader}>
        <View style={styles.authorInfo}>
          <Text style={styles.author}>{comment.author?.nickname ?? '탈퇴한 사용자'}</Text>
          {comment.createdAt && <Text style={styles.time}>{formatSmartTime(comment.createdAt)}</Text>}
        </View>
        {isAuthor && (
          <Pressable ref={buttonRef} onPress={handleOpenMenu}>
            <IconSymbol name="more.horizontal" size={20} color={COLOR.TINT.GRAY_DARK} />
          </Pressable>
        )}
      </View>

      {/* 읽기·수정 모드 */}
      {editing ? (
        <View>
          <TextInput
            value={editText ?? ''}
            onChangeText={setEditText}
            style={styles.editInput}
            multiline
          />
          <View style={styles.editRow}>
            <TouchableOpacity activeOpacity={0.5} onPress={handleCancel}>
              <Text style={styles.cancelButtonText}>취소</Text>
            </TouchableOpacity>
            <TouchableOpacity activeOpacity={0.8} onPress={handleSubmit} style={styles.submitButton}>
              <Text style={styles.submitButtonText}>수정</Text>
            </TouchableOpacity>
          </View>
        </View>
      ) : (
        <BreakAllText style={styles.content}>
          {comment.deleted ? '삭제된 댓글입니다.' : comment.content}
        </BreakAllText>
      )}

      {/* 답글 버튼 */}
      {!comment.deleted && onReply && !editing && (
        <Pressable onPress={() => onReply(comment)}>
          <Text style={styles.replyButton}>💬 답글 달기</Text>
        </Pressable>
      )}

      {/* 답글 목록 */}
      {comment.replies?.length > 0 && (
        <FlatList
          data={comment.replies}
          keyExtractor={(reply) => reply.id.toString()}
          renderItem={({ item }) => (
            <CommentItem
              comment={item}
              menuId={menuId}
              onChangeMenuId={onChangeMenuId}
              onUpdate={onUpdate}
              onDeleteConfirm={onDeleteConfirm}
            />
          )}
        />
      )}

      {/* 더보기 팝업 */}
      {menuId === comment.id && !editing && (
        <Modal transparent visible animationType='fade' onRequestClose={() => onChangeMenuId(null)}>
          <Pressable style={STYLE.FLEX} onPress={() => onChangeMenuId(null)} />
          <MorePopup
            actions={[
              { label: '수정', onPress: () => setEditing(true) },
              { label: '삭제', onPress: () => onDeleteConfirm(comment) },
            ]}
            style={{ top: menuPosition.y, left: menuPosition.x }}  // 더보기 팝업 위치
          />
        </Modal>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  commentItem: {
    marginHorizontal: 24,
    paddingVertical: 8,
    borderBottomWidth: 1,
    borderBottomColor: COLOR.TINT.GRAY_LIGHT
  },
  replyItem: { paddingTop: 12, paddingLeft: 18 },
  itemRowHeader: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center' },
  authorInfo: {
    flexDirection: 'row',
    alignItems: 'flex-end',
    marginBottom: 4,
    gap: 6
  },
  author: { fontSize: 14, color: COLOR.TEXT.GRAY_DARK, fontWeight: 600 },
  time: { fontSize: 12, color: COLOR.TEXT.GRAY_MEDIUM },
  content: { fontSize: 15 },
  replyButton: { marginTop: 4, fontSize: 12, color: COLOR.TEXT.GRAY_MEDIUM },
  editInput: {
    borderWidth: 1,
    borderColor: COLOR.TINT.GRAY_LIGHT,
    borderRadius: 8,
    paddingVertical: 6,
    paddingHorizontal: 10,
    fontSize: 14,
    maxHeight: 120
  },
  editRow: {
    marginTop: 4,
    marginHorizontal: 5,
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    gap: 12
  },
  cancelButtonText: { fontSize: 14, color: COLOR.TEXT.GRAY_CHARCOAL },
  submitButton: {
    backgroundColor: COLOR.BUTTON.NAVY,
    borderRadius: 16,
    paddingVertical: 4, paddingHorizontal: 12
  },
  submitButtonText: { fontSize: 15, fontWeight: 700, color: 'white' }
});