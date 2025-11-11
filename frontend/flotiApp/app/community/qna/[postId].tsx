import { View, Text, StyleSheet, FlatList, Pressable, TouchableOpacity, Alert } from 'react-native';
import { router, useLocalSearchParams } from 'expo-router';
import { useEffect, useState } from 'react';

import { dummyPostDetails } from '@/__mocks__/qna';
import { IconSymbol } from '@/components/ui/IconSymbol';
import BreakAllText from '@/components/ui/BreakAllText';
import DeleteConfirmModal from '@/components/ui/DeleteConfirmModal';
import { LoadingView, EmptyView } from '@/components/CommunityStateView';
import COLOR from '@/constants/colors';
import { STYLE } from '@/constants/styles';
import { QnaPostDetailResponse } from '@/types/community/qna';

export default function QnaDetailScreen() {
  const { postId } = useLocalSearchParams();  // URL에서 게시글 ID 가져오기
  const [loading, setLoading] = useState(true);
  const [post, setPost] = useState<QnaPostDetailResponse>();
  const [confirmVisible, setConfirmVisible] = useState(false);

  useEffect(() => {
    // 더미 데이터 호출
    const found = dummyPostDetails.find((p) => p.id.toString() === postId);
    setPost(found);
    setLoading(false);
  }, [postId]);

  if (loading) return <LoadingView />
  if (!post) return <EmptyView text='게시글을 찾을 수 없습니다.' />

  // 답변 작성 화면으로 이동
  const goToAnswerCreateScreen = () => {
    router.push({
      pathname: '/community/qna/[postId]/answer/create',
      params: { 
        postId: String(post.id),
        title: post.title, 
        content: post.content 
      }
    });
  };

  // 수정 화면으로 이동
  const goToPostUpdateScreen = () => {
    router.push({
      pathname: '/community/qna/update/[postId]',
      params: { 
        postId: String(post.id),
        title: post.title, 
        content: post.content 
      }
    });
  };

  // 삭제 확인 모달 표시
  const showDeleteConfirmModal = () => {
    setConfirmVisible(true);
  };

  // 삭제 처리
  const handleDelete = async (answerId: number) => {
    try {
      setConfirmVisible(false);
      router.back();
    } catch (error) {
      console.error(error);
      Alert.alert('삭제 실패', '잠시 후 다시 시도해주세요.');
    }
  };

  return (
    <View style={STYLE.BASE_CONTAINER}>
      {/* 답변 목록 */}
      <FlatList
        data={post.answers}
        keyExtractor={(item) => item.id.toString()}
        style={[STYLE.WRAPPER, STYLE.NO_PADDING_BOTTOM]}
        contentContainerStyle={{ 
          flexGrow: 1, // ScrollView가 화면 전체 높이 차지,
          paddingBottom: 90 
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
          <View style={styles.card}>
            {/* 작성자 */}
            <Text style={styles.author}>{item.author?.nickname ?? '탈퇴한 사용자'}</Text>

            {/* 내용 */}
            <BreakAllText style={styles.content}>{item.content}</BreakAllText>

            {/* 좋아요수 & 시간 */}
            <View style={styles.infoRow}>
              <Pressable style={styles.likeButton}>
                <IconSymbol size={20} name="heart" color={COLOR.ICON.GRAY_DARK} />
                <Text style={styles.likeCount}> {item.likeCount}</Text>
              </Pressable>
              <Text style={styles.time}>{item.createdAt}</Text>
            </View>
          </View>
        )}
      />

      {/* 답변 버튼 */}
      <TouchableOpacity activeOpacity={0.8} style={styles.answerButton} onPress={goToAnswerCreateScreen}>
        <IconSymbol name="plus.pen" size={24} color='white' />
        <Text style={styles.answerButtonText}>답변하기</Text>
      </TouchableOpacity>

      {/* 수정 & 삭제 버튼 */}
      {/* <View style={styles.bottomButtonRow}>
        <TouchableOpacity style={styles.editButton} onPress={goToPostUpdateScreen}>
          <IconSymbol name="pen" size={24} color='white' />
          <Text style={styles.answerButtonText}>수정하기</Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.deleteButton} onPress={showDeleteConfirmModal}>
          <IconSymbol name="trash" size={24} color='white' />
        </TouchableOpacity>
      </View> */}

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
  answerCount: { color: COLOR.TEXT.SKY },
  card: {
    marginBottom: 16,
    padding: 12,
    borderRadius: 10,
    backgroundColor: COLOR.BACKGROUND.POWDER_LIGHT,
    gap: 8
  },
  infoRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingTop: 8,
    borderTopWidth: 1,
    borderColor: COLOR.TINT.SLATE_SOFT
  },
  likeButton: { flexDirection: 'row', alignItems: 'center' },
  likeCount: { fontSize: 12, color: COLOR.TEXT.GRAY_MEDIUM },
  bottomButtonRow: {
    position: 'absolute',
    flexDirection: 'row',
    bottom: 16,
    left: 16, right: 16,
    gap: 8
  },
  editButton: {
    width: '80%',
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: COLOR.BUTTON.NAVY,
    paddingVertical: 10, paddingHorizontal: 16,
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
    paddingVertical: 10, paddingHorizontal: 16,
    borderRadius: 16
  },
  answerButtonText: {
    marginLeft: 8,
    fontSize: 16,
    fontWeight: 'bold',
    color: 'white'
  }
});
