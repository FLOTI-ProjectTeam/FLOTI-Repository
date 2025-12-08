import { View, Text, TextInput, ScrollView, StyleSheet, TouchableOpacity, Image } from 'react-native';
import { useLocalSearchParams } from 'expo-router';
import { useState, useEffect } from 'react';

import { EditorHeader } from '@/components/ui/Header';
import { IconSymbol } from '@/components/ui/IconSymbol';
import { useNavigation } from '@/hooks/useNavigation';
import { showToast } from '@/utils/toast';
import { getChallengePost } from '@/api/community/challengeApi';
import COLOR from '@/constants/colors';
import { STYLE } from '@/constants/styles';

export default function FeedCreateScreen() {
    const { challengeId } = useLocalSearchParams();
    const { goBackSafely } = useNavigation();

    const [content, setContent] = useState('');
    const [image, setImage] = useState<string | null>(null);
    const [challengeTitle, setChallengeTitle] = useState<string>('');

    useEffect(() => {
        if (challengeId) {
            getChallengePost(Number(challengeId))
                .then(res => setChallengeTitle(res.data.title))
                .catch(() => setChallengeTitle('챌린지 정보를 불러올 수 없습니다.'));
        }
    }, [challengeId]);

    const handleSave = () => {
        showToast('임시저장 되었습니다.');
    };

    const handleSubmit = () => {
        // TODO: API integration
        showToast('피드가 등록되었습니다.');
        goBackSafely();
    };

    const handlePickImage = () => {
        // Mock Image Pickup
        setImage('https://via.placeholder.com/300');
    };

    return (
        <View style={STYLE.BASE_CONTAINER}>
            <EditorHeader onSave={handleSave} onSubmit={handleSubmit} />

            <ScrollView style={STYLE.CONTENT_CONTAINER} contentContainerStyle={{ padding: 16 }}>
                {/* 챌린지 정보 (간략) */}
                <View style={styles.infoContainer}>
                    <Text style={styles.infoLabel}>참여 중인 챌린지</Text>
                    <Text style={styles.challengeTitle}>{challengeTitle || '로딩 중...'}</Text>
                </View>

                {/* 입력 폼 */}
                <View style={styles.inputCard}>
                    <TextInput
                        style={styles.input}
                        placeholder="챌린지 성과를 공유해보세요! (오늘 읽은 책, 운동 기록 등)"
                        placeholderTextColor={COLOR.TEXT.GRAY_MEDIUM}
                        multiline
                        value={content}
                        onChangeText={setContent}
                        textAlignVertical="top"
                    />

                    {/* 이미지 첨부 */}
                    <View style={styles.imageSection}>
                        <ScrollView horizontal showsHorizontalScrollIndicator={false}>
                            <TouchableOpacity style={styles.addImageButton} onPress={handlePickImage}>
                                <IconSymbol name="camera.fill" size={24} color={COLOR.TEXT.GRAY_MEDIUM} />
                                <Text style={styles.addImageText}>0/5</Text>
                            </TouchableOpacity>

                            {image && (
                                <View style={styles.imagePreview}>
                                    <Image source={{ uri: image }} style={styles.image} />
                                    <TouchableOpacity style={styles.deleteImage} onPress={() => setImage(null)}>
                                        <IconSymbol name="x" size={12} color="white" />
                                    </TouchableOpacity>
                                </View>
                            )}
                        </ScrollView>
                    </View>
                </View>
            </ScrollView>
        </View>
    );
}

const styles = StyleSheet.create({
    infoContainer: { marginBottom: 16, paddingHorizontal: 4 },
    infoLabel: { fontSize: 12, color: COLOR.TEXT.GRAY_MEDIUM, marginBottom: 4 },
    challengeTitle: { fontSize: 16, fontWeight: '700', color: COLOR.TEXT.GRAY_DARK },

    inputCard: {
        backgroundColor: 'white',
        borderRadius: 12,
        padding: 16,
        minHeight: 300
    },
    input: {
        fontSize: 15,
        color: COLOR.TEXT.GRAY_DARK,
        minHeight: 150,
        marginBottom: 16
    },
    imageSection: {
        flexDirection: 'row',
        paddingTop: 16,
        borderTopWidth: 1,
        borderTopColor: '#F0F0F0'
    },
    addImageButton: {
        width: 70, height: 70,
        borderRadius: 8,
        borderWidth: 1, borderColor: '#E0E0E0',
        backgroundColor: '#F8F9FA',
        justifyContent: 'center', alignItems: 'center',
        marginRight: 10
    },
    addImageText: { fontSize: 11, color: COLOR.TEXT.GRAY_MEDIUM, marginTop: 4 },
    imagePreview: { width: 70, height: 70, borderRadius: 8, overflow: 'hidden', marginRight: 10 },
    image: { width: '100%', height: '100%' },
    deleteImage: {
        position: 'absolute', top: 4, right: 4,
        width: 18, height: 18, borderRadius: 9,
        backgroundColor: 'rgba(0,0,0,0.6)',
        justifyContent: 'center', alignItems: 'center'
    }
});
