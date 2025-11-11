import { createCommunityEndpoint } from '@/utils/apiHelpers';

/* 서버 주소 */
export const BASE_URL = 'http://localhost:8080';

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

    /* WebSocket API */
    WS_SUBSCRIBE: (roomId: number) => `/topic/community/discussions/${roomId}/messages`,
    WS_SEND: (roomId: number) => `/app/community/discussions/${roomId}/messages`,
    WS_DELETE: (roomId: number, messageId: number) => `/app/community/discussions/${roomId}/messages/${messageId}`,
    WS_LIKE: (roomId: number, messageId: number) => `/app/community/discussions/${roomId}/messages/${messageId}/like`,
    WS_ERROR: '/user/queue/errors'
};