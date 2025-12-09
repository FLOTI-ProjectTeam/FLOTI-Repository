import { View, Text, StyleSheet, TouchableOpacity } from 'react-native';
import { useEffect, useState } from 'react';
import { ChallengeSummaryResponse } from '@/types/community/challenge';
import { IconSymbol } from '@/components/ui/IconSymbol';
import COLOR from '@/constants/colors';
import { STYLE } from '@/constants/styles';
import { formatDay } from '@/utils/time';
import { getChallengeProgress } from '@/api/community/challengeApi';

export default function ChallengeItem({
    item, onPress, showProgress = true
}: {
    item: ChallengeSummaryResponse;
    onPress: () => void;
    showProgress?: boolean;
}) {
    const [progress, setProgress] = useState(0);

    useEffect(() => {
        if (showProgress) {
            getChallengeProgress(item.id)
                .then(res => {
                    setProgress(res.data.progress || 0);
                })
                .catch(err => {
                    console.error('Progress fetch failed:', err);
                });
        }
    }, [item.id, showProgress]);

    // 진행 상태 계산: 날짜 지났으면 종료 처리
    const now = new Date();
    const endDate = new Date(item.endDate);
    const isExpired = now > endDate;

    const isActive = !item.isCompleted && !isExpired;
    const statusText = isActive ? '진행 중' : '종료됨';
    const statusColor = isActive ? '#53C3A6' : '#999';

    const period = `${formatDay(item.startDate)} ~ ${formatDay(item.endDate)}`;

    return (
        <TouchableOpacity
            activeOpacity={0.9}
            onPress={onPress}
            style={[STYLE.CARD, styles.container]}
        >
            <View style={styles.mainContent}>
                {/* 상단 태그와 상태 */}
                <View style={styles.topRow}>
                    <Text style={styles.title}>{item.title}</Text>
                    <Text style={[styles.status, { color: statusColor }]}>{statusText}</Text>
                </View>

                {/* 설명 */}
                <Text style={styles.description} numberOfLines={1}>{item.intro}</Text>

                {/* 날짜 및 인원 */}
                <View style={styles.infoRow}>
                    <IconSymbol name="calendar" size={14} color={COLOR.TEXT.GRAY_MEDIUM} />
                    <Text style={styles.infoText}>{period}</Text>
                    <View style={{ width: 8 }} />
                    <IconSymbol name="person.2" size={14} color={COLOR.TEXT.GRAY_MEDIUM} />
                    <Text style={styles.infoText}>{item.currentParticipants}/{item.maxParticipants}명</Text>
                </View>

                {/* 태그 (백엔드 미지원으로 제거) */}
            </View>

            {/* 원형 진행률 (우측 배치) */}
            <View style={styles.progressSection}>
                <View style={styles.progressCircleStub}>
                    <View style={styles.progressInnerRing} />
                    <Text style={styles.progressText}>
                        {Math.round(progress)}%
                    </Text>
                </View>
            </View>
        </TouchableOpacity>
    );
}

const styles = StyleSheet.create({
    container: { flexDirection: 'row', justifyContent: 'space-between', padding: 20, borderRadius: 16 },
    mainContent: { flex: 1, paddingRight: 10 },
    topRow: { flexDirection: 'row', justifyContent: 'space-between', marginBottom: 6 },
    title: { fontSize: 16, fontWeight: '700', color: COLOR.TEXT.GRAY_DARK, flex: 1, marginRight: 8 },
    status: { fontSize: 12, fontWeight: '600' },
    description: { fontSize: 13, color: COLOR.TEXT.GRAY_CHARCOAL, marginBottom: 10 },

    infoRow: { flexDirection: 'row', alignItems: 'center', marginBottom: 10 },
    infoText: { fontSize: 12, color: COLOR.TEXT.GRAY_MEDIUM, marginLeft: 4 },

    tags: { flexDirection: 'row', gap: 6 },
    tag: { backgroundColor: '#E8EAFF', paddingHorizontal: 10, paddingVertical: 4, borderRadius: 12 },
    tagText: { fontSize: 11, color: '#7D8CFF', fontWeight: '600' },

    progressSection: { justifyContent: 'center', alignItems: 'center' },
    progressCircleStub: {
        width: 60, height: 60, borderRadius: 30,
        backgroundColor: '#E0E0E0', // 배경색 (도넛 모양을 위해)
        justifyContent: 'center', alignItems: 'center'
    },
    progressInnerRing: {
        position: 'absolute', width: 50, height: 50, borderRadius: 25,
        backgroundColor: 'white' // 내부 원 (도넛 효과)
    },
    progressText: { fontSize: 12, fontWeight: '700', color: COLOR.TEXT.GRAY_DARK, zIndex: 1 }
});
