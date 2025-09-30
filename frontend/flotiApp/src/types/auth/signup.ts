export interface SignUpRequest {
    email: string;
    password: string;
    username: string;
    nickname: string;
}

export interface EmailRequest {
    email: string;
}

export interface CodeVerifyRequest {
    code: string;
    email: string;
}

export interface UsernameCheckRequest {
    username: string;
}