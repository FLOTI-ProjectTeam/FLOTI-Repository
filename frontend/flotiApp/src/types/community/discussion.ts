import { AuthorResponse } from "./common";

export interface DiscussionRoomRequest {
    title: string;
    intro: string;
    maxParticipants: number;
}

export interface DiscussionRoomResponse {
    id: number;
    author: AuthorResponse;
    title: string;
    intro: string;
    maxParticipants: number;
    participantCount: number;
    createdAt: string;
}

export interface DiscussionRoomDetailResponse extends DiscussionRoomResponse {
    participants: AuthorResponse[];
    messages: MessageResponse[];
}

export interface MessageRequest {
    content: number;
}

export interface MessageResponse {
    id: number;
    roomId: number;
    author: AuthorResponse | null;
    content: string;
    likeCount: number;
    liked: boolean;
}

export interface MessageDeleteResponse {
    id: number;
}