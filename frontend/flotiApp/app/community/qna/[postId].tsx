import { View, Text, StyleSheet, FlatList, TouchableOpacity } from 'react-native';
import { useLocalSearchParams } from 'expo-router';
import { useContext, useEffect, useState } from 'react';

import { useMenuInteraction } from '@/hooks/useMenuInteraction';
import { useModal } from '@/hooks/useModal';
import { useNavigation } from '@/hooks/useNavigation';

import { dummyPostDetails } from '@/__mocks__/qna';
import { deleteAnswer, deleteQnaPost, getQnaPost, acceptAnswer, toggleLikeAnswer } from '@/api/community/qnaApi';
import { AnswerReponse, QnaPostDetailResponse } from '@/types/community/qna';

import { IconSymbol } from '@/components/ui/IconSymbol';
import ConfirmModal from '@/components/ui/ConfirmModal';
import { Header } from '@/components/ui/Header';
import { LoadingView } from '@/components/feature/community/CommunityStateView';
import AnswerItem from '@/components/feature/community/qna/AnswerItem';
import QnaDetailHeader from '@/components/feature/community/qna/QnaDetailHeader';

import { showToast } from '@/utils/toast';
import COLOR from '@/constants/colors';
import { STYLE } from '@/constants/styles';
import { UserContext } from '@/contexts/UserContext';

