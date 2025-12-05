import { useEffect, useRef, useState } from 'react';
import { View, Text, StyleSheet, Pressable, Animated, ScrollView, Dimensions } from 'react-native';

import { IconSymbol } from '@/components/ui/IconSymbol';
import { formatDetailTime, formatSmartTime } from '@/utils/time';
import { AuthorResponse } from '@/types/community/common';
import { DiscussionRoomResponse } from '@/types/community/discussion';
import COLOR from '@/constants/colors';

const SCREEN_WIDTH = Dimensions.get('window').width;
const MENU_WIDTH = SCREEN_WIDTH * 0.75; // 화면의 75% 차지

export function SideMenu({ 
    visible, onClose, room, participants, onUpdate, onDelete 
}: {
    visible: boolean;
    onClose: () => void;
    room: DiscussionRoomResponse;
    participants: AuthorResponse[];
    onUpdate: () => void;
    onDelete: () => void;
}) {
    const slideAnim = useRef(new Animated.Value(MENU_WIDTH)).current; // 슬라이드 애니메이션
    const fadeAnim = useRef(new Animated.Value(0)).current;   // 투명도 애니메이션
  
    const [shouldRender, setShouldRender] = useState(visible);

    useEffect(() => {
        if (visible) {
            setShouldRender(true);
            // 병렬 애니메이션 실행 (슬라이드 + 배경 어두워짐)
            Animated.parallel([
                Animated.timing(slideAnim, {
                    toValue: 0,
                    duration: 300,
                    useNativeDriver: true,
                }),
                Animated.timing(fadeAnim, {
                    toValue: 1, // 불투명도 1 (스타일에서 backgroundColor alpha로 조절하거나 여기서 opacity 조절)
                    duration: 300,
                    useNativeDriver: true,
                })
            ]).start();
        } else {
            Animated.parallel([
                Animated.timing(slideAnim, {
                    toValue: MENU_WIDTH,
                    duration: 300,
                    useNativeDriver: true,
                }),
                Animated.timing(fadeAnim, {
                    toValue: 0, 
                    duration: 300,
                    useNativeDriver: true,
                })
            ]).start(() => setShouldRender(false));
        }
    }, [visible]);

    if (!shouldRender) return null;

    return (
        <View style={styles.overlayContainer}>
        {/* 배경 터치 시 닫기 */}
        <Animated.View 
            style={[
            styles.backdrop, 
            { opacity: fadeAnim } 
            ]}
        >
            <Pressable style={{ flex: 1 }} onPress={onClose} />
        </Animated.View>

        <Animated.View style={[styles.menuPanel, { transform: [{ translateX: slideAnim }] }]}>
            <View style={styles.safeArea}>
            
            {/* 1. 헤더 & 토론방 정보 */}
            <View style={styles.headerSection}>
                <View style={styles.titleRow}>
                <Text style={styles.menuTitle} numberOfLines={1}>{room.title}</Text>
                </View>
                <Text style={styles.createdDate}>
                    개설일: {formatDetailTime(room.createdAt)}
                </Text>
                <Text style={styles.createdDate}>
                    활동일: {formatSmartTime(room.recentActivityAt, 'detail')}
                </Text>
                <Text style={styles.roomContent} numberOfLines={3}>
                    {room.content}
                </Text>
            </View>

            <View style={styles.divider} />

            {/* 2. 참여자 리스트 (여기를 풍성하게!) */}
            <View style={styles.participantSection}>
                <View style={styles.sectionHeader}>
                <Text style={styles.sectionTitle}>참여자</Text>
                <Text style={styles.participantCount}>
                    {room.participantCount} / {room.maxParticipantCount}명
                </Text>
                </View>
                
                <ScrollView style={styles.participantList}>
                    <View style={styles.participantItem}>
                        {/* 프로필 이미지가 없으면 기본 아이콘 */}
                        <View style={styles.avatar}>
                            <Text style={styles.avatarText}>{room.author.nickname[0]}</Text>
                        </View>
                        <Text style={styles.nickname}>{room.author.nickname}</Text>
                        <IconSymbol name="crown" size={14} color="#FFD700" style={{marginLeft: 4}}/>
                    </View>
                    {participants.map((user) => (
                        <View key={user.username} style={styles.participantItem}>
                            {/* 프로필 이미지가 없으면 기본 아이콘 */}
                            <View style={styles.avatar}>
                                <Text style={styles.avatarText}>{user.nickname[0]}</Text>
                            </View>
                            <Text style={styles.nickname}>{user.nickname}</Text>
                        </View>
                ))}
                </ScrollView>
            </View>

            <View style={styles.divider} />

            {/* 3. 하단 액션 버튼 */}
            <View style={styles.actionSection}>
                <Pressable style={styles.menuItem} onPress={onUpdate}>
                <IconSymbol name="plus.pen" size={20} color={COLOR.TINT.GRAY_DARK} />
                <Text style={styles.menuItemText}>토론방 정보 수정</Text>
                </Pressable>

                <Pressable style={[styles.menuItem, styles.deleteItem]} onPress={onDelete}>
                <IconSymbol name="trash" size={20} color="#FF453A" />
                <Text style={[styles.menuItemText, { color: '#FF453A' }]}>토론방 삭제</Text>
                </Pressable>
            </View>

            </View>
        </Animated.View>
        </View>
    );
}

