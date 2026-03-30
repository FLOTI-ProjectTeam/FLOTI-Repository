import React, { useState } from 'react';
import { View, Text, TextInput, TouchableOpacity, StyleSheet, KeyboardAvoidingView, Platform, ScrollView, Alert } from 'react-native';
import { useRouter } from 'expo-router';
import { useSafeAreaInsets } from 'react-native-safe-area-context';

import { signup, login, checkUsername, sendSignupCode, verifySignupCode } from '@/api/authApi';
import { useUser } from '@/contexts/UserContext';
import { IconSymbol } from '@/components/ui/IconSymbol';
import COLOR from '@/constants/colors';
import { STYLE, SHADOW } from '@/constants/styles';

export default function SignupScreen() {
    const router = useRouter();
    const insets = useSafeAreaInsets();
    const { setUsername: setContextUsername } = useUser();

    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [email, setEmail] = useState('');
    const [nickname, setNickname] = useState('');

    // 이메일 인증 관련 상태
    const [verificationCode, setVerificationCode] = useState('');
    const [isCodeSent, setIsCodeSent] = useState(false);
    const [isEmailVerified, setIsEmailVerified] = useState(false);
    const [verificationMessage, setVerificationMessage] = useState<{ text: string; type: 'success' | 'error' } | null>(null);
    const [successMessage, setSuccessMessage] = useState('');

    // 중복 확인 관련 상태
    const [isUsernameChecked, setIsUsernameChecked] = useState(false);
    const [usernameMessage, setUsernameMessage] = useState<{ text: string; type: 'success' | 'error' } | null>(null);

    // Form Validation Errors
    const [passwordError, setPasswordError] = useState('');
    const [emailError, setEmailError] = useState('');
    const [generalError, setGeneralError] = useState('');

    const handleCheckUsername = async () => {
        if (!username) {
            setUsernameMessage({ text: '아이디를 입력해주세요.', type: 'error' });
            return;
        }
        try {
            await checkUsername({ username });
            setIsUsernameChecked(true);
            setUsernameMessage({ text: '사용 가능한 아이디입니다.', type: 'success' });
        } catch (error: any) {
            console.error(error);
            const msg = error.response?.data?.message || '이미 사용 중인 아이디입니다.';
            setUsernameMessage({ text: msg, type: 'error' });
            setIsUsernameChecked(false);
        }
    };

    const handleSendCode = async () => {
        if (!email) {
            setEmailError('이메일을 입력해주세요.');
            return;
        }
        try {
            await sendSignupCode({ email });
            setIsCodeSent(true);
            setEmailError(''); // Clear error
            Alert.alert('전송 완료', '인증 코드가 전송되었습니다. (백엔드 콘솔 확인)');
        } catch (error: any) {
            console.error(error);
            setEmailError('인증 코드 전송에 실패했습니다.');
        }
    };

    const handleVerifyCode = async () => {
        setVerificationMessage(null);
        if (!verificationCode) {
            setVerificationMessage({ text: '인증 코드를 입력해주세요.', type: 'error' });
            return;
        }
        try {
            await verifySignupCode({ email, code: verificationCode });
            setIsEmailVerified(true);
            setIsCodeSent(false); // Hide input on success or keep it disabled
            setVerificationMessage({ text: '이메일 인증이 완료되었습니다.', type: 'success' });
            setEmailError(''); // Clear any email errors
        } catch (error: any) {
            console.error(error);
            setVerificationMessage({ text: '인증 코드가 올바르지 않습니다.', type: 'error' });
        }
    };

    const handleSignup = async () => {
        // Reset errors
        setPasswordError('');
        setGeneralError('');

        // 유효성 검사
        if (!username || !password || !nickname || !email) {
            setGeneralError('모든 필드를 입력해주세요.');
            return;
        }
        if (!isUsernameChecked) {
            setUsernameMessage({ text: '아이디 중복 확인을 해주세요.', type: 'error' });
            setGeneralError('아이디 중복 확인을 완료해주세요.');
            return;
        }
        if (!isEmailVerified) {
            setEmailError('이메일 인증을 완료해주세요.');
            setGeneralError('이메일 인증을 완료해주세요.');
            return;
        }
        if (password.length < 8) {
            setPasswordError('비밀번호는 최소 8자 이상이어야 합니다.');
            setGeneralError('비밀번호 길이를 확인해주세요.');
            return;
        }
        if (password !== confirmPassword) {
            setPasswordError('비밀번호가 일치하지 않습니다.');
            setGeneralError('비밀번호가 일치하지 않습니다.');
            return;
        }
        if (username.length > 10) {
            setGeneralError('아이디는 10자 이하만 가능합니다.');
            return;
        }
        if (nickname.length > 15) {
            setGeneralError('닉네임은 15자 이하만 가능합니다.');
            return;
        }

        try {
            await signup({ username, password, email, nickname });
            
            // 자동 로그인 진행
            setSuccessMessage('회원가입이 완료되었습니다! 자동 로그인 중입니다...');
            
            try {
                const response = await login({ username, password });
                if (response && response.username) {
                    setContextUsername(response.username);
                    router.replace('/(tabs)');
                } else {
                    router.back(); // 로그인 실패 시 수동 로그인 창으로
                }
            } catch (autoLoginError) {
                router.back();
            }

        } catch (error: any) {
            console.error(error);
            const msg = error.response?.data?.message || '회원가입에 실패했습니다.';
            setGeneralError(msg);
        }
    };

    return (
        <View style={[styles.container, { paddingTop: insets.top }]}>
            <View style={styles.header}>
                <TouchableOpacity onPress={() => router.back()} style={styles.backButton}>
                    <IconSymbol name="chevron.left" size={28} color="#333" />
                </TouchableOpacity>
                <Text style={styles.headerTitle}>회원가입</Text>
                <View style={{ width: 28 }} />
            </View>

            <KeyboardAvoidingView
                behavior={Platform.OS === 'ios' ? 'padding' : undefined}
                style={{ flex: 1 }}
            >
                <ScrollView contentContainerStyle={styles.scrollContent} keyboardShouldPersistTaps="handled">

                    {/* 아이디 */}
                    <View style={styles.inputGroup}>
                        <Text style={styles.label}>아이디</Text>
                        <View style={styles.row}>
                            <TextInput
                                style={[styles.input, { flex: 1 }, usernameMessage?.type === 'error' && styles.errorBorder, usernameMessage?.type === 'success' && styles.successBorder]}
                                placeholder="아이디 입력"
                                value={username}
                                onChangeText={(t) => {
                                    setUsername(t);
                                    setIsUsernameChecked(false);
                                    setUsernameMessage(null); // Clear message on edit
                                }}

                                autoCapitalize="none"
                                maxLength={10}
                            />
                            <TouchableOpacity
                                style={[styles.smallButton, isUsernameChecked ? styles.checkedButton : {}]}
                                onPress={handleCheckUsername}
                            >
                                <Text style={styles.smallButtonText}>
                                    {isUsernameChecked ? '확인됨' : '중복 확인'}
                                </Text>
                            </TouchableOpacity>
                        </View>
                        {usernameMessage ? (
                            <Text style={[styles.helperText, usernameMessage.type === 'success' ? styles.successText : styles.errorText]}>
                                {usernameMessage.text}
                            </Text>
                        ) : (
                            <Text style={styles.helperText}>* 최대 10자까지 입력 가능합니다.</Text>
                        )}
                    </View>

                    {/* 이메일 */}
                    <View style={styles.inputGroup}>
                        <Text style={styles.label}>이메일</Text>
                        <View style={styles.row}>
                            <TextInput
                                style={[styles.input, { flex: 1 }, emailError ? styles.errorBorder : null]}
                                placeholder="example@email.com"
                                value={email}
                                onChangeText={(t) => {
                                    setEmail(t);
                                    setIsEmailVerified(false);
                                    setIsCodeSent(false);
                                    setEmailError('');
                                }}
                                autoCapitalize="none"
                                keyboardType="email-address"
                                editable={!isEmailVerified}
                            />
                            <TouchableOpacity
                                style={[styles.smallButton, isEmailVerified ? styles.checkedButton : {}]}
                                onPress={handleSendCode}
                                disabled={isEmailVerified}
                            >
                                <Text style={styles.smallButtonText}>
                                    {isEmailVerified ? '인증됨' : '인증번호 전송'}
                                </Text>
                            </TouchableOpacity>
                        </View>
                        {emailError ? <Text style={styles.errorText}>{emailError}</Text> : null}
                    </View>

                    {/* 인증 코드 입력 (전송 후 표시) */}
                    {(isCodeSent || isEmailVerified) && (
                        <View style={[styles.inputGroup, { marginTop: -10 }]}>
                            <View style={styles.row}>
                                <TextInput
                                    style={[styles.input, { flex: 1 }, verificationMessage?.type === 'error' && styles.errorBorder, verificationMessage?.type === 'success' && styles.successBorder]}
                                    placeholder="인증번호 입력"
                                    value={verificationCode}
                                    onChangeText={(t) => { setVerificationCode(t); setVerificationMessage(null); }}
                                    keyboardType="number-pad"
                                    editable={!isEmailVerified}
                                />
                                <TouchableOpacity
                                    style={[styles.smallButton, isEmailVerified ? styles.checkedButton : {}]}
                                    onPress={handleVerifyCode}
                                    disabled={isEmailVerified}
                                >
                                    <Text style={styles.smallButtonText}>
                                        {isEmailVerified ? '확인됨' : '확인'}
                                    </Text>
                                </TouchableOpacity>
                            </View>
                            {/* 안내 메시지 / 에러 메시지 */}
                            {verificationMessage ? (
                                <Text style={[styles.helperText, verificationMessage.type === 'success' ? styles.successText : styles.errorText]}>
                                    {verificationMessage.text}
                                </Text>
                            ) : (
                                !isEmailVerified && <Text style={styles.helperText}>* 백엔드 콘솔 로그에서 코드를 확인해주세요.</Text>
                            )}
                        </View>
                    )}

                    {/* 비밀번호 */}
                    <View style={styles.inputGroup}>
                        <Text style={styles.label}>비밀번호</Text>
                        <TextInput
                            style={styles.input}
                            placeholder="비밀번호 입력"
                            secureTextEntry
                            value={password}
                            onChangeText={(t) => { setPassword(t); setPasswordError(''); }}
                        />
                        <Text style={styles.helperText}>* 최소 8자 이상 입력해주세요.</Text>
                    </View>

                    <View style={styles.inputGroup}>
                        <Text style={styles.label}>비밀번호 확인</Text>
                        <TextInput
                            style={[styles.input, passwordError ? styles.errorBorder : null]}
                            placeholder="비밀번호 재입력"
                            secureTextEntry
                            value={confirmPassword}
                            onChangeText={(t) => { setConfirmPassword(t); setPasswordError(''); }}
                        />
                        {(password !== confirmPassword && confirmPassword.length > 0) && (
                            <Text style={styles.errorText}>비밀번호가 일치하지 않습니다.</Text>
                        )}
                        {passwordError ? <Text style={styles.errorText}>{passwordError}</Text> : null}
                    </View>

                    {/* 닉네임 */}
                    <View style={styles.inputGroup}>
                        <Text style={styles.label}>닉네임</Text>
                        <TextInput
                            style={styles.input}
                            placeholder="활동명 입력"
                            value={nickname}
                            onChangeText={setNickname}
                            maxLength={15}
                        />
                        <Text style={styles.helperText}>* 최대 15자까지 입력 가능합니다.</Text>
                    </View>

                    {/* Success Message */}
                    {successMessage ? (
                         <Text style={[styles.successText, { textAlign: 'center', marginBottom: 10, fontSize: 16 }]}>
                            {successMessage}
                        </Text>
                    ) : null}

                    {/* General Error Message */}
                    {generalError ? (
                        <Text style={[styles.errorText, { textAlign: 'center', marginBottom: 10 }]}>
                            {generalError}
                        </Text>
                    ) : null}

                    {/* 가입 완료 버튼 */}
                    <TouchableOpacity
                        style={[styles.signupButton]}
                        onPress={handleSignup}
                    >
                        <Text style={styles.signupButtonText}>회원가입 완료</Text>
                    </TouchableOpacity>

                </ScrollView>
            </KeyboardAvoidingView>
        </View>
    );
}

