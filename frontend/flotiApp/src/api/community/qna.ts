import apiClient from '@/api/apiClient';
import { QNA_API } from '@/constants/endpoints';
import { PostRequest } from '@/types/community/common';
import { QnaPostResponse, AnswerRequest, AnswerReponse } from '@/types/community/qna';

/* 게시글 API */
// 1. 조회 & 검색
export const getQnaPosts = (search?: string, sort: string = 'latest', page: number = 0, accepted: boolean = false) =>
    apiClient.get<QnaPostResponse[]>(QNA_API.POST_BASE, { params: { search, sort, page, accepted } });

// 2. 상세 조회
export const getQnaPost = (postId: number) => 
    apiClient.get<QnaPostResponse>(QNA_API.POST_DETAIL(postId));

// 3. 등록
export const createQnaPost = (data: PostRequest) => 
    apiClient.post<QnaPostResponse>(QNA_API.POST_BASE, data);

// 4. 수정
export const updateQnaPost = (postId: number, data: PostRequest) =>
    apiClient.put<QnaPostResponse>(QNA_API.POST_DETAIL(postId), data);

// 5. 삭제
export const deleteQnaPost = (postId: number) => 
    apiClient.delete(QNA_API.POST_DETAIL(postId));

/* 답변 API */
// 1. 등록
export const createAnswer = (postId: number, data: AnswerRequest) => 
    apiClient.post<AnswerReponse>(QNA_API.ENTITY_BASE(postId), data);
  
// 2. 수정
export const updateAnswer = (answerId: number, data: AnswerRequest) => 
    apiClient.put<AnswerReponse>(QNA_API.ENTITY_DETAIL(answerId), data);

// 3. 삭제
export const deleteAnswer = (answerId: number) => 
    apiClient.delete(QNA_API.ENTITY_DETAIL(answerId));

// 4. 채택
export const acceptAnswer = (postId: number, answerId: number) => 
    apiClient.patch(QNA_API.ACCEPT_ENTITY(postId, answerId));