import { UserResponse } from '@/types/community/common';

export interface DiscussionRoomRequest {
    title: string;
    content: string;
    maxParticipantCount: number;
}

export interface DiscussionRoomResponse {
    id: number;
    author: UserResponse;
    title: string;
    content: string;
    maxParticipantCount: number;
    participantCount: number;
    joined: boolean;
    createdAt: string;
    recentActivityAt: string;
}

export interface DiscussionRoomDetailResponse extends DiscussionRoomResponse {
    participants: UserResponse[];
    messages: MessageResponse[];
}

export interface MessageRequest {
    content: string;
}

export interface MessageResponse {
    id: number;
    roomId: number;
    author: UserResponse | null;
    content: string;
    likeCount: number;
    liked: boolean;
    createdAt: string;
}

export interface MessageDeleteResponse {
    id: number;
}