const styles = StyleSheet.create({
    overlayContainer: { ...StyleSheet.absoluteFillObject, zIndex: 100 },
    backdrop: { ...StyleSheet.absoluteFillObject, backgroundColor: 'rgba(0,0,0,0.5)' },
    menuPanel: {
        position: 'absolute',
        right: 0, // 오른쪽에 붙이기
        top: 0,
        bottom: 0,
        width: MENU_WIDTH,
        height: '100%',
        backgroundColor: 'white',
        padding: 20,
        shadowColor: "#000",
        shadowOffset: { width: -2, height: 0 },
        shadowOpacity: 0.25,
        shadowRadius: 3.84,
        elevation: 5,
    },
    safeArea: {
        flex: 1,
        marginTop: 40, // 노치 고려
    },
    /* 섹션 스타일 */
    headerSection: { marginBottom: 20 },
    titleRow: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
        marginBottom: 8,
    },
    menuTitle: {
        fontSize: 20,
        fontWeight: 'bold',
        flex: 1,
        marginRight: 10,
    },
    createdDate: { fontSize: 12, color: '#888', marginBottom: 8 },
    roomContent: { fontSize: 14, color: '#555', lineHeight: 20 },
    divider: { height: 1, backgroundColor: '#EEE', marginVertical: 15 },
    /* 참여자 섹션 */
    participantSection: { flex: 1 },
    sectionHeader: { flexDirection: 'row', justifyContent: 'space-between', marginBottom: 10 },
    sectionTitle: { fontSize: 16, fontWeight: '600' },
    participantCount: { fontSize: 14, color: '#666' },
    participantList: { flex: 1 },
    participantItem: { flexDirection: 'row', alignItems: 'center', paddingVertical: 8 },
    avatar: {
        width: 32,
        height: 32,
        borderRadius: 16,
        backgroundColor: '#E0E0E0',
        justifyContent: 'center',
        alignItems: 'center',
        marginRight: 10,
    },
    avatarText: { fontSize: 14, fontWeight: 'bold', color: '#555' },
    nickname: { fontSize: 15, color: '#333' },
    /* 하단 액션 */
    actionSection: { marginBottom: 20 },
    menuItem: {
        flexDirection: 'row',
        alignItems: 'center',
        paddingVertical: 12,
        gap: 12,
    },
    menuItemText: { fontSize: 16, color: '#333' },
    deleteItem: { marginTop: 10 }
});