export default function QnaDetailScreen() {
  const { postId } = useLocalSearchParams();  // URL에서 게시글 ID 가져오기
  const { goBackSafely, navigateWithParams } = useNavigation();
  const { target: targetAnswerId, ...answerTools } = useMenuInteraction(); // 선택 답변 처리
  const { modalVisible, type, openModal, closeModal } = useModal<'postDelete' | 'answerDelete' | 'answerAccept' | 'likeError'>();

  const [loading, setLoading] = useState(true);
  const [post, setPost] = useState<QnaPostDetailResponse>();
  const [answers, setAnswers] = useState<AnswerReponse[]>([]);

  const userContext = useContext(UserContext);  // 사용자 상태

  /* 사이드 이펙트 */
  useEffect(() => {
    loadPost();
  }, [postId]);

  /* API 호출 */
  const loadPost = async () => {
    try {
      const response = await getQnaPost(Number(postId));
      setPost(response.data);
      setAnswers(response.data.answers);
    } catch (error) {
      // 테스트용
      const filtered = dummyPostDetails.find((post) => post.id.toString() === postId);
      setPost(filtered);
      setAnswers(filtered!.answers);
    } finally {
      setLoading(false);
    }
  }

  const callDeleteQnaPost = () => deleteQnaPost(post!.id);
  const callDeleteAnswer = (answerId: number) => deleteAnswer(post!.id, answerId);
  const callAcceptAnswer = (answerId: number) => acceptAnswer(post!.id, answerId);
  const callToggleLikeAnswer = (answerId: number) => toggleLikeAnswer(post!.id, answerId);

  if (!post) return;

  /* 이벤트 핸들러 */
  const handleGoToUpdate = () => {
    navigateWithParams('/community/qna/update/[postId]', {
      postId: post.id,
      initialTitle: post.title,
      initialContent: post.content
    });
  };

  const handleGoToAnswerCreate = () => {
    navigateWithParams('/community/qna/[postId]/answer/create', {
      postId: post.id,
      postTitle: post.title,
      postContent: post.content
    });
  };

  const handleGoToAnswerUpdate = ({ id, content }: AnswerReponse) => {
    navigateWithParams('/community/qna/[postId]/answer/[answerId]/update', {
      postId: post.id,
      answerId: id,
      postTitle: post.title,
      postContent: post.content,
      initialContent: content
    });
    answerTools.setOpenMenuId(null);
  };

  const handleShowAnswerDeleteConfirm = (targetId: number) => {
    answerTools.selectTarget(targetId);
    openModal('answerDelete');
  };

  const handleShowAnswerAcceptConfirm = (targetId: number) => {
    answerTools.selectTarget(targetId);
    openModal('answerAccept');
  };

  const handleDelete = async () => {
    try {
      closeModal();
      await callDeleteQnaPost();
      goBackSafely();
    } catch (error) {
      showToast('삭제 실패', 'error');
    }
  };

  const handleDeleteAnswer = async () => {
    try {
      if (!targetAnswerId) return;
      await callDeleteAnswer(targetAnswerId);
      setAnswers(prev => prev.filter(answer => answer.id !== targetAnswerId)); // 답변 삭제
      setPost(prev => prev ? { ...prev, answerCount: prev.answerCount - 1 } : prev);  // 답변수 감소
    } catch (error) {
      showToast('삭제 실패', 'error');
    } finally {
      closeModal();
      answerTools.clearTarget();
    }
  }

  const handleAcceptAnswer = async () => {
    try {
      if (!targetAnswerId) return;
      await callAcceptAnswer(targetAnswerId);

      // 답변 채택
      setAnswers(prev =>
        prev.map(answer => {
          if (answer.id === targetAnswerId)
            return { ...answer, accepted: true };
          return answer;
        })
      );
      setPost(prev => prev ? { ...prev, accepted: true } : prev);
    } catch (error) {
      showToast('채택 실패', 'error');
    } finally {
      closeModal();
      answerTools.clearTarget();
    }
  }

  const handleToggleLike = async ({ id, liked, author }: AnswerReponse) => {
    if (author?.username === userContext?.username) {
      openModal('likeError');
      return;
    }

    try {
      await callToggleLikeAnswer(id);

      // 좋아요 갱신
      setAnswers(prev =>
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
    postDelete: '게시글을 삭제하시겠습니까?',
    answerDelete: '답변을 삭제하시겠습니까?',
    answerAccept: '답변을 채택하시겠습니까?',
    likeError: '내 답변은 좋아요 할 수 없습니다.'
  };

  const modalAction = {
    postDelete: handleDelete,
    answerDelete: handleDeleteAnswer,
    answerAccept: handleAcceptAnswer,
    likeError: undefined
  };

  if (loading) return <LoadingView />;

  return (
    <View style={STYLE.BASE_CONTAINER}>
      <Header title='Q&A' />

      {/* 답변 목록 */}
      <FlatList
        data={answers}
        keyExtractor={(item) => item.id.toString()}
        style={STYLE.WRAPPER}
        contentContainerStyle={{
          flexGrow: 1, // ScrollView가 화면 전체 높이 차지
          paddingBottom: post.accepted ? 0 : 70
        }}
        ListHeaderComponent={<QnaDetailHeader post={post} />}
        ListEmptyComponent={
          <View style={[STYLE.WRAPPER, STYLE.CENTER]}>
            <Text style={STYLE.EMPTY_TEXT}>답변이 없습니다.</Text>
          </View>
        }
        renderItem={({ item }) => (
          <AnswerItem
            answer={item}
            questioner={post.author.username}
            menuId={answerTools.openMenuId}
            accepted={post.accepted}
            onChangeMenuId={answerTools.setOpenMenuId}
            onGoToUpdate={handleGoToAnswerUpdate}
            onDeleteConfirm={handleShowAnswerDeleteConfirm}
            onAcceptConfirm={handleShowAnswerAcceptConfirm}
            onToggleLike={handleToggleLike}
          />
        )}
      />

      {/* 수정·삭제·답변 버튼 */}
      <View style={styles.buttonContainer}>
        {post.author.username === userContext?.username ? (
          <>
            <TouchableOpacity activeOpacity={0.8} style={styles.updateButton} onPress={handleGoToUpdate}>
              <IconSymbol name="pen" size={24} color='white' />
              <Text style={styles.buttonText}>수정하기</Text>
            </TouchableOpacity>
            <TouchableOpacity activeOpacity={0.8} style={styles.deleteButton} onPress={() => openModal('postDelete')}>
              <IconSymbol name="trash" size={24} color='white' />
            </TouchableOpacity>
          </>
        ) : (
          !post.accepted && (
            <TouchableOpacity activeOpacity={0.8} style={styles.answerButton} onPress={handleGoToAnswerCreate}>
              <IconSymbol name="plus.pen" size={24} color='white' />
              <Text style={styles.buttonText}>답변하기</Text>
            </TouchableOpacity>
          )
        )}
      </View>

      <ConfirmModal
        visible={modalVisible}
        title={modalTitle[type!]}
        onClose={closeModal}
        onAction={modalAction[type!]}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  buttonContainer: {
    position: 'absolute',
    flexDirection: 'row',
    bottom: 16,
    left: 16, right: 16,
    gap: 8
  },
  updateButton: { ...STYLE.BUTTON, width: '80%' },
  deleteButton: {
    ...STYLE.BUTTON,
    flex: 1,
    backgroundColor: COLOR.BUTTON.RED,
    paddingHorizontal: 16
  },
  answerButton: { ...STYLE.BUTTON, flex: 1 },
  buttonText: {
    marginLeft: 8,
    fontSize: 16,
    fontWeight: 700,
    color: 'white'
  }
});