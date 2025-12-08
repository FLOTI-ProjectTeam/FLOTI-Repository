import apiClient from '@/api/apiClient';
import { SignUpRequest, EmailRequest, CodeVerifyRequest, UsernameCheckRequest } from '@/types/auth/signup';
import { LoginRequest, LoginResponse } from '@/types/auth/login';

import { userStorage } from '@/utils/storage';
import { AUTH_API } from '@/constants/endpoints';

// 회원가입
export const signup = (data: SignUpRequest) => 
  apiClient.post(AUTH_API.SIGNUP, data);

// 회원가입 코드 전송·검증
export const sendSignupCode = (data: EmailRequest) => 
  apiClient.post(AUTH_API.SIGNUP_SEND_CODE, data);
export const verifySignupCode = (data: CodeVerifyRequest) => 
  apiClient.post(AUTH_API.SIGNUP_VERIFY_CODE, data);

// 아이디 중복 확인
export const checkUsername = (data: UsernameCheckRequest) => 
  apiClient.post(AUTH_API.SIGNUP_CHECK_USERNAME, data);

// 로그인
export const login = async (data: LoginRequest) => {
  const response = await apiClient.post<LoginResponse>(AUTH_API.LOGIN, data);
  await userStorage.setUser(response.data);
  return response.data;
};