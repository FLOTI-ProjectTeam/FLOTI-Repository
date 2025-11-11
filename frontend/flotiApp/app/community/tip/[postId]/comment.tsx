import { useEffect, useState } from 'react';
import { View, Text, FlatList, ActivityIndicator } from 'react-native';
import { useLocalSearchParams } from 'expo-router';

import { dummyPosts, dummyComments } from '@/__mocks__/tip';
import Header from '@/components/ui/Header';
import DeleteConfirmModal from '@/components/ui/DeleteConfirmModal';
import InputBar, { ReplyTo } from '@/components/InputBar';
import CommentItem from '@/components/CommentItem';
import { CommentResponse } from '@/types/community/tip';
import { STYLE } from '@/constants/styles';
import COLOR from '@/constants/colors';

export default function CommentListScreen() {
  const { postId } = useLocalSearchParams(); // URL에서 글 ID 가져오기
  const [loading, setLoading] = useState(true);
  const [comments, setComments] = useState<CommentResponse[]>([]);
  const post = dummyPosts.find(p => p.id.toString() === postId);
  const [commentCount, setCommentCount] = useState(post ? post.commentCount : 0);
  const [menuVisibleId, setMenuVisibleId] = useState<number | null>(null); // 열린 메뉴 댓글 ID
  const [newComment, setNewComment] = useState(''); // 작성중인 댓글 내용
  const [replyTo, setReplyTo] = useState<ReplyTo | null>(null); // 답글 대상
  const [confirmVisible, setConfirmVisible] = useState(false);
  const [targetComment, setTargetComment] = useState<CommentResponse | null>(null); // 삭제할 댓글

  useEffect(() => {
    // 더미 데이터 호출
    const filtered = dummyComments.filter(c => c.postId.toString() === postId);
    setComments(filtered);
    setLoading(false);
  }, [postId]);

  const handleSend = (replyTo?: ReplyTo | null) => {
    if (!newComment.trim()) return;
  
    const newItem: CommentResponse = {
      id: Date.now(),
      postId: Number(postId),
      parentId: replyTo?.commentId || null, // 답글이면 parentId 기록
      author: {
        id: 1,
        nickname: "Alice",
        profileImage: "https://i.pravatar.cc/150?img=1",
      },
      content: newComment,
      deleted: false,
      replies: [],
      createdAt: new Date().toISOString(),
    };
  
    if (replyTo) {
      // 답글이면 해당 댓글 찾아서 replies 뒤쪽에 추가
      setComments(prev =>
        prev.map(comment => {
          if (comment.id === replyTo.commentId) {
            return {
              ...comment,
              replies: [...comment.replies, newItem],
            };
          }
          return comment;
        })
      );
    } else {
      // 일반 댓글이면 comments 뒤쪽에 추가
      setComments(prev => [...prev, newItem]);
    }

    setCommentCount(prev => prev + 1);  // 댓글 수 증가    
    setNewComment('');
    setReplyTo(null); // 답글 모드 종료
  };

  const handleUpdate = (updatedComment: CommentResponse) => {
    setComments(prev =>
      prev.map(comment => {
        // 부모 댓글 수정
        if (comment.id === updatedComment.id)
          return { ...comment, content: updatedComment.content };
  
        // 답글 수정
        if (comment.replies?.length) {
          return {
            ...comment,
            replies: comment.replies.map(reply =>
              reply.id === updatedComment.id
                ? { ...reply, content: updatedComment.content }
                : reply
            ),
          };
        }
  
        return comment;
      })
    );
  
    setMenuVisibleId(null);
  };

  // 삭제 확인 모달 표시
  const showDeleteConfirmModal = (comment: CommentResponse) => {
    setTargetComment(comment);
    setMenuVisibleId(null);
    setConfirmVisible(true);
  };

  const handleDelete = () => {
    if (!targetComment) return;
  
    const comment = targetComment;
  
    setComments(prev => {
      if (comment.parentId) {
        // 답글만 삭제, 부모 댓글은 그대로
        return prev.map(parent =>
          parent.id === comment.parentId
            ? { ...parent, replies: parent.replies.filter(r => r.id !== comment.id) }
            : parent
        );
      } else {
        // 부모 댓글 삭제
        return prev.map(c =>
          c.id === comment.id ? { ...c, deleted: true } : c
        );
      }
    });
  
    setCommentCount(prev => prev - 1);
    setConfirmVisible(false);
    setTargetComment(null);
  };

  const handleReply = (comment: CommentResponse) => {
    if (comment.author)
      setReplyTo({ commentId: comment.id, nickname: comment.author.nickname });
    setMenuVisibleId(null);
  };

  if (loading) {
    return (
      <View style={STYLE.BASE_CONTAINER}>
        <Header title='댓글' commentCount={commentCount} />
        <View style={[STYLE.BASE_CONTAINER, STYLE.CENTER]}>
          <ActivityIndicator size='large' color={COLOR.TINT.SLATE} />
        </View>
      </View>
    );
  }

  return (
    <View style={STYLE.BASE_CONTAINER}>
      <Header title='댓글' commentCount={commentCount} />

      {comments.length === 0 ? (
        <View style={[STYLE.CENTER, STYLE.FLEX]}>
          <Text style={STYLE.EMPTY_TEXT}>댓글이 없습니다.</Text>
        </View>
      ) : (
        <FlatList
          data={comments}
          keyExtractor={(item) => item.id.toString()}
          renderItem={({ item }) => (
            <CommentItem
              comment={item}
              menuVisibleId={menuVisibleId}
              setMenuVisibleId={setMenuVisibleId}
              handleUpdate={handleUpdate}
              handleDeleteConfirm={showDeleteConfirmModal}
              handleReply={handleReply}
            />
          )}
        />
      )}

      <InputBar
        value={newComment}
        onChangeText={setNewComment}
        onSend={() => {
          handleSend(replyTo);
          setReplyTo(null);
        }}
        placeholder = '댓글을 입력하세요'
        replyTo={replyTo}
        onCancelReply={() => setReplyTo(null)}
      />

      <DeleteConfirmModal
        visible={confirmVisible}
        title='댓글을 삭제하시겠습니까?'
        onCancel={() => setConfirmVisible(false)}
        onDeleteComment={handleDelete}
      />
    </View>
  );
}