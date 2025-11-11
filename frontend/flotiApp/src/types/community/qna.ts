import { AuthorResponse } from '@/types/community/common';

export interface QnaPostResponse {
    id: number;
    author: AuthorResponse;
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
    author: AuthorResponse | null;
    content: string;
    likeCount: number;
    accepted: boolean;
    liked: boolean;
    createdAt: string;
}