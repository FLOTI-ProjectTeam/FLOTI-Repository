import { createCommunityEndpoint } from '@/api/endpointHelpers';

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
    ...createCommunityEndpoint('/community/tips', 'comments')
};
export const QNA_API = {
    ...createCommunityEndpoint('/community/qnas', 'answers'),
    ACCEPT_ENTITY: (postId: number, answerId: number) => `/community/qnas/${postId}/answers/${answerId}/accept`
};