import apiClient from '@/api/apiClient';
import { DISCUSSION_API } from '@/constants/endpoints';
import { Page } from '@/types/community/common';
import { DiscussionRoomDetailResponse, DiscussionRoomRequest, DiscussionRoomResponse, MessageResponse } from '@/types/community/discussion';

/* 토론방 API */
// 조회 & 검색
export const getDiscussionRooms = (search?: string, sort: string = 'recentActivity', page: number = 0) =>
    apiClient.get<Page<DiscussionRoomResponse>>(DISCUSSION_API.BASE, { params: { search, sort, page } });

// 상세 조회
export const getDiscussionRoom = (roomId: number) => 
    apiClient.get<DiscussionRoomDetailResponse>(DISCUSSION_API.BASE_DETAIL(roomId));

// 등록
export const createDiscussionRoom = (room: DiscussionRoomRequest) => 
    apiClient.post<DiscussionRoomResponse>(DISCUSSION_API.BASE, room);

// 수정
export const updateDiscussionRoom = (roomId: number, room: DiscussionRoomRequest) =>
    apiClient.put<DiscussionRoomResponse>(DISCUSSION_API.BASE_DETAIL(roomId), room);

// 삭제
export const deleteDiscussionRoom = (roomId: number) => 
    apiClient.delete(DISCUSSION_API.BASE_DETAIL(roomId));

// 참여 토글
export const toggleJoinDiscussion = (roomId: number) => 
    apiClient.post(DISCUSSION_API.JOIN(roomId));

/* 메시지 API */
// 조회
export const getMessages = (roomId: number) => 
  apiClient.get<MessageResponse[]>(DISCUSSION_API.ENTITY_BASE(roomId));