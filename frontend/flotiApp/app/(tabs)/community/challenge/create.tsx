import { View, ScrollView, StyleSheet, TouchableOpacity, Text, Alert, ActivityIndicator, Platform } from 'react-native';
import { useState } from 'react';

import { useRouter } from 'expo-router';
import { useNavigation } from '@/hooks/useNavigation';
import { createChallengePost } from '@/api/community/challengeApi';
import { ChallengeRequest } from '@/types/community/challenge';
import { getErrorMessage } from '@/utils/errorMapping';
import { userStorage, tokenStorage } from '@/utils/storage';

import ChallengeInputView from '@/components/feature/community/challenge/ChallengeInputView';
import { EditorHeader } from '@/components/ui/Header';
import { IconSymbol } from '@/components/ui/IconSymbol';
import { STYLE } from '@/constants/styles';
import COLOR from '@/constants/colors';

export default function ChallengeCreateScreen() {
    const router = useRouter();
    const [isLoading, setIsLoading] = useState(false);
    const [request, setRequest] = useState({
        title: '',
        content: '',
        detail: '',
        startDate: '',
        endDate: '',
        maxParticipants: 10,
        tags: ''
    });

    const handleBack = () => {
        router.navigate('/community/challenge');
    };

    const showAlert = (title: string, message: string, onPress?: () => void) => {
        if (Platform.OS === 'web') {
            window.alert(`${title}\n${message}`);
            if (onPress) onPress();
        } else {
            Alert.alert(title, message, onPress ? [{ text: '확인', onPress }] : undefined);
        }
    };

    const handleSubmit = async () => {
        console.log('Submit clicked', request);

        if (!request.title || !request.content || !request.startDate || !request.endDate) {
            showAlert('알림', '필수 정보를 모두 입력해주세요.');
            return;
        }

        // [DEBUG] Check Token visibly
        const AsyncStorage = require('@react-native-async-storage/async-storage').default;
        const debugToken = await AsyncStorage.getItem('jwt');
        // alert(`[Debug] Token Check:\n${debugToken ? debugToken.substring(0, 10) + '...' : 'NULL'}`); 
        // Uncomment above if needed, but console should suffice if verified.
        // Actually, user said console is empty. Let's use showAlert.
        // showAlert('Debug Token', debugToken ? 'Exists: ' + debugToken.substring(0, 20) : 'NULL');
        // Let's rely on the requested alert.
        console.log('[Create] Token:', debugToken);

        setIsLoading(true);
        try {
            const formatToISO = (dateStr: string) => {
                // Input: YYYY.MM.DD -> Output: YYYY-MM-DDTHH:mm:ss
                return dateStr.replace(/\./g, '-') + 'T00:00:00';
            };

            const payload: ChallengeRequest = {
                title: request.title,
                intro: request.content, // Input 'content' is the short intro
                content: request.detail, // Input 'detail' is the actual content
                startDate: formatToISO(request.startDate),
                endDate: formatToISO(request.endDate),
                maxParticipants: request.maxParticipants
            };

            await createChallengePost(payload);
            setIsLoading(false);
            showAlert('성공', '챌린지가 등록되었습니다!', handleBack);
        } catch (e: any) {
            console.error('Create Challenge Error:', e);
            setIsLoading(false);

            const msg = getErrorMessage(e);

            // [Fix] Handle 403 (Token Invalid/Expired) -> Force Logout to reset state
            if (e.response?.status === 403) {
                showAlert('세션 만료', '로그인 정보가 만료되었습니다. 다시 로그인해주세요.', async () => {
                    await userStorage.removeUser();
                    await tokenStorage.removeToken();
                    // Clear the specific JWT key used by apiClient
                    const AsyncStorage = require('@react-native-async-storage/async-storage').default;
                    await AsyncStorage.removeItem('jwt');

                    router.dismissAll();
                    router.replace('/');
                });
                return;
            }

            showAlert('실패', msg);
        }
    };

    return (
        <View style={STYLE.BASE_CONTAINER}>
            {/* Custom Header */}
            <View style={{
                flexDirection: 'row',
                alignItems: 'center',
                justifyContent: 'space-between',
                paddingHorizontal: 16,
                paddingVertical: 12,
                borderBottomWidth: 1,
                borderBottomColor: '#F0F0F0',
                backgroundColor: 'white'
            }}>
                <TouchableOpacity onPress={handleBack} style={{ padding: 4 }}>
                    <IconSymbol name="chevron.left" size={24} color={COLOR.TEXT.GRAY_DARK} />
                </TouchableOpacity>
                <Text style={{ fontSize: 18, fontWeight: '700', color: COLOR.TEXT.GRAY_DARK }}>챌린지 등록</Text>
                <TouchableOpacity onPress={handleSubmit} style={{ padding: 4 }} disabled={isLoading}>
                    {isLoading ? (
                        <ActivityIndicator size="small" color={COLOR.TEXT.NAVY} />
                    ) : (
                        <Text style={{ fontSize: 16, fontWeight: '600', color: COLOR.TEXT.NAVY }}>등록</Text>
                    )}
                </TouchableOpacity>
            </View>
            <ScrollView style={STYLE.CONTENT_CONTAINER} contentContainerStyle={{ padding: 16, paddingBottom: 40 }}>
                <ChallengeInputView
                    title={request.title}
                    content={request.content}
                    detail={request.detail}
                    startDate={request.startDate}
                    endDate={request.endDate}
                    maxParticipants={request.maxParticipants}
                    tags={request.tags}
                    onChangeTitle={(v) => setRequest(p => ({ ...p, title: v }))}
                    onChangeContent={(v) => setRequest(p => ({ ...p, content: v }))}
                    onChangeDetail={(v) => setRequest(p => ({ ...p, detail: v }))}
                    onChangeStartDate={(v) => setRequest(p => ({ ...p, startDate: v }))}
                    onChangeEndDate={(v) => setRequest(p => ({ ...p, endDate: v }))}
                    onChangeMaxParticipants={(v) => setRequest(p => ({ ...p, maxParticipants: v }))}
                    onChangeTags={(v) => setRequest(p => ({ ...p, tags: v }))}
                />
            </ScrollView>
        </View>
    );
}

const styles = StyleSheet.create({
    header: { alignItems: 'center', marginBottom: 20 },
    headerTitle: { fontSize: 18, fontWeight: '700', color: COLOR.TEXT.GRAY_DARK }
});
