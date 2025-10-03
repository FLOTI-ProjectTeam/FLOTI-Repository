// 커뮤니티 엔드포인트 생성
export const createCommunityEndpoint = (base: string, entity: string) => ({
    POST_BASE: base,
    POST_DETAIL: (postId: number) => `${base}/${postId}`,
    ENTITY_BASE: (postId: number) => `${base}/${postId}/${entity}`,
    ENTITY_DETAIL: (entityId: number) => `${base}/${entity}/${entityId}`,
});