import apiClient from './apiClient';
import { SignUpRequest, EmailRequest, CodeVerifyRequest, UsernameCheckRequest } from '@/types/auth/signup';
import { LoginRequest, LoginResponse } from '@/types/auth/login';

// 1. 회원가입
export const signup = (data: SignUpRequest) => 
  apiClient.post('/auth/signup', data);
export const sendSignupCode = (data: EmailRequest) => 
  apiClient.post('/auth/signup/send-code', data);
export const verifySignupCode = (data: CodeVerifyRequest) => 
  apiClient.post('/auth/signup/verify-code', data);
export const checkUsername = (data: UsernameCheckRequest) => 
  apiClient.post('/auth/signup/check-username', data);

// 2. 로그인
export const login = (data: LoginRequest) => 
  apiClient.post<LoginResponse>('/auth/login', data);