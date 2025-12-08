import { UserResponse } from '@/types/community/common';

export interface ChallengeSummaryResponse {
    id: number;
    title: string;
    intro: string; // Changed from description
    startDate: string;
    endDate: string;
    currentParticipants: number; // Changed from participantCount
    maxParticipants: number;
    isCompleted: boolean; // Changed from status
    createdAt: string;
}

export interface ChallengeDetailResponse extends ChallengeSummaryResponse {
    content: string;
    myProgress: number; // Integer in backend
    progress: number; // totalProgress -> progress
    participants: any[]; // generic for now, backend has List<ParticipantResponse>
}

export interface ChallengeRequest {
    title: string;
    content: string;
    intro: string; // description -> intro
    startDate: string;
    endDate: string;
    maxParticipants: number;
    // tags removed as they are missing in backend DTO
}

export interface FeedResponse {
    id: number;
    // challengeId removed
    author: UserResponse;
    content: string;
    // imageUrls removed
    createdAt: string;
}

export interface FeedRequest {
    content: string;
    // imageUrls removed
}
