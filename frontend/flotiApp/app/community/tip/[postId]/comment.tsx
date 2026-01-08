import { useEffect, useState } from 'react';
import { View, Text, FlatList, ActivityIndicator } from 'react-native';
import { useLocalSearchParams } from 'expo-router';

import { useMenuInteraction } from '@/hooks/useMenuInteraction';
import { useModal } from '@/hooks/useModal';

import { dummyComments } from '@/__mocks__/tip';
import { createComment, deleteComment, getComments, updateComment } from '@/api/community/tipApi';
import { CommentResponse } from '@/types/community/tip';

import { Header } from '@/components/ui/Header';
import ConfirmModal from '@/components/ui/ConfirmModal';
import InputBar, { ReplyTo } from '@/components/feature/community/InputBar';
import CommentItem from '@/components/feature/community/tip/CommentItem';

import { showToast } from '@/utils/toast';
import { STYLE } from '@/constants/styles';
import COLOR from '@/constants/colors';

export default function CommentListScreen() {
  const { postId, initialCommentCount } = useLocalSearchParams(); // URL에서 게시글 정보 가져오기
  const { target: targetComment, ...commentTools } = useMenuInteraction<CommentResponse>(); // 선택 댓글 처리
  const { modalVisible, openModal, closeModal } = useModal();

  const [loading, setLoading] = useState(true);
  const [comments, setComments] = useState<CommentResponse[]>([]);
  const [commentCount, setCommentCount] = useState(Number(initialCommentCount || 0));
  const [content, setContent] = useState(''); // 작성 중인 댓글 내용
  const [replyTo, setReplyTo] = useState<ReplyTo | null>(null); // 답글 대상

  /* 사이드 이펙트 */
  useEffect(() => {
    fetchComments();
  }, [postId]);

  /* API 호출 */
  const fetchComments = async () => {
    try {
      const response = await getComments(Number(postId));
      setComments(response.data);
    } catch (error) {
      // 테스트용
      const filtered = dummyComments.filter(comment => comment.postId.toString() === postId);
      setComments(filtered);
    } finally {
      setLoading(false);
    }
  }

  const callCreateComment = () => createComment(Number(postId), { parentId: replyTo?.commentId ?? null, content });

  const callUpdateComment = (comment: CommentResponse) =>
    updateComment(Number(postId), comment.id, { parentId: comment.parentId, content: String(comment.content) });

  const callDeleteComment = (comment: CommentResponse) => deleteComment(Number(postId), comment.id);

  /* 이벤트 핸들러 */
  const handleSubmit = async (replyTo: ReplyTo | null) => {
    try {
      if (!content.trim()) return;
      const response = await callCreateComment();

      // 답글이면 부모 댓글의 replies에 추가
      if (replyTo) {
        setComments(prev =>
          prev.map(comment => {
            if (comment.id === replyTo.commentId) {
              return {
                ...comment,
                replies: [...comment.replies, response.data],
              };
            }
            return comment;
          })
        );
      }
      // 일반 댓글이면 comments에 추가
      else setComments(prev => [...prev, response.data]);

      setCommentCount(prev => prev + 1);  // 댓글수 증가
      setContent('');
      setReplyTo(null); // 답글 모드 종료
    } catch (error) {
      showToast('전송 실패', 'error');
    }
  };

  const handleUpdateSubmit = async (target: CommentResponse) => {
    try {
      commentTools.setOpenMenuId(null);
      await callUpdateComment(target);

      setComments(prev =>
        prev.map(comment => {
          // 부모 댓글 수정
          if (comment.id === target.id)
            return { ...comment, content: target.content };

          // 답글 수정
          if (comment.replies?.length) {
            return {
              ...comment,
              replies: comment.replies.map(reply =>
                reply.id === target.id ? { ...reply, content: target.content } : reply
              )
            };
          }

          return comment;
        })
      );
    } catch (error) {
      throw error;  // 하위 컴포넌트에 에러 전달
    }
  };

  const handleShowDeleteConfirm = (target: CommentResponse) => {
    commentTools.selectTarget(target);
    openModal();
  };

  const handleDelete = async () => {
    try {
      if (!targetComment) return;
      await callDeleteComment(targetComment);

      setComments(prev => {
        // 부모 댓글이면 soft 삭제
        if (!targetComment.parentId) {
          return prev.map(comment =>
            comment.id === targetComment.id ? { ...comment, deleted: true } : comment
          );
        }

        // 답글이면 hard 삭제
        return prev.map(comment =>
          comment.id === targetComment.parentId
            ? { ...comment, replies: comment.replies.filter(reply => reply.id !== targetComment.id) }
            : comment
        );
      });

      setCommentCount(prev => prev - 1);  // 댓글수 감소
    } catch (error) {
      showToast('삭제 실패', 'error');
    } finally {
      closeModal();
      commentTools.setOpenMenuId(null);
    }
  };

  const handleReply = (comment: CommentResponse) => {
    if (comment.author) setReplyTo({ commentId: comment.id, nickname: comment.author.nickname });
    commentTools.setOpenMenuId(null);
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

      <FlatList
        data={comments}
        keyExtractor={(item) => item.id.toString()}
        contentContainerStyle={{ flexGrow: 1 }}
        ListEmptyComponent={
          <View style={[STYLE.CENTER, STYLE.FLEX]}>
            <Text style={STYLE.EMPTY_TEXT}>댓글이 없습니다.</Text>
          </View>
        }
        renderItem={({ item }) => (
          <CommentItem
            comment={item}
            menuId={commentTools.openMenuId}
            onChangeMenuId={commentTools.setOpenMenuId}
            onUpdate={handleUpdateSubmit}
            onDeleteConfirm={handleShowDeleteConfirm}
            onReply={handleReply}
          />
        )}
      />

      <InputBar
        content={content}
        onChangeText={setContent}
        onSubmit={() => handleSubmit(replyTo)}
        placeholder='댓글을 입력하세요'
        replyTo={replyTo}
        onCancelReply={() => setReplyTo(null)}
      />

      <ConfirmModal
        visible={modalVisible}
        title='댓글을 삭제하시겠습니까?'
        onClose={closeModal}
        onAction={handleDelete}
      />
    </View>
  );
}