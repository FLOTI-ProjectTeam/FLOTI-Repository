export interface AuthorResponse {
    id: number;
    nickname: string;
    profileImage: string;
}

export interface PostRequest {
    title: string;
    content: string;
}

export interface ErrorMessage {
    message: string;
    code: number;
}