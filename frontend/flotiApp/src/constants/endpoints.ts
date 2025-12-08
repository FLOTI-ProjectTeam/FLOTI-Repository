import { Platform } from 'react-native';
import { createCommunityEndpoint } from '../utils/apiHelpers';

/* 서버 주소 */
// 안드로이드 에뮬레이터는 10.0.2.2가 PC의 localhost를 가리킵니다.
// 실기기(폰)로 테스트할 경우 PC의 내부 IP(예: 192.168.x.x)를 입력해야 합니다.
export const BASE_URL = Platform.OS === 'android' ? 'http://10.0.2.2:8080' : 'http://localhost:8080';

/* 인증 API */
export const AUTH_API = {
    LOGIN: '/auth/login',
    SIGNUP: '/auth/signup',
    SIGNUP_SEND_CODE: '/auth/signup/send-code',
    SIGNUP_VERIFY_CODE: '/auth/signup/verify-code',
    SIGNUP_CHECK_USERNAME: '/auth/signup/check-username',
};

/* 커뮤니티 API */
export const TIP_API = {
    ...createCommunityEndpoint('/community/tips', 'comments'),
    LIKE: (postId: number) => `/community/tips/${postId}/like`
};
export const QNA_API = {
    ...createCommunityEndpoint('/community/qnas', 'answers'),
    ACCEPT: (postId: number, answerId: number) => `/community/qnas/${postId}/answers/${answerId}/accept`,
    LIKE: (postId: number, answerId: number) => `/community/qnas/${postId}/answers/${answerId}/like`
};

export const DISCUSSION_API = {
    ...createCommunityEndpoint('/community/discussions', 'messages'),
    JOIN: (roomId: number) => `/community/discussions/${roomId}/join`,

    /* 소켓 API */
    WS_LIKE: (roomId: number, messageId: number) => `/app/community/discussions/${roomId}/messages/${messageId}/like`,
    WS_ERROR: '/user/queue/errors'
};

export const CHALLENGE_API = {
    BASE: '/community/challenges',
    MINE: '/community/challenges/mine',
    DETAIL: (id: number) => `/community/challenges/${id}`,
    JOIN: (id: number) => `/community/challenges/${id}/join`,
    PROGRESS: (id: number) => `/community/challenges/${id}/progress`,
    FEEDS: (id: number) => `/community/challenges/${id}/feeds`,
    FEED_DETAIL: (feedId: number) => `/community/challenges/feeds/${feedId}`,
};