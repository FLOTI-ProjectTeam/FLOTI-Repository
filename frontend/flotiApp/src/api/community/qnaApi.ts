import apiClient from '@/api/apiClient';
import { QNA_API } from '@/constants/endpoints';
import { Page, LikeResponse,PostRequest } from '@/types/community/common';
import { QnaPostResponse, AnswerRequest, AnswerReponse, QnaPostDetailResponse } from '@/types/community/qna';

/* 게시글 API */
// 조회 & 검색
export const getQnaPosts = (search?: string, sort: string = 'latest', page: number = 0, accepted: boolean = false) =>
    apiClient.get<Page<QnaPostResponse>>(QNA_API.BASE, { params: { search, sort, page, accepted } });

// 상세 조회
export const getQnaPost = (postId: number) => 
    apiClient.get<QnaPostDetailResponse>(QNA_API.BASE_DETAIL(postId));

// 등록
export const createQnaPost = (post: PostRequest) => 
    apiClient.post<QnaPostResponse>(QNA_API.BASE, post);

// 수정
export const updateQnaPost = (postId: number, post: PostRequest) =>
    apiClient.put<QnaPostResponse>(QNA_API.BASE_DETAIL(postId), post);

// 삭제
export const deleteQnaPost = (postId: number) => 
    apiClient.delete(QNA_API.BASE_DETAIL(postId));

/* 답변 API */
// 등록
export const createAnswer = (postId: number, post: AnswerRequest) => 
    apiClient.post<AnswerReponse>(QNA_API.ENTITY_BASE(postId), post);
  
// 수정
export const updateAnswer = (postId: number, answerId: number, post: AnswerRequest) => 
    apiClient.put<AnswerReponse>(QNA_API.ENTITY_DETAIL(postId, answerId), post);

// 삭제
export const deleteAnswer = (postId: number, answerId: number) => 
    apiClient.delete(QNA_API.ENTITY_DETAIL(postId, answerId));

// 채택
export const acceptAnswer = (postId: number, answerId: number) => 
    apiClient.patch(QNA_API.ACCEPT(postId, answerId));

// 좋아요 토글
export const toggleLikeAnswer = (postId: number, answerId: number) => 
    apiClient.post<LikeResponse>(QNA_API.LIKE(postId, answerId));