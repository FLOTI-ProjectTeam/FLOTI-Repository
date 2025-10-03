import apiClient from './apiClient';
import { AUTH_API } from '@/constants/endpoints';
import { SignUpRequest, EmailRequest, CodeVerifyRequest, UsernameCheckRequest } from '@/types/auth/signup';
import { LoginRequest, LoginResponse } from '@/types/auth/login';

// 1. 회원가입
export const signup = (data: SignUpRequest) => 
  apiClient.post(AUTH_API.SIGNUP, data);

// 2. 회원가입 코드 전송·검증
export const sendSignupCode = (data: EmailRequest) => 
  apiClient.post(AUTH_API.SIGNUP_SEND_CODE, data);
export const verifySignupCode = (data: CodeVerifyRequest) => 
  apiClient.post(AUTH_API.SIGNUP_VERIFY_CODE, data);

// 3. 아이디 중복 확인
export const checkUsername = (data: UsernameCheckRequest) => 
  apiClient.post(AUTH_API.SIGNUP_CHECK_USERNAME, data);

// 4. 로그인
export const login = (data: LoginRequest) => 
  apiClient.post<LoginResponse>(AUTH_API.LOGIN, data);