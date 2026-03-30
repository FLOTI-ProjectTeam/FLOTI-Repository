import React, { useState } from 'react';
import { View, Text, TextInput, TouchableOpacity, StyleSheet, KeyboardAvoidingView, Platform, ScrollView, Alert } from 'react-native';
import { useRouter } from 'expo-router';
import { useSafeAreaInsets } from 'react-native-safe-area-context';

import { login } from '@/api/authApi';
import { useUser } from '@/contexts/UserContext';
import { IconSymbol } from '@/components/ui/IconSymbol';
import COLOR from '@/constants/colors';
import { STYLE, SHADOW } from '@/constants/styles';

export default function LoginScreen() {
    const router = useRouter();
    const insets = useSafeAreaInsets();
    const { setUsername } = useUser();

    const [usernameInput, setUsernameInput] = useState('');
    const [password, setPassword] = useState('');
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [loginError, setLoginError] = useState('');

    const handleLogin = async () => {
        setLoginError(''); // Reset previous errors

        if (!usernameInput || !password) {
            setLoginError('아이디와 비밀번호를 모두 입력해주세요.');
            return;
        }

        try {
            setIsSubmitting(true);
            const response = await login({ username: usernameInput, password });

            // 로그인 성공 시 Context 업데이트
            if (response && response.username) {
                console.log('[LoginScreen] Success:', response.username);
                setUsername(response.username);
                // 메인 화면으로 이동 (replace를 써서 뒤로가기 방지)
                router.replace('/(tabs)');
            }
        } catch (error: any) {
            console.error('[LoginScreen] Error:', error);
            const msg = error.response?.data?.message || '로그인에 실패했습니다. 아이디와 비밀번호를 확인해주세요.';
            setLoginError(msg);
        } finally {
            setIsSubmitting(false);
        }
    };

    const handleGoToSignup = () => {
        router.push('/auth/signup');
    };

    return (
        <View style={[styles.container, { paddingTop: insets.top }]}>
            <KeyboardAvoidingView
                behavior={Platform.OS === 'ios' ? 'padding' : undefined}
                style={{ flex: 1 }}
            >
                <ScrollView contentContainerStyle={styles.scrollContent} keyboardShouldPersistTaps="handled">

                    {/* 로고 영역 */}
                    <View style={styles.logoContainer}>
                        <View style={styles.logoCircle}>
                            <IconSymbol name="leaf.fill" size={40} color="white" />
                        </View>
                        <Text style={styles.appName}>FLOTI</Text>
                        <Text style={styles.appSlogan}>나만의 플로팅 라이프, 플로티</Text>
                    </View>

                    {/* 입력 폼 */}
                    <View style={styles.formContainer}>
                        <View style={styles.inputGroup}>
                            <Text style={styles.label}>아이디</Text>
                            <TextInput
                                style={[styles.input, loginError ? styles.errorBorder : null]}
                                placeholder="아이디를 입력하세요"
                                placeholderTextColor="#999"
                                value={usernameInput}
                                onChangeText={(t) => { setUsernameInput(t); setLoginError(''); }}
                                autoCapitalize="none"
                            />
                        </View>

                        <View style={styles.inputGroup}>
                            <Text style={styles.label}>비밀번호</Text>
                            <TextInput
                                style={[styles.input, loginError ? styles.errorBorder : null]}
                                placeholder="비밀번호를 입력하세요"
                                placeholderTextColor="#999"
                                value={password}
                                onChangeText={(t) => { setPassword(t); setLoginError(''); }}
                                secureTextEntry
                                autoCapitalize="none"
                            />
                        </View>

                        {loginError ? (
                            <Text style={styles.errorText}>{loginError}</Text>
                        ) : null}

                        <TouchableOpacity
                            style={[styles.loginButton, isSubmitting && styles.disabledButton]}
                            onPress={handleLogin}
                            disabled={isSubmitting}
                        >
                            <Text style={styles.loginButtonText}>
                                {isSubmitting ? '로그인 중...' : '로그인'}
                            </Text>
                        </TouchableOpacity>

                        <View style={styles.footerLinks}>
                            <TouchableOpacity onPress={() => Alert.alert('준비 중', '비밀번호 찾기 기능은 아직 준비 중입니다.')}>
                                <Text style={styles.linkText}>비밀번호 찾기</Text>
                            </TouchableOpacity>
                            <Text style={styles.divider}>|</Text>
                            <TouchableOpacity onPress={handleGoToSignup}>
                                <Text style={styles.signupText}>회원가입</Text>
                            </TouchableOpacity>
                        </View>
                    </View>

                </ScrollView>
            </KeyboardAvoidingView>
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: 'white',
    },
    scrollContent: {
        flexGrow: 1,
        justifyContent: 'center',
        paddingHorizontal: 30,
        paddingBottom: 50,
    },
    logoContainer: {
        alignItems: 'center',
        marginBottom: 50,
    },
    logoCircle: {
        width: 80,
        height: 80,
        borderRadius: 40,
        backgroundColor: '#53C3A6', // FLOTI Main Color
        justifyContent: 'center',
        alignItems: 'center',
        marginBottom: 16,
        ...SHADOW,
    },
    appName: {
        fontSize: 32,
        fontFamily: Platform.OS === 'ios' ? 'System' : 'Roboto',
        fontWeight: '800',
        color: '#333',
        letterSpacing: 2,
    },
    appSlogan: {
        fontSize: 14,
        color: '#666',
        marginTop: 8,
    },
    formContainer: {
        width: '100%',
    },
    inputGroup: {
        marginBottom: 20,
    },
    label: {
        fontSize: 14,
        fontWeight: '600',
        color: '#444',
        marginBottom: 8,
        marginLeft: 4,
    },
    input: {
        height: 52,
        backgroundColor: '#F7F8FA',
        borderRadius: 12,
        paddingHorizontal: 16,
        fontSize: 16,
        color: '#333',
        borderWidth: 1,
        borderColor: '#E0E0E0',
    },
    loginButton: {
        height: 56,
        backgroundColor: '#53C3A6',
        borderRadius: 12,
        justifyContent: 'center',
        alignItems: 'center',
        marginTop: 10,
        ...SHADOW,
    },
    disabledButton: {
        backgroundColor: '#A0DBC9',
    },
    loginButtonText: {
        fontSize: 18,
        fontWeight: 'bold',
        color: 'white',
    },
    footerLinks: {
        flexDirection: 'row',
        justifyContent: 'center',
        marginTop: 24,
        alignItems: 'center',
    },
    linkText: {
        fontSize: 14,
        color: '#666',
    },
    divider: {
        fontSize: 14,
        color: '#DDD',
        marginHorizontal: 12,
    },
    signupText: {
        fontSize: 14,
        color: '#53C3A6',
        fontWeight: '700',
    },
    errorBorder: {
        borderColor: 'tomato',
        borderWidth: 1,
    },
    errorText: {
        color: 'tomato',
        fontSize: 14,
        textAlign: 'center',
        marginBottom: 10,
    }
});
