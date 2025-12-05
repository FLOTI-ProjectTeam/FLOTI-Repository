export interface Page<T> {
    content: T[];
    totalElements: number;
    totalPages: number;
    size: number;
    number: number;
    first: boolean;
    last: boolean;
    empty: boolean;
}
export interface UserResponse {
    username: string;
    nickname: string;
    profileImage: string | null;
}

export interface PostRequest {
    title: string;
    content: string;
}

export interface ErrorMessage {
    message: string;
    code: number;
}

export interface LikeResponse {
    id: number;
    liked: boolean;
    likeCount: number;
}