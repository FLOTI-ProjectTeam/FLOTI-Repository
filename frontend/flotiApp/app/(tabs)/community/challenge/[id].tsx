import { View, Text, ScrollView, StyleSheet, TouchableOpacity, Pressable } from 'react-native';
import { useLocalSearchParams } from 'expo-router';
import { useEffect, useState } from 'react';

import { useNavigation } from '@/hooks/useNavigation';
import { getChallengePost, getChallengeProgress, getChallengeFeeds, joinChallenge } from '@/api/community/challengeApi';
import { ChallengeDetailResponse, FeedResponse, ParticipantResponse } from '@/types/community/challenge';
import { useUser } from '@/contexts/UserContext';
import { IconSymbol } from '@/components/ui/IconSymbol';
import { formatDay } from '@/utils/time';
import COLOR from '@/constants/colors';
import { STYLE, SHADOW } from '@/constants/styles';

export default function ChallengeDetailScreen() {
    const { id } = useLocalSearchParams();
    const { goBackSafely, navigateTo } = useNavigation();
    const { user } = useUser();

    const [challenge, setChallenge] = useState<ChallengeDetailResponse | null>(null);
    const [feeds, setFeeds] = useState<FeedResponse[]>([]);
    const [tab, setTab] = useState<'PARTICIPANT' | 'FEED'>('PARTICIPANT');
    const [progress, setProgress] = useState(0);

    // 헤더 X 버튼 클릭 시 챌린지 메인화면(리스트)으로 이동
    const handleGoBack = () => {
        navigateTo('/community/challenge');
    };

    // 성과 공유(피드 작성) 버튼
    const handleGoToFeedCreate = () => {
        navigateTo(`/community/challenge/feed/create?challengeId=${id}`);
    };

    // 전체 피드 보기
    const handleGoToAllFeeds = () => {
        navigateTo(`/community/challenge/feed/list?challengeId=${id}`);
    };

    // 챌린지 참여하기
    const handleJoinChallenge = async () => {
        if (!user) {
            alert('로그인이 필요합니다.');
            return;
        }
        try {
            await joinChallenge(Number(id));
            alert('챌린지에 참여하였습니다!');
            // 데이터 갱신
            const res = await getChallengePost(Number(id));
            setChallenge(res.data);
            // 진행률 갱신
            getChallengeProgress(Number(id)).then(p => setProgress(p.data.myProgress || 0));
        } catch (error) {
            console.error(error);
            alert('참여에 실패했습니다.');
        }
    };

    useEffect(() => {
        if (id) {
            // 챌린지 상세 조회
            getChallengePost(Number(id))
                .then(res => setChallenge(res.data))
                .catch(() => {
                    console.log('챌린지 정보 로딩 실패');
                });

            // 피드 목록 조회
            getChallengeFeeds(Number(id), 0)
                .then(res => setFeeds(res.data.content || []))
                .catch(() => setFeeds([]));

            // 내 진행률 조회 (상세 조회 응답에 포함되어 있지만 개별 API도 호출 가능, 여기서는 상세 응답 우선 사용 또는 별도 호출)
            getChallengeProgress(Number(id))
                .then(res => setProgress(res.data.myProgress || 0))
                .catch(() => setProgress(0));
        }
    }, [id]);

    // 로딩 중이거나 데이터가 없을 때의 "틀"
    const safeChallenge = challenge || {
        title: '챌린지 정보를 불러오는 중...',
        intro: '잠시만 기다려 주세요.',
        startDate: new Date().toISOString(),
        endDate: new Date().toISOString(),
        currentParticipants: 0,
        content: '',
        participants: [],
        myProgress: 0,
        progress: 0,
        isCompleted: false
    } as unknown as ChallengeDetailResponse;

    const now = new Date();
    const endDate = new Date(safeChallenge.endDate);
    const isExpired = now > endDate;

    // 종료 조건: DB status가 true이거나 날짜가 지났으면
    const isCompleted = safeChallenge.isCompleted || isExpired;

    const statusText = !isCompleted ? '진행 중' : '종료됨';
    const statusColor = !isCompleted ? '#53C3A6' : '#999';

    // 참여 여부 확인
    const isJoined = challenge?.participants?.some(p => p.nickname === user?.username) || false;

    return (
        <View style={STYLE.BASE_CONTAINER}>
            {/* 헤더 */}
            <View style={styles.headerContainer}>
                <Pressable onPress={handleGoBack} style={styles.iconWrapper}>
                    <IconSymbol name="chevron.left" size={32} color={COLOR.TINT.GRAY_DARK} />
                </Pressable>
                <View style={styles.titleContainer}>
                    <Text style={styles.headerTitle} numberOfLines={1}>{safeChallenge.title}</Text>
                </View>
                <View style={{ width: 40 }} />
            </View>

            <ScrollView style={STYLE.CONTENT_CONTAINER} contentContainerStyle={{ padding: 16, paddingBottom: 100 }}>
                {/* 1. 상단 정보 카드 */}
                <View style={styles.card}>
                    <View style={styles.headerRow}>
                        <Text style={styles.title}>{safeChallenge.title}</Text>
                        <Text style={[styles.status, { color: statusColor }]}>{statusText}</Text>
                    </View>
                    <Text style={styles.description}>{safeChallenge.intro}</Text>

                    <View style={styles.infoRow}>
                        <IconSymbol name="calendar" size={14} color={COLOR.TEXT.GRAY_MEDIUM} />
                        <Text style={styles.infoText}>{formatDay(safeChallenge.startDate)} ~ {formatDay(safeChallenge.endDate)}</Text>
                        <View style={{ width: 8 }} />
                        <IconSymbol name="person.2" size={14} color={COLOR.TEXT.GRAY_MEDIUM} />
                        <Text style={styles.infoText}>{safeChallenge.currentParticipants}/{safeChallenge.maxParticipants}명</Text>
                    </View>

                    {/* 진행률 원형 (전체 평균 진행률 -> progress) */}
                    <View style={styles.progressCircle}>
                        <View style={styles.progressInner}>
                            <Text style={styles.progressPercent}>{Math.round(safeChallenge.progress || 0)}%</Text>
                        </View>
                    </View>
                </View>

                {/* 2. 상세 내용 */}
                <View style={styles.card}>
                    <Text style={styles.sectionTitle}>📝 상세 내용</Text>
                    <Text style={styles.content}>{safeChallenge.content || '내용이 없습니다.'}</Text>
                </View>

                {/* 3. 나의 진행률 */}
                <View style={styles.card}>
                    <Text style={styles.sectionTitle}>📊 나의 진행률</Text>
                    <View style={styles.myProgressRow}>
                        <View style={styles.avatarPlaceholder} />
                        <Text style={styles.nickname}>-</Text>
                        <View style={styles.progressBarContainer}>
                            <View style={[styles.progressBarFill, { width: `${progress}%` }]} />
                            <Text style={styles.progressLabel}>{progress}%</Text>
                            <Text style={styles.contributionLabel}>공헌도 0</Text>
                        </View>
                    </View>
                </View>

                {/* 4. 탭 (참여자 / 피드) */}
                <View style={styles.tabContainer}>
                    <TouchableOpacity
                        style={[styles.tabButton, tab === 'PARTICIPANT' && styles.activeTab]}
                        onPress={() => setTab('PARTICIPANT')}
                    >
                        <Text style={[styles.tabText, tab === 'PARTICIPANT' && styles.activeTabText]}>참여자</Text>
                    </TouchableOpacity>
                    <TouchableOpacity
                        style={[styles.tabButton, tab === 'FEED' && styles.activeTab]}
                        onPress={() => setTab('FEED')}
                    >
                        <Text style={[styles.tabText, tab === 'FEED' && styles.activeTabText]}>피드</Text>
                    </TouchableOpacity>
                </View>

                {/* 5. 탭 컨텐츠 */}
                <View style={styles.tabContentCard}>
                    {tab === 'PARTICIPANT' ? (
                        <>
                            <Text style={{ fontSize: 13, color: COLOR.TEXT.GRAY_MEDIUM, marginBottom: 10 }}>
                                👥 {safeChallenge.participants ? safeChallenge.participants.length : 0}/{safeChallenge.maxParticipants}명
                            </Text>
                            {safeChallenge.participants && safeChallenge.participants.length > 0 ? (
                                safeChallenge.participants.map((p, index) => (
                                    <View key={p.id} style={styles.participantRow}>
                                        <Text style={styles.rank}>{index + 1}</Text>
                                        <View style={styles.avatarPlaceholderSmall} />
                                        <View style={{ flexDirection: 'row', alignItems: 'center', gap: 4, width: 60 }}>
                                            <Text style={styles.participantName} numberOfLines={1}>{p.nickname}</Text>
                                            {safeChallenge.author && safeChallenge.author.nickname === p.nickname && (
                                                <Text style={{ fontSize: 10 }}>👑</Text>
                                            )}
                                        </View>
                                        <View style={styles.smallProgressContainer}>
                                            <View style={{ width: `${p.progress}%`, backgroundColor: '#53C3A6', height: '100%', borderRadius: 6 }} />
                                        </View>
                                        <Text style={{ fontSize: 12, color: '#53C3A6', fontWeight: '600' }}>{p.progress}%</Text>
                                    </View>
                                ))
                            ) : (
                                <View style={{ paddingVertical: 20, alignItems: 'center' }}>
                                    <Text style={{ color: COLOR.TEXT.GRAY_MEDIUM }}>아직 참여자가 없습니다.</Text>
                                </View>
                            )}
                        </>
                    ) : (
                        <>
                            <TouchableOpacity style={styles.viewAllIcon} onPress={handleGoToAllFeeds}>
                                <IconSymbol name="list.bullet" size={24} color={COLOR.TEXT.GRAY_MEDIUM} />
                            </TouchableOpacity>

                            {feeds.length > 0 ? feeds.map((feed) => (
                                <View key={feed.id} style={styles.feedItem}>
                                    <View style={styles.feedHeader}>
                                        <View style={styles.avatarPlaceholderSmall} />
                                        <Text style={styles.feedAuthor}>{feed.author.nickname}</Text>
                                    </View>
                                    <View style={styles.feedContentContainer}>
                                        <Text style={styles.feedContent} numberOfLines={3}>{feed.content}</Text>
                                    </View>
                                </View>
                            )) : (
                                <View style={{ paddingVertical: 20, alignItems: 'center' }}>
                                    <Text style={{ color: COLOR.TEXT.GRAY_MEDIUM }}>등록된 피드가 없습니다.</Text>
                                </View>
                            )}
                        </>
                    )}
                </View>
            </ScrollView>

            {/* 하단 버튼 */}
            {isCompleted ? (
                <TouchableOpacity
                    style={[styles.bottomButton, { backgroundColor: '#999' }]}
                    disabled={true}
                >
                    <Text style={styles.bottomButtonText}>종료된 챌린지</Text>
                </TouchableOpacity>
            ) : isJoined ? (
                <TouchableOpacity style={styles.bottomButton} onPress={handleGoToFeedCreate}>
                    <Text style={styles.bottomButtonText}>성과 공유</Text>
                </TouchableOpacity>
            ) : (
                <TouchableOpacity
                    style={[styles.bottomButton, { backgroundColor: '#53C3A6' }]}
                    onPress={handleJoinChallenge}
                >
                    <Text style={styles.bottomButtonText}>참여하기</Text>
                </TouchableOpacity>
            )}
        </View>
    );
}

