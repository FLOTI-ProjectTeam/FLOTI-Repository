import { AuthorResponse } from '@/types/community/common';

export interface TipPostResponse {
    id: number;
    author: AuthorResponse;
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
    author: AuthorResponse | null;
    content: string | null;
    deleted: boolean;
    replies: CommentResponse[];
    createdAt: string | null;
}