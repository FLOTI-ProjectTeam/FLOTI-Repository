import { AuthorResponse } from '@/types/community/common';

export interface TipPostResponse {
    id: number;
    author: AuthorResponse;
    title: string;
    content: string;
    thumbnail: string;
    commentCount: number;
    likeCount: number;
    liked: boolean;
}

export interface CommentRequest {
    parentId: number;
    content: string;
}

export interface CommentResponse {
    id: number;
    postId: number;
    author: AuthorResponse;
    content: string;
    thumbnail: string;
    likeCount: number;
    deleted: boolean;
    replies: CommentResponse[];
}