// 커뮤니티 엔드포인트 생성
export const createCommunityEndpoint = (base: string, entity: string) => ({
    BASE: base,
    BASE_DETAIL: (baseId: number) => `${base}/${baseId}`,
    ENTITY_BASE: (baseId: number) => `${base}/${baseId}/${entity}`,
    ENTITY_DETAIL: (baseId: number, entityId: number) => `${base}/${baseId}/${entity}/${entityId}`,
});