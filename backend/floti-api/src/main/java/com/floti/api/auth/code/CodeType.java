package com.floti.api.auth.code;

/**
 * 인증 코드의 용도를 구분하기 위한 열거형(enum).
 *
 * - SIGNUP: 회원가입 시 이메일 인증코드
 * - RESET_PASSWORD: 비밀번호 재설정 시 이메일 인증코드
 * - FIND_USERNAME: 아이디 찾기 시 이메일 인증코드
 *
 * enum은 상수(바뀌지 않는 값)를 모아둔 클래스예요.
 */
public enum CodeType {
    SIGNUP,         // 회원가입 코드
    RESET_PASSWORD, // 비밀번호 재설정 코드
    FIND_USERNAME   // 아이디 찾기 코드
}

