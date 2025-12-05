export interface PostRequest {
    title: string;
    content: string;
}

export interface AuthorResponse {
    id: number;
    nickname: string;
    profileImage: string;
}