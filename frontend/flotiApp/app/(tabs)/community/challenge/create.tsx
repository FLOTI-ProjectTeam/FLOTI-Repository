import { View, ScrollView, StyleSheet, TouchableOpacity, Text, Alert } from 'react-native';
import { useState } from 'react';

import { useNavigation } from '@/hooks/useNavigation';
import { createChallengePost } from '@/api/community/challengeApi';
import { ChallengeRequest } from '@/types/community/challenge';

import ChallengeInputView from '@/components/feature/community/challenge/ChallengeInputView';
import { EditorHeader } from '@/components/ui/Header';
import { STYLE } from '@/constants/styles';
import COLOR from '@/constants/colors';

export default function ChallengeCreateScreen() {
    const { goBackSafely } = useNavigation();
    const [request, setRequest] = useState({
        title: '',
        content: '', // 한줄 소개 (description 역할)
        detail: '', // 상세 내용 (content 역할)
        startDate: '',
        endDate: '',
        maxParticipants: 10,
        tags: ''
    });

    const handleSubmit = async () => {
        if (!request.title || !request.content || !request.startDate || !request.endDate) {
            Alert.alert('알림', '필수 정보를 모두 입력해주세요.');
            return;
        }

        try {
            const payload: ChallengeRequest = {
                title: request.title,
                description: request.content, // 한줄 소개
                content: request.detail, // 상세 내용
                startDate: request.startDate,
                endDate: request.endDate,
                maxParticipants: request.maxParticipants,
                tags: request.tags.split(',').map(t => t.trim()).filter(Boolean)
            };

            await createChallengePost(payload);
            goBackSafely();
        } catch (e) {
            console.error(e);
            Alert.alert('에러', '챌린지 생성 중 오류가 발생했습니다.');
        }
    };

    return (
        <View style={STYLE.BASE_CONTAINER}>
            <EditorHeader onSubmit={handleSubmit} onSave={() => { }} />
            <ScrollView style={STYLE.CONTENT_CONTAINER} contentContainerStyle={{ padding: 16, paddingBottom: 40 }}>
                <View style={styles.header}>
                    <Text style={styles.headerTitle}>챌린지 등록</Text>
                </View>

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