const styles = StyleSheet.create({
    headerContainer: { flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between', paddingHorizontal: 16, height: 60, backgroundColor: 'white' },
    iconWrapper: { padding: 8 },
    titleContainer: { flex: 1, alignItems: 'center' },
    headerTitle: { fontSize: 18, fontWeight: '700', color: COLOR.TEXT.GRAY_DARK },

    card: {
        backgroundColor: 'white', borderRadius: 12, padding: 20, marginBottom: 16,
        ...SHADOW
    },
    headerRow: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'flex-start', paddingRight: 60 },
    title: { fontSize: 18, fontWeight: '700', color: COLOR.TEXT.GRAY_DARK, marginBottom: 8, flex: 1 },
    status: { fontSize: 12, color: '#53C3A6', fontWeight: '600' },
    description: { fontSize: 13, color: COLOR.TEXT.GRAY_CHARCOAL, marginBottom: 12 },
    infoRow: { flexDirection: 'row', alignItems: 'center', marginBottom: 12, gap: 4 },
    infoText: { fontSize: 12, color: COLOR.TEXT.GRAY_MEDIUM },

    progressCircle: {
        position: 'absolute', top: 20, right: 20,
        width: 50, height: 50, borderRadius: 25,
        borderWidth: 4, borderColor: '#E0E0E0',
        justifyContent: 'center', alignItems: 'center'
    },
    progressInner: { alignItems: 'center' },
    progressPercent: { fontSize: 12, fontWeight: '700', color: COLOR.TEXT.GRAY_DARK },

    tags: { flexDirection: 'row', gap: 6 },
    tag: { backgroundColor: '#E8EAFF', paddingHorizontal: 10, paddingVertical: 4, borderRadius: 12 },
    tagText: { color: '#7D8CFF', fontSize: 12, fontWeight: '600' },

    sectionTitle: { fontSize: 16, fontWeight: '700', color: COLOR.TEXT.GRAY_DARK, marginBottom: 8 },
    content: { fontSize: 14, color: COLOR.TEXT.GRAY_CHARCOAL, lineHeight: 22 },

    myProgressRow: { flexDirection: 'row', alignItems: 'center', gap: 12, backgroundColor: '#F8F9FA', padding: 12, borderRadius: 8 },
    avatarPlaceholder: { width: 36, height: 36, borderRadius: 18, backgroundColor: '#DDE2E5' },
    nickname: { fontSize: 14, fontWeight: '600', color: COLOR.TEXT.GRAY_DARK, width: 60 },
    progressBarContainer: { flex: 1, height: 16, backgroundColor: '#E0E0E0', borderRadius: 8, justifyContent: 'center' },
    progressBarFill: { height: '100%', backgroundColor: '#D0D0D0', borderRadius: 8 },
    progressLabel: { position: 'absolute', right: 8, fontSize: 10, color: COLOR.TEXT.GRAY_DARK },
    contributionLabel: { position: 'absolute', right: 0, bottom: -16, fontSize: 10, color: COLOR.TEXT.GRAY_MEDIUM },

    tabContainer: {
        flexDirection: 'row',
        marginBottom: -1,
        zIndex: 1,
        marginHorizontal: 4
    },
    tabButton: {
        flex: 1, paddingVertical: 12, alignItems: 'center', backgroundColor: '#E0E6EF',
        borderTopLeftRadius: 12, borderTopRightRadius: 12,
        marginRight: 4
    },
    activeTab: { backgroundColor: '#9FAFC6', zIndex: 2 },
    tabText: { fontSize: 15, fontWeight: '600', color: 'white' },
    activeTabText: { color: 'white' },

    tabContentCard: {
        backgroundColor: 'white',
        borderRadius: 12,
        padding: 20,
        marginBottom: 80,
        borderTopLeftRadius: 0,
        borderTopRightRadius: 0,
        ...SHADOW,
        zIndex: 0
    },

    participantRow: { flexDirection: 'row', alignItems: 'center', gap: 10, paddingVertical: 12, borderBottomWidth: 1, borderBottomColor: '#F0F0F0' },
    rank: { width: 20, textAlign: 'center', fontWeight: '700', color: COLOR.TEXT.GRAY_MEDIUM },
    avatarPlaceholderSmall: { width: 32, height: 32, borderRadius: 16, backgroundColor: '#DDE2E5' },
    participantName: { fontSize: 13, color: COLOR.TEXT.GRAY_DARK, width: 50 },
    smallProgressContainer: { flex: 1, height: 12, backgroundColor: '#E0E0E0', borderRadius: 6 },

    viewAllIcon: {
        position: 'absolute',
        top: 10,
        right: 10,
        padding: 8,
        zIndex: 100,
        elevation: 10
    },
    feedItem: { marginBottom: 16, paddingBottom: 16, borderBottomWidth: 1, borderBottomColor: '#F0F0F0' },
    feedHeader: { flexDirection: 'row', alignItems: 'center', marginBottom: 8, gap: 8 },
    feedAuthor: { fontSize: 14, fontWeight: '600', color: COLOR.TEXT.GRAY_DARK },
    feedContentContainer: { paddingLeft: 40 },
    feedContent: { fontSize: 14, color: COLOR.TEXT.GRAY_CHARCOAL, lineHeight: 20 },

    bottomButtonContainer: { padding: 16, backgroundColor: 'white', borderTopWidth: 1, borderTopColor: '#F0F0F0' },
    bottomButton: { backgroundColor: '#B2D8D5', paddingVertical: 16, borderRadius: 24, alignItems: 'center' },
    bottomButtonText: { fontSize: 18, fontWeight: '700', color: 'white' }
});
