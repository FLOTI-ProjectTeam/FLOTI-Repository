export const ERROR_MESSAGES: Record<string, string> = {
    // Auth
    'USER_NOT_FOUND': '사용자를 찾을 수 없습니다.',
    'LOGIN_FAILED': '아이디 또는 비밀번호가 일치하지 않습니다.',
    'UNAUTHORIZED': '로그인이 필요합니다.',
    'FORBIDDEN': '권한이 없습니다.',

    // Community
    'POST_NOT_FOUND': '게시글을 찾을 수 없습니다.',
    'ALREADY_JOINED': '이미 참여 중인 챌린지입니다.',
    'FULL_PARTICIPANTS': '참여 인원이 마감되었습니다.',
    'NOT_PARTICIPANT': '챌린지 참여자가 아닙니다.',

    // Default
    'DEFAULT': '일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요.'
};

export const getErrorMessage = (error: any): string => {
    // 1. Direct String Error
    if (typeof error === 'string') return error;

    const data = error?.response?.data;

    // 2. data is string (e.g. error message from backend)
    if (typeof data === 'string') return data;

    // 3. Backend Custom Error Format (assuming { message: "..." } or { code: "..." })
    if (data?.message) {
        // Map known backend messages if codes are used, otherwise return message
        return ERROR_MESSAGES[data.message] || data.message;
    }

    // 4. HTTP Status Fallback
    const status = error?.response?.status;
    if (status === 401) return ERROR_MESSAGES['UNAUTHORIZED'];
    if (status === 403) return ERROR_MESSAGES['FORBIDDEN'];
    if (status === 404) return '요청한 정보를 찾을 수 없습니다.';
    if (status === 500) return '서버 내부 오류가 발생했습니다.';

    return ERROR_MESSAGES['DEFAULT'];
};
