import { useContext, useEffect, useRef, useState } from 'react';
import { View, Text, StyleSheet, Pressable, Animated, ScrollView, Dimensions, TouchableOpacity } from 'react-native';

import { UserContext } from '@/contexts/UserContext';

import { UserResponse } from '@/types/community/common';
import { DiscussionRoomResponse } from '@/types/community/discussion';

import { IconSymbol } from '@/components/ui/IconSymbol';
import BreakAllText from '@/components/ui/BreakAllText';
import ProfileAvatar from '@/components/feature/community/ProfileAvatar';

import { formatDetailTime, formatSmartTime } from '@/utils/time';
import COLOR from '@/constants/colors';
import { STYLE } from '@/constants/styles';

const SCREEN_WIDTH = Dimensions.get('window').width;
const MENU_WIDTH = Math.min(SCREEN_WIDTH * 0.8, 320);   // 최대 320px 또는 80% 

export function SideMenu({
    visible, room, participants, onClose, onUpdate, onDelete, onLeave
}: {
    visible: boolean;
    room: DiscussionRoomResponse;
    participants: UserResponse[];
    onClose: () => void;
    onUpdate: () => void;
    onDelete: () => void;
    onLeave: () => void;
}) {
    const [shouldRender, setShouldRender] = useState(visible);

    const slideAnim = useRef(new Animated.Value(MENU_WIDTH)).current; // slide 애니메이션
    const fadeAnim = useRef(new Animated.Value(0)).current; // fade 애니메이션
    const username = useContext(UserContext)?.username ?? '';
    const isAuthor = (room.author.username === username);

    const sortedParticipants = [...participants].sort((a, b) => {
        if (a.username === room.author.username) return -1;
        if (b.username === room.author.username) return 1;
        return 0;
    }); // 작성자 우선 정렬

    /* 사이드 이펙트 */
    useEffect(() => {
        if (visible) {
            setShouldRender(true);
            Animated.parallel([
                Animated.timing(slideAnim, {
                    toValue: 0,
                    duration: 250,
                    useNativeDriver: true
                }),
                Animated.timing(fadeAnim, {
                    toValue: 1,
                    duration: 250,
                    useNativeDriver: true
                })
            ]).start();
        } else {
            Animated.parallel([
                Animated.timing(slideAnim, {
                    toValue: MENU_WIDTH,
                    duration: 200,
                    useNativeDriver: true
                }),
                Animated.timing(fadeAnim, {
                    toValue: 0,
                    duration: 200,
                    useNativeDriver: true
                })
            ]).start(() => setShouldRender(false));
        }
    }, [visible]);

    if (!shouldRender) return null;

    return (
        <View style={StyleSheet.absoluteFillObject}>
            <Animated.View style={[styles.backdrop, { opacity: fadeAnim }]}>
                <Pressable style={STYLE.FLEX} onPress={onClose} />
            </Animated.View>

            <Animated.View style={[styles.menuPanel, { transform: [{ translateX: slideAnim }] }]}>
                <Text style={styles.title}>{room.title}</Text>

                <View style={styles.metaInfoContainer}>
                    <Text style={styles.metaText}>개설일  {formatDetailTime(room.createdAt)}</Text>
                    <Text style={styles.metaText}>활동일  {formatSmartTime(room.recentActivityAt, 'detail')}</Text>
                </View>

                <ScrollView showsVerticalScrollIndicator={false}>
                    {/* 토론 정보 */}
                    <View style={styles.sectionHeader}>
                        <Text style={styles.sectionTitle}>토론 내용</Text>
                    </View>

                    <View style={styles.contentContainer}>
                        <BreakAllText style={styles.content}>{room.content}</BreakAllText>
                    </View>

                    <View style={styles.sectionHeader}>
                        <Text style={styles.sectionTitle}>참여자</Text>
                        <View style={styles.participantBadge}>
                            <Text style={styles.participantCount}>
                                {room.participantCount} / {room.maxParticipantCount}
                            </Text>
                        </View>
                    </View>

                    {/* 참여자 목록 */}
                    {sortedParticipants.map((participant) => (
                        <ParticipantItem
                            key={participant.username}
                            participant={participant}
                            username={username}
                            isAuthor={participant.username === room.author.username}
                        />
                    ))}
                </ScrollView>
                {/* 하단 버튼 */}
                <View style={styles.footerActions}>
                    {isAuthor ? (
                        <>
                            <TouchableOpacity activeOpacity={0.7} style={styles.updateButton} onPress={onUpdate}>
                                <IconSymbol name="plus.pen" size={18} color={COLOR.TEXT.NAVY} />
                                <Text style={[styles.buttonText, { color: COLOR.TEXT.NAVY }]}>수정하기</Text>
                            </TouchableOpacity>

                            <TouchableOpacity activeOpacity={0.7} style={styles.leaveOrDeleteButton} onPress={onDelete}>
                                <IconSymbol name="trash" size={18} color={COLOR.BUTTON.RED} />
                            </TouchableOpacity>
                        </>
                    ) : (
                        <TouchableOpacity activeOpacity={0.7} style={styles.leaveOrDeleteButton} onPress={onLeave}>
                            <IconSymbol name="exit" size={18} color={COLOR.BUTTON.RED} />
                            <Text style={[styles.buttonText, { color: COLOR.BUTTON.RED }]}>나가기</Text>
                        </TouchableOpacity>
                    )}
                </View>
            </Animated.View>
        </View>
    );
}

