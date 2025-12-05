import apiClient from '@/api/apiClient';
import { TIP_API } from '@/constants/endpoints';
import { Page, LikeResponse, PostRequest } from '@/types/community/common';
import { TipPostResponse, CommentRequest, CommentResponse } from '@/types/community/tip';

/* 게시글 API */
// 조회 & 검색
export const getTipPosts = (search?: string, sort: string = 'latest', page: number = 0) =>
    apiClient.get<Page<TipPostResponse>>(TIP_API.BASE, { params: { search, sort, page } });

// 상세 조회
export const getTipPost = (postId: number) => 
    apiClient.get<TipPostResponse>(TIP_API.BASE_DETAIL(postId));

// 등록
export const createTipPost = (post: PostRequest, file?: File) => {
    const formData = new FormData();
    formData.append('post', JSON.stringify(post));
    if (file) formData.append('file', file);
    return apiClient.post<TipPostResponse>(
        TIP_API.BASE, formData, 
        { headers: { 'Content-Type': 'multipart/form-data' } }
    );
};

// 수정
export const updateTipPost = (postId: number, post: PostRequest, file?: File, deleted = false) => {
    const formData = new FormData();
    formData.append('post', JSON.stringify(post));
    if (file) formData.append('file', file);
    formData.append('deleted', JSON.stringify(deleted));
    return apiClient.put<TipPostResponse>(
        TIP_API.BASE_DETAIL(postId), formData, 
        { headers: { 'Content-Type': 'multipart/form-data' } }
    );
};

// 삭제
export const deleteTipPost = (postId: number) => 
    apiClient.delete(TIP_API.BASE_DETAIL(postId));

// 좋아요 토글
export const toggleLikeTipPost = (postId: number) => 
    apiClient.post<LikeResponse>(TIP_API.LIKE(postId));

/* 댓글 API */
// 조회
export const getComments = (postId: number) => 
    apiClient.get<CommentResponse[]>(TIP_API.ENTITY_BASE(postId));
  
// 등록
export const createComment = (postId: number, comment: CommentRequest) => 
    apiClient.post<CommentResponse>(TIP_API.ENTITY_BASE(postId), comment);

// 수정
export const updateComment = (postId: number, commentId: number, comment: CommentRequest) => 
    apiClient.put<CommentResponse>(TIP_API.ENTITY_DETAIL(postId, commentId), comment);

// 삭제
export const deleteComment = (postId: number, commentId: number) => 
    apiClient.delete(TIP_API.ENTITY_DETAIL(postId, commentId));