import apiClient from '@/api/apiClient';
import { CHALLENGE_API } from '@/constants/endpoints';
import { Page } from '@/types/community/common';
import { ChallengeSummaryResponse, ChallengeDetailResponse, ChallengeRequest, FeedResponse, FeedRequest } from '@/types/community/challenge';

/* 챌린지 API */
// 목록 조회
export const getChallengePosts = (search?: string, sort: string = 'latest', page: number = 0) =>
    apiClient.get<Page<ChallengeSummaryResponse>>(CHALLENGE_API.BASE, { params: { search, sort, page } });

// 내 챌린지 조회
export const getMyChallengePosts = (search?: string, sort: string = 'latest', page: number = 0) =>
    apiClient.get<Page<ChallengeSummaryResponse>>(CHALLENGE_API.MINE, { params: { search, sort, page } });

// 상세 조회
export const getChallengePost = (id: number) =>
    apiClient.get<ChallengeDetailResponse>(CHALLENGE_API.DETAIL(id));

// 등록
export const createChallengePost = (post: ChallengeRequest) =>
    apiClient.post<ChallengeSummaryResponse>(CHALLENGE_API.BASE, post);

// 수정
export const updateChallengePost = (id: number, post: ChallengeRequest) =>
    apiClient.put<ChallengeSummaryResponse>(CHALLENGE_API.DETAIL(id), post);

// 삭제
export const deleteChallengePost = (id: number) =>
    apiClient.delete(CHALLENGE_API.DETAIL(id));

// 참여
export const joinChallenge = (id: number) =>
    apiClient.post(CHALLENGE_API.JOIN(id));

// 진행률 조회
export const getChallengeProgress = (id: number) =>
    apiClient.get<{ progress: number, myProgress?: number }>(CHALLENGE_API.PROGRESS(id));

/* 피드 API */
// 목록 조회
export const getChallengeFeeds = (id: number, page: number = 0) =>
    apiClient.get<Page<FeedResponse>>(CHALLENGE_API.FEEDS(id), { params: { page } });

// 작성
export const createChallengeFeed = (id: number, feed: FeedRequest) =>
    apiClient.post<FeedResponse>(CHALLENGE_API.FEEDS(id), feed);

// 상세 조회 (피드 컨트롤러 사용)
export const getChallengeFeed = (feedId: number) =>
    apiClient.get<FeedResponse>(CHALLENGE_API.FEED_DETAIL(feedId));

// 수정 (피드 컨트롤러 사용)
export const updateChallengeFeed = (feedId: number, feed: FeedRequest) =>
    apiClient.put<FeedResponse>(CHALLENGE_API.FEED_DETAIL(feedId), feed);

// 삭제 (피드 컨트롤러 사용)
export const deleteChallengeFeed = (feedId: number) =>
    apiClient.delete(CHALLENGE_API.FEED_DETAIL(feedId));