function ParticipantItem({
    participant, username, isAuthor
}: {
    participant: UserResponse;
    username: string;
    isAuthor?: boolean;
}) {
    return (
        <View key={participant.username} style={styles.participantItem}>
            <ProfileAvatar profileImage={participant.profileImage} />
            <View style={styles.nameRow}>
                {participant.username === username && (
                    <View style={styles.meBadge}>
                        <Text style={styles.meBadgeText}>나</Text>
                    </View>
                )}
                <Text style={styles.nickname}>{participant.nickname}</Text>
                {isAuthor && (
                    <View style={styles.crownBadge}>
                        <IconSymbol name="crown" size={12} color='white' />
                    </View>
                )}
            </View>
        </View>
    );
}

const styles = StyleSheet.create({
    backdrop: { flex: 1, backgroundColor: COLOR.OVERLAY },
    menuPanel: {
        position: 'absolute',
        right: 0,
        height: '100%',
        width: MENU_WIDTH,
        backgroundColor: 'white',
        paddingVertical: 30, paddingHorizontal: 20
    },
    title: { fontSize: 18, fontWeight: 700, marginBottom: 20 },
    metaInfoContainer: { marginBottom: 20, gap: 6 },
    metaText: { fontSize: 13, color: COLOR.TEXT.GRAY_MEDIUM, fontWeight: 500 },
    sectionHeader: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
        marginBottom: 16
    },
    sectionTitle: { fontSize: 17, fontWeight: 600, color: COLOR.TEXT.GRAY_DARK },
    contentContainer: { padding: 12, marginBottom: 30, backgroundColor: COLOR.BACKGROUND.SLATE_LIGHT },
    content: { fontSize: 15, color: COLOR.TEXT.GRAY_CHARCOAL, lineHeight: 22 },
    participantBadge: {
        backgroundColor: COLOR.BACKGROUND.SLATE_LIGHT,
        paddingVertical: 4, paddingHorizontal: 10,
        borderRadius: 12
    },
    participantCount: { fontSize: 13, color: COLOR.TEXT.NAVY, fontWeight: 600 },
    participantItem: {
        flexDirection: 'row',
        alignItems: 'center',
        paddingHorizontal: 8,
        marginBottom: 16
    },
    nameRow: { flexDirection: 'row', alignItems: 'center', gap: 6 },
    nickname: { fontSize: 15, fontWeight: 600, color: COLOR.TEXT.GRAY_DARK },
    crownBadge: {
        backgroundColor: 'orange',
        borderRadius: 10,
        width: 18, height: 18,
        justifyContent: 'center',
        alignItems: 'center'
    },
    footerActions: { flexDirection: 'row', marginTop: 20, gap: 8 },
    updateButton: { ...STYLE.BUTTON, width: '80%', backgroundColor: COLOR.BUTTON.GRAY_LIGHT },
    leaveOrDeleteButton: { ...STYLE.BUTTON, flex: 1, backgroundColor: COLOR.BUTTON.RED_LIGHT },
    buttonText: { marginLeft: 4, fontSize: 15, fontWeight: 600 },
    meBadge: {
        backgroundColor: COLOR.BACKGROUND.SLATE_LIGHT,
        paddingVertical: 2, paddingHorizontal: 6,
        borderRadius: 4
    },
    meBadgeText: { fontSize: 11, color: COLOR.TEXT.NAVY, fontWeight: 500 },
});