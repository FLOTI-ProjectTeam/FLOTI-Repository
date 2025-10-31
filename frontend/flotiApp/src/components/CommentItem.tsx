import { formatRelativeOrDate } from '@/utils/time';
import { Dispatch, SetStateAction, useRef, useState } from 'react';
import { View, Text, Pressable, Modal, StyleSheet, FlatList, TextInput, Button, TouchableOpacity } from 'react-native';

import { IconSymbol } from '@/components/ui/IconSymbol';
import MorePopup from '@/components/MorePopup';
import { CommentResponse } from '@/types/community/tip';
import COLOR from '@/constants/colors';

export default function CommentItem({
  comment, menuVisibleId, setMenuVisibleId, handleUpdate, handleDeleteConfirm, handleReply, isReply = false
}: {
  comment: CommentResponse;
  menuVisibleId: number | null;
  setMenuVisibleId: Dispatch<SetStateAction<number | null>>;
  handleUpdate: (comment: CommentResponse) => void;
  handleDeleteConfirm: (comment: CommentResponse) => void;
  handleReply?: (comment: CommentResponse) => void;
  isReply?: boolean;
}) {
  const buttonRef = useRef<View>(null); // 컴포넌트의 레퍼런스를 저장하는 훅
  const [menuPosition, setMenuPosition] = useState({ x: 0, y: 0 });
  const [editing, setEditing] = useState(false);
  const [editText, setEditText] = useState(comment.content);

  const openMenu = () => {
    if (buttonRef.current) {
      buttonRef.current.measure((x, y, width, height, pageX, pageY) => {
        setMenuPosition({ x: pageX, y: pageY });  // 컴포넌트의 레퍼런스에 따라 메뉴 위치 변경
        setMenuVisibleId(comment.id);
      });
    }
  };

  const saveEdit = () => {
    handleUpdate({ ...comment, content: editText });
    setEditing(false);
    setMenuVisibleId(null);
  };

  const cancelEdit = () => {
    setEditText(comment.content);
    setEditing(false);
    setMenuVisibleId(null);
  };

  return (
    <View style={isReply ? styles.replyItem : styles.commentItem}>
      <View style={styles.itemContent}>
        {/* 작성자 정보, 더보기 버튼 */}
        <View style={styles.itemRowHeader}>
          <View style={styles.authorInfo}>
            <Text style={styles.author}>{comment.author?.nickname ?? '탈퇴한 사용자'}</Text>
            {!comment.deleted && 
              <Text style={styles.time}>{formatRelativeOrDate(comment.createdAt as string)}</Text>
            }
          </View>
          {!comment.deleted && (
            <Pressable ref={buttonRef} onPress={openMenu}>
              <IconSymbol name="more.horizontal" size={20} color={COLOR.ICON.GRAY_DARK} />
            </Pressable>
          )}
        </View>

        {/* 내용 / 수정 모드 */}
        {editing ? (
          <View>
            <TextInput
              value={editText ?? ""}
              onChangeText={setEditText}
              style={styles.editInput}
              multiline
            />
            <View style={styles.editRow}>
              <TouchableOpacity activeOpacity={0.5} onPress={cancelEdit}>
                <Text style={styles.cancelButtonText}>취소</Text>
              </TouchableOpacity>
              <TouchableOpacity activeOpacity={0.8} onPress={saveEdit} style={styles.submitButton}>
                <Text style={styles.submitButtonText}>수정</Text>
              </TouchableOpacity>
            </View>
          </View>
        ) : (
          <Text style={styles.content}>{comment.deleted ? '삭제된 댓글입니다.' : comment.content}</Text>
        )}
        
        {/* 답글 버튼 */}
        {!comment.deleted && handleReply && !editing && (
          <Pressable onPress={() => handleReply(comment)}>
            <Text style={styles.replyButton}>💬 답글 달기</Text>
          </Pressable>
        )}

        {/* 답글 목록 */}
        {comment.replies?.length > 0 && (
          <FlatList
            data={comment.replies}
            keyExtractor={(reply) => reply.id.toString()}
            renderItem={({ item: reply }) => (
              <CommentItem
                comment={reply}
                menuVisibleId={menuVisibleId}
                setMenuVisibleId={setMenuVisibleId}
                handleUpdate={handleUpdate}
                handleDeleteConfirm={handleDeleteConfirm}
                isReply={true}
              />
            )}
          />
        )}
        
        {menuVisibleId === comment.id && !editing && (
          <Modal transparent visible animationType='fade' onRequestClose={() => setMenuVisibleId(null)}>
            <Pressable style={styles.overlay} onPress={() => setMenuVisibleId(null)} />

            {/* 더보기 팝업 */}
            <MorePopup
              actions={[
                { label: '수정', onPress: () => setEditing(true) },
                { label: '삭제', onPress: () => handleDeleteConfirm(comment) },
              ]}
              popupStyle={{
                flexDirection: 'row', justifyContent: 'center', width: 110, gap: 18,
                top: menuPosition.y - 32, left: menuPosition.x - 116  // 더보기 버튼의 위치
              }}
            />
          </Modal>
        )}
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  commentItem: {
    paddingVertical: 8,
    marginHorizontal: 24,
    borderBottomWidth: 1,
    borderBottomColor: COLOR.TINT.GRAY_LIGHT
  },
  replyItem: { paddingTop: 12, paddingLeft: 18 },
  itemContent: { flex: 1 },
  itemRowHeader: { 
    flexDirection: 'row', 
    justifyContent: 'space-between', 
    alignItems: 'center' 
  },
  authorInfo: { 
    flexDirection: 'row', 
    alignItems: 'flex-end', 
    marginBottom: 4, 
    gap: 6
  },
  author: { fontSize: 14, color: COLOR.TEXT.GRAY_DARK, fontWeight: 'bold' },
  time: { fontSize: 12, color: COLOR.TEXT.GRAY_MEDIUM },
  content: { fontSize: 15 },
  replyButton: { 
    fontSize: 12, 
    color: COLOR.TEXT.GRAY_MEDIUM, 
    marginTop: 4 
  },
  overlay: { flex: 1 },
  editInput: {
    borderWidth: 1,
    borderColor: COLOR.TINT.GRAY_LIGHT,
    borderRadius: 8,
    paddingVertical: 6,
    paddingHorizontal: 10,
    fontSize: 14,
    minHeight: 36,
    maxHeight: 120,
    textAlignVertical: 'top'
  },
  editRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginTop: 4,
    marginHorizontal: 5 ,
    gap: 12
  },
  cancelButtonText: { fontSize: 14, color: COLOR.TEXT.GRAY_CHARCOAL },
  submitButton: {
    backgroundColor: COLOR.BUTTON.NAVY,
    borderRadius: 16,
    paddingVertical: 4, paddingHorizontal: 12
  },
  submitButtonText: {
    fontSize: 15,
    color: 'white',
    fontWeight: 600
  }
});