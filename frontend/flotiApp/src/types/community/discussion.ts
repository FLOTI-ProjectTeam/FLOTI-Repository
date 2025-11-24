import { AuthorResponse } from '@/types/community/common';

export interface DiscussionRoomRequest {
    title: string;
    content: string;
    maxParticipantCount: number;
}

export interface DiscussionRoomResponse {
    id: number;
    author: AuthorResponse;
    title: string;
    content: string;
    maxParticipantCount: number;
    participantCount: number;
    createdAt: string;
    recentActivityAt: string;
}

export interface DiscussionRoomDetailResponse extends DiscussionRoomResponse {
    participants: AuthorResponse[];
    messages: MessageResponse[];
}

export interface MessageRequest {
    content: string;
}

export interface MessageResponse {
    id: number;
    roomId: number;
    author: AuthorResponse | null;
    content: string;
    likeCount: number;
    liked: boolean;
    createdAt: string;
}

export interface MessageDeleteResponse {
    id: number;
}