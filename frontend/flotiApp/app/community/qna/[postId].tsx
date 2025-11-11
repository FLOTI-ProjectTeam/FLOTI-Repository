import { View, Text, StyleSheet, FlatList, TouchableOpacity } from 'react-native';
import { router, useLocalSearchParams } from 'expo-router';
import { useEffect, useState } from 'react';

import { dummyPostDetails } from '@/__mocks__/qna';
import { deleteAnswer, deleteQnaPost, getQnaPost, toggleLikeAnswer } from '@/api/community/qnaApi';
import { IconSymbol } from '@/components/ui/IconSymbol';
import BreakAllText from '@/components/ui/BreakAllText';
import DeleteConfirmModal from '@/components/ui/DeleteConfirmModal';
import { LoadingView, EmptyView } from '@/components/CommunityStateView';
import AnswerItem from '@/components/AnswerItem';
import { showToast } from '@/utils/toast';
import { AnswerReponse, QnaPostDetailResponse } from '@/types/community/qna';
import COLOR from '@/constants/colors';
import { STYLE } from '@/constants/styles';

export default function QnaDetailScreen() {
  const [loading, setLoading] = useState(true);
  const { postId } = useLocalSearchParams();  // URL에서 게시글 ID 가져오기

  const [post, setPost] = useState<QnaPostDetailResponse>();
  const [confirmVisible, setConfirmVisible] = useState(false);  // 삭제 확인 모달 표시 여부

  const [answers, setAnswers] = useState<AnswerReponse[]>([]);
  const [menuAnswerId, setMenuAnswerId] = useState<number | null>(null); // 열린 메뉴 댓글 ID
  const [answerConfirmVisible, setAnswerConfirmVisible] = useState(false);  // 답변 삭제 확인 모달 표시 여부
  const [targetAnswerId, setTargetAnswerId] = useState<number | null>(null); // 삭제할 답변 ID

  /* API 호출 */
  const fetchPost = async () => {
    try {
      const response = await getQnaPost(Number(postId));
      setPost(response.data);
      setAnswers(response.data.answers);
    } catch (error) {
      // 서버 호출 실패 시 더미 데이터로 대체
      const filtered = dummyPostDetails.find((post) => post.id.toString() === postId);
      setPost(filtered);
      setAnswers(filtered!.answers);
    } finally {
      setLoading(false);
    }
  }

  const callDeleteQnaPost = () => deleteQnaPost(post!.id);
  const callDeleteAnswer = (answerId: number) => deleteAnswer(post!.id, answerId);
  const callToggleLikeAnswer = (answerId: number) => toggleLikeAnswer(post!.id, answerId)

  // 게시글 ID 변경 시 실행
  useEffect(() => {
    fetchPost();
  }, [postId]);

  /* 이벤트 핸들러 */
  const handleGoToUpdate = () => {
    router.push({
      pathname: '/community/qna/update/[postId]',
      params: { 
        postId: post!.id,
        title: post!.title, 
        content: post!.content 
      }
    });
  };

  const handleGoToAnswerCreate = () => {
    router.push({
      pathname: '/community/qna/[postId]/answer/create',
      params: { 
        postId: post!.id,
        postTitle: post!.title, 
        postContent: post!.content 
      }
    });
  };

  const goToAnswerUpdate = (answer: AnswerReponse) => {
    router.push({
      pathname: '/community/qna/[postId]/answer/[answerId]/update',
      params: { 
          postId: post!.id,
          answerId: answer.id,
          postTitle: post!.title, 
          postContent: post!.content,
          content: answer.content
      }
    });

    setMenuAnswerId(null);
  };

  const handleShowConfirm = () => {
    setConfirmVisible(true);
  };

  const showAnswerConfirm = (answerId: number) => {
    setTargetAnswerId(answerId);
    setMenuAnswerId(null);
    setAnswerConfirmVisible(true);
  };

  const handleDelete = async () => {
    try {
      setConfirmVisible(false);
      await callDeleteQnaPost();
      router.back();
    } catch (error) {
      showToast('삭제 중 오류가 발생했습니다.', 'error');
    }
  };

  const handleDeleteAnswer = async () => {
    try {
      if (!targetAnswerId) return;
      await callDeleteAnswer(targetAnswerId);
      setAnswers(prev => prev.filter(answer => answer.id !== targetAnswerId)); // 답변 삭제
      setPost(prev => prev ? { ...prev, answerCount: prev.answerCount - 1 } : prev);  // 답변수 감소
    } catch (error) {
      showToast('삭제 중 오류가 발생했습니다.', 'error');
    } finally {
      setAnswerConfirmVisible(false);
      setTargetAnswerId(null);
    }
  }

  const handleToggleLike = async ({ id, liked }: AnswerReponse) => {
    try {
      await callToggleLikeAnswer(id);

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
      const action = liked ? '좋아요 취소' : '좋아요';
      showToast(`${action} 중 오류가 발생했습니다.`, 'error');
    }
  }

  if (loading) return <LoadingView />
  if (!post) return <EmptyView />

  return (
    <View style={STYLE.BASE_CONTAINER}>
      {/* 답변 목록 */}
      <FlatList
        data={answers}
        keyExtractor={(item) => item.id.toString()}
        style={STYLE.WRAPPER}
        contentContainerStyle={{ 
          flexGrow: 1, // ScrollView가 화면 전체 높이 차지,
          paddingBottom: 60 
        }}
        ListHeaderComponent={
          <>
            {/* 제목 */}
            <BreakAllText style={styles.title}>{post.title}</BreakAllText>

            {/* 작성자 & 시간 */}
            <Text style={styles.author}>{post.author.nickname}</Text>
            <Text style={styles.time}>{post.createdAt}</Text>

            {/* 본문 */}
            <View style={styles.contentContainer}>
              <BreakAllText style={styles.content}>{post.content}</BreakAllText>
            </View>

            {/* 답변수 */}
            <View style={styles.answerHeader}>
              <Text style={styles.answerLabel}>답변 </Text>
              <Text style={[styles.answerLabel, styles.answerCount]}>{post.answerCount}</Text>
            </View>
          </>
        }
        ListEmptyComponent={
          <View style={[STYLE.WRAPPER, STYLE.CENTER]}>
            <Text style={STYLE.EMPTY_TEXT}>답변이 없습니다.</Text>
          </View>
        }
        renderItem={({ item }) => (
          <AnswerItem
            answer={item}
            menuId={menuAnswerId}
            onChangeMenuId={setMenuAnswerId}
            onGoToUpdate={goToAnswerUpdate}
            onDeleteConfirm={showAnswerConfirm}
            onToggleLike={handleToggleLike}
          />
        )}
      />

      {/* 답변 버튼 */}
      <TouchableOpacity activeOpacity={0.8} style={styles.answerButton} onPress={handleGoToAnswerCreate}>
        <IconSymbol name="plus.pen" size={24} color='white' />
        <Text style={styles.answerButtonText}>답변하기</Text>
      </TouchableOpacity>

      {/* 수정 & 삭제 버튼 */}
      {/* <View style={styles.bottomButtonRow}>
        <TouchableOpacity style={styles.updateButton} onPress={handleGoToUpdate}>
          <IconSymbol name="pen" size={24} color='white' />
          <Text style={styles.answerButtonText}>수정하기</Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.deleteButton} onPress={handleShowConfirm}>
          <IconSymbol name="trash" size={24} color='white' />
        </TouchableOpacity>
      </View> */}

      {/* 삭제 확인 모달 */}
      <DeleteConfirmModal
        visible={confirmVisible}
        title='게시글을 삭제하시겠습니까?'
        onCancel={() => setConfirmVisible(false)}
        onDelete={handleDelete}
      />
      <DeleteConfirmModal
        visible={answerConfirmVisible}
        title='답변을 삭제하시겠습니까?'
        onCancel={() => setAnswerConfirmVisible(false)}
        onDelete={handleDeleteAnswer}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  title: {
    marginBottom: 8,
    fontSize: 20,
    fontWeight: 'bold',
    color: 'black'
  },
  author: { fontSize: 14, fontWeight: 'bold', color: COLOR.TEXT.GRAY_DARK },
  time: { fontSize: 12, color: COLOR.TEXT.GRAY_MEDIUM },
  contentContainer: {
    marginTop: 12,
    padding: 16,
    borderRadius: 12,
    backgroundColor: COLOR.BACKGROUND.SLATE_LIGHT
  },
  content: { fontSize: 15, lineHeight: 22, color: COLOR.TEXT.GRAY_DARK },
  answerHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    marginTop: 16,
    paddingVertical: 12,
    borderTopWidth: 1,
    borderColor: COLOR.TINT.GRAY
  },
  answerLabel: { fontSize: 18, fontWeight: 'bold', color: COLOR.TEXT.GRAY_DARK },
  answerCount: { color: 'skyblue' },
  bottomButtonRow: {
    position: 'absolute',
    flexDirection: 'row',
    bottom: 16,
    left: 16, right: 16,
    gap: 8
  },
  updateButton: {
    width: '80%',
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: COLOR.BUTTON.NAVY,
    paddingVertical: 10,
    borderRadius: 16
  },
  deleteButton: {
    flex: 1,
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: COLOR.BUTTON.RED,
    paddingVertical: 10, paddingHorizontal: 16,
    borderRadius: 16
  },
  answerButton: {
    position: 'absolute',
    bottom: 16,
    left: 16, right: 16,
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: COLOR.BUTTON.NAVY,
    paddingVertical: 10,
    borderRadius: 16
  },
  answerButtonText: {
    marginLeft: 8,
    fontSize: 16,
    fontWeight: 'bold',
    color: 'white'
  }
});
