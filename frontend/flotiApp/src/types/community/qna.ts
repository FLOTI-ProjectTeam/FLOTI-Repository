import { UserResponse } from '@/types/community/common';

export interface QnaPostResponse {
    id: number;
    author: UserResponse;
    title: string;
    content: string;
    answerCount: number;
    accepted: boolean;
    createdAt: string;
}

export interface QnaPostDetailResponse extends QnaPostResponse {
    answers: AnswerReponse[];
}

export interface AnswerRequest {
    content: string;
}

export interface AnswerReponse {
    id: number;
    postId: number;
    author: UserResponse | null;
    content: string;
    likeCount: number;
    accepted: boolean;
    liked: boolean;
    createdAt: string;
}