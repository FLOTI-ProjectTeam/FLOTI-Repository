import apiClient from '@/api/apiClient';
import { QNA_URL } from '@/constants/api'
import { PostRequest } from '@/types/community/common';
import { QnaPostResponse, AnswerRequest, AnswerReponse } from '@/types/community/qna';

/* QNA 게시판 글 API */
// 1. 조회 & 검색
export const getQnaPosts = (search?: string, sort = 'latest', page = 0, accepted = 'false') =>
    apiClient.get<QnaPostResponse[]>(QNA_URL, { params: { search, sort, page, accepted } });

// 2. 상세 조회
export const getQnaPost = (id: number) => 
    apiClient.get<QnaPostResponse>(`${QNA_URL}/${id}`);

// 3. 등록
export const createQnaPost = (data: PostRequest) => 
    apiClient.post<QnaPostResponse>(QNA_URL, data);

// 4. 수정
export const updateQnaPost = (id: number, data: PostRequest) =>
    apiClient.put<QnaPostResponse>(`${QNA_URL}/${id}`, data);

// 5. 삭제
export const deleteQnaPost = (id: number) => 
    apiClient.delete(`${QNA_URL}/${id}`);

/* QNA 게시판 답변 API */
// 1. 등록
export const createAnswer = (postId: number, data: AnswerRequest) => 
    apiClient.post<AnswerReponse>(`${QNA_URL}/${postId}/answers`, data);
  
// 2. 수정
export const updateAnswer = (id: number, data: AnswerRequest) => 
    apiClient.put<AnswerReponse>(`${QNA_URL}/answers/${id}`, data);

// 3. 삭제
export const deleteAnswer = (id: number) => 
    apiClient.delete(`${QNA_URL}/answers/${id}`);

// 4. 채택
export const acceptAnswer = (postId: number, id: number) => 
    apiClient.patch(`${QNA_URL}/${postId}/answers/${id}/accept`);