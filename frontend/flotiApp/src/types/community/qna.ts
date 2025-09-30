import { AuthorResponse } from '@/types/community/common';

export interface QnaPostResponse {
    id: number;
    author: AuthorResponse;
    title: string;
    content: string;
    answerCount: number;
    liked: boolean;
    answers: AnswerReponse[];
}

export interface AnswerRequest {
    content: string;
}

export interface AnswerReponse {
    id: number;
    postId: number;
    author: AuthorResponse;
    content: string;
    likeCount: number;
    accepted: boolean;
    liked: boolean;
}