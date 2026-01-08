import { UserResponse } from '@/types/community/common';

export interface ChallengeSummaryResponse {
    id: number;
    title: string;
    intro: string; // Changed from description
    author: UserResponse;
    startDate: string;
    endDate: string;
    currentParticipants: number; // Changed from participantCount
    maxParticipants: number;
    isCompleted: boolean; // Changed from status
    createdAt: string;
    progress: number;
}

export interface ParticipantResponse {
    id: number;
    nickname: string;
    profileImage: string | null;
    progress: number;
    contribution: number;
}

export interface ChallengeDetailResponse extends ChallengeSummaryResponse {
    content: string;
    myProgress: number; // Integer in backend
    progress: number; // totalProgress -> progress
    participants: ParticipantResponse[];
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
