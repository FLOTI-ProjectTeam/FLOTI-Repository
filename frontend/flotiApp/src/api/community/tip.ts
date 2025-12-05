import apiClient from '@/api/apiClient';
import { TIP_URL } from '@/constants/api'
import { PostRequest } from '@/types/community/common';
import { TipPostResponse, CommentRequest, CommentResponse } from '@/types/community/tip';

/* TIP 게시판 글 API */
// 1. 조회 & 검색
export const getTipPosts = (search?: string, sort = 'latest', page = 0) =>
    apiClient.get<TipPostResponse[]>(TIP_URL, { params: { search, sort, page } });

// 2. 상세 조회
export const getTipPost = (id: number) => 
    apiClient.get<TipPostResponse>(`${TIP_URL}/${id}`);

// 3. 등록
export const createTipPost = (data: PostRequest, file?: File) => {
    const formData = new FormData();
    formData.append('post', JSON.stringify(data));
    if (file) formData.append('file', file);
    return apiClient.post<TipPostResponse>(
        TIP_URL, formData, { headers: { 'Content-Type': 'multipart/form-data' } }
    );
};

// 4. 수정
export const updateTipPost = (id: number, data: PostRequest, file?: File, deleted = false) => {
    const formData = new FormData();
    formData.append('post', JSON.stringify(data));
    if (file) formData.append('file', file);
    formData.append('deleted', JSON.stringify(deleted));
    return apiClient.put<TipPostResponse>(
        `${TIP_URL}/${id}`, formData, { headers: { 'Content-Type': 'multipart/form-data' } }
    );
};

// 5. 삭제
export const deleteTipPost = (id: number) => 
    apiClient.delete(`${TIP_URL}/${id}`);

/* TIP 게시판 댓글 API */
// 1. 조회
export const getComments = (postId: number) => 
    apiClient.get<CommentResponse[]>(`${TIP_URL}/${postId}/comments`);
  
// 2. 등록
export const createComment = (postId: number, data: CommentRequest) => 
    apiClient.post<CommentResponse>(`${TIP_URL}/${postId}/comments`, data);

// 3. 수정
export const updateComment = (id: number) => 
    apiClient.put<CommentResponse>(`${TIP_URL}/comments/${id}`);

// 4. 삭제
export const deleteComment = (id: number) => 
    apiClient.delete(`${TIP_URL}/comments/${id}`);
