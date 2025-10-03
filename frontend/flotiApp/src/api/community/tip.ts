import apiClient from '@/api/apiClient';
import { TIP_API } from '@/constants/endpoints';
import { PostRequest } from '@/types/community/common';
import { TipPostResponse, CommentRequest, CommentResponse } from '@/types/community/tip';

/* 게시글 API */
// 1. 조회 & 검색
export const getTipPosts = (search?: string, sort: string = 'latest', page: number = 0) =>
    apiClient.get<TipPostResponse[]>(TIP_API.POST_BASE, { params: { search, sort, page } });

// 2. 상세 조회
export const getTipPost = (postId: number) => 
    apiClient.get<TipPostResponse>(TIP_API.POST_DETAIL(postId));

// 3. 등록
export const createTipPost = (data: PostRequest, file?: File) => {
    const formData = new FormData();
    formData.append('post', JSON.stringify(data));
    if (file) formData.append('file', file);
    return apiClient.post<TipPostResponse>(
        TIP_API.POST_BASE, formData, { headers: { 'Content-Type': 'multipart/form-data' } }
    );
};

// 4. 수정
export const updateTipPost = (postId: number, data: PostRequest, file?: File, deleted = false) => {
    const formData = new FormData();
    formData.append('post', JSON.stringify(data));
    if (file) formData.append('file', file);
    formData.append('deleted', JSON.stringify(deleted));
    return apiClient.put<TipPostResponse>(
        TIP_API.POST_DETAIL(postId), formData, { headers: { 'Content-Type': 'multipart/form-data' } }
    );
};

// 5. 삭제
export const deleteTipPost = (postId: number) => 
    apiClient.delete(TIP_API.POST_DETAIL(postId));

/* 댓글 API */
// 1. 조회
export const getComments = (postId: number) => 
    apiClient.get<CommentResponse[]>(TIP_API.ENTITY_BASE(postId));
  
// 2. 등록
export const createComment = (postId: number, data: CommentRequest) => 
    apiClient.post<CommentResponse>(TIP_API.ENTITY_BASE(postId), data);

// 3. 수정
export const updateComment = (commentId: number) => 
    apiClient.put<CommentResponse>(TIP_API.ENTITY_DETAIL(commentId));

// 4. 삭제
export const deleteComment = (commentId: number) => 
    apiClient.delete(TIP_API.ENTITY_DETAIL(commentId));