const styles = StyleSheet.create({
    container: { flex: 1, backgroundColor: 'white' },
    header: { flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between', padding: 16, borderBottomWidth: 1, borderBottomColor: '#F0F0F0' },
    backButton: { padding: 4 },
    headerTitle: { fontSize: 18, fontWeight: '700', color: '#333' },

    scrollContent: { padding: 24, paddingBottom: 50 },

    inputGroup: { marginBottom: 20 },
    label: { fontSize: 14, fontWeight: '600', color: '#444', marginBottom: 8, marginLeft: 4 },
    row: { flexDirection: 'row', gap: 10 },
    input: {
        height: 50, backgroundColor: '#F7F8FA', borderRadius: 12, paddingHorizontal: 16,
        fontSize: 16, color: '#333', borderWidth: 1, borderColor: '#E0E0E0'
    },

    smallButton: {
        width: 100, height: 50, backgroundColor: '#666', borderRadius: 12,
        justifyContent: 'center', alignItems: 'center'
    },
    checkedButton: {
        backgroundColor: '#53C3A6'
    },
    smallButtonText: { color: 'white', fontWeight: '600', fontSize: 14 },

    signupButton: {
        height: 56, backgroundColor: '#53C3A6', borderRadius: 12,
        justifyContent: 'center', alignItems: 'center', marginTop: 20,
        ...SHADOW
    },
    signupButtonText: { fontSize: 18, fontWeight: 'bold', color: 'white' },

    helperText: { fontSize: 12, color: '#888', marginTop: 6, marginLeft: 4 },
    errorText: { fontSize: 12, color: 'tomato', marginTop: 6, marginLeft: 4 },
    successText: { fontSize: 12, color: '#4CAF50', marginTop: 6, marginLeft: 4 },
    errorBorder: { borderColor: 'tomato', borderWidth: 1 },
    successBorder: { borderColor: '#4CAF50', borderWidth: 1 },
});
