import apiClient from '@/api/apiClient';
import { AUTH_API } from '@/constants/endpoints';
import { userStorage } from '@/utils/storage';
import { SignUpRequest, EmailRequest, CodeVerifyRequest, UsernameCheckRequest } from '@/types/auth/signup';
import { LoginRequest, LoginResponse } from '@/types/auth/login';

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
// 로그인
export const login = async (data: LoginRequest) => {
  // [Fix] Backend returns wrapped ApiResponse: { data: { jwt, nickname, refreshToken }, message: ... }
  // We need to unwrap 'data' and map 'nickname' -> 'username'
  const response = await apiClient.post<any>(AUTH_API.LOGIN, data);

  const backendData = response.data.data; // Unwrap 'data'

  const mappedUser: LoginResponse = {
    jwt: backendData.jwt,
    username: backendData.nickname, // Map nickname to username
    refreshToken: backendData.refreshToken
  };

  console.log('[Auth] Login Success. Token:', mappedUser.jwt?.substring(0, 10) + '...');

  await userStorage.setUser(mappedUser);

  // [Fix] Store token specifically for apiClient to read
  const AsyncStorage = require('@react-native-async-storage/async-storage').default;
  if (mappedUser.jwt) {
    await AsyncStorage.setItem('jwt', mappedUser.jwt);
  } else {
    console.error('[Auth] Login Warning: No JWT in response!');
  }

  return mappedUser;
};