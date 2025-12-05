import { UserResponse } from '@/types/community/common';

export interface TipPostResponse {
    id: number;
    author: UserResponse;
    title: string;
    content: string;
    thumbnail: string | null;
    commentCount: number;
    likeCount: number;
    liked: boolean;
    createdAt: string;
}

export interface CommentRequest {
    parentId: number | null;
    content: string;
}

export interface CommentResponse {
    id: number;
    postId: number;
    parentId: number | null;
    author: UserResponse | null;
    content: string | null;
    deleted: boolean;
    replies: CommentResponse[];
    createdAt: string | null;
}