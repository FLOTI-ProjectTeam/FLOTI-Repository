export interface LoginRequest {
    username: string;
    password: string;
}
  
export interface LoginResponse {
    jwt: string;
    username: string;
    refreshToken: string;
}