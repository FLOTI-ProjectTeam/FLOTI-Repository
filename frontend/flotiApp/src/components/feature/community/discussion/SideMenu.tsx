import { useContext, useEffect, useRef, useState } from 'react';
import { View, Text, StyleSheet, Pressable, Animated, ScrollView, Dimensions, Platform } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';

import { UserContext } from '@/contexts/UserContext';

import { UserResponse } from '@/types/community/common';
import { DiscussionRoomResponse } from '@/types/community/discussion';

import { IconSymbol } from '@/components/ui/IconSymbol';
import ProfileAvatar from '@/components/feature/community/ProfileAvatar';

import { formatDetailTime, formatSmartTime } from '@/utils/time';
import COLOR from '@/constants/colors';
import BreakAllText from '@/components/ui/BreakAllText';

const SCREEN_WIDTH = Dimensions.get('window').width;
const MENU_WIDTH = Math.min(SCREEN_WIDTH * 0.8, 320); // Max 320px or 80%

export function SideMenu({
    visible, onClose, room, participants, onUpdate, onDelete
}: {
    visible: boolean;
    onClose: () => void;
    room: DiscussionRoomResponse;
    participants: UserResponse[];
    onUpdate: () => void;
    onDelete: () => void;
}) {
    const [shouldRender, setShouldRender] = useState(visible);
    const insets = useSafeAreaInsets();

    const slideAnim = useRef(new Animated.Value(MENU_WIDTH)).current;
    const fadeAnim = useRef(new Animated.Value(0)).current;
    const userContext = useContext(UserContext);
    const isAuthor = (room.author.username === userContext?.username);

    useEffect(() => {
        if (visible) {
            setShouldRender(true);
            Animated.parallel([
                Animated.timing(slideAnim, {
                    toValue: 0,
                    duration: 250,
                    useNativeDriver: true,
                }),
                Animated.timing(fadeAnim, {
                    toValue: 1,
                    duration: 250,
                    useNativeDriver: true,
                })
            ]).start();
        } else {
            Animated.parallel([
                Animated.timing(slideAnim, {
                    toValue: MENU_WIDTH,
                    duration: 200,
                    useNativeDriver: true,
                }),
                Animated.timing(fadeAnim, {
                    toValue: 0,
                    duration: 200,
                    useNativeDriver: true,
                })
            ]).start(() => setShouldRender(false));
        }
    }, [visible]);

    if (!shouldRender) return null;

    return (
        <View style={styles.overlayContainer}>
            <Animated.View style={[styles.backdrop, { opacity: fadeAnim }]}>
                <Pressable style={{ flex: 1 }} onPress={onClose} />
            </Animated.View>

            <Animated.View
                style={[
                    styles.menuPanel,
                    {
                        transform: [{ translateX: slideAnim }],
                        paddingTop: insets.top + 20,
                        paddingBottom: insets.bottom + 20
                    }
                ]}
            >
                <Text style={styles.roomTitle}>{room.title}</Text>

                <View style={styles.metaInfoRow}>
                    <IconSymbol name="time" size={14} color={COLOR.TEXT.GRAY_MEDIUM} />
                    <Text style={styles.metaText}>
                        개설일  {formatDetailTime(room.createdAt)}
                    </Text>
                </View>
                <View style={styles.metaInfoRow}>
                    <IconSymbol name="report" size={14} color={COLOR.TEXT.GRAY_MEDIUM} />
                    <Text style={styles.metaText}>
                        활동일  {formatSmartTime(room.recentActivityAt, 'detail')}
                    </Text>
                </View>

                <ScrollView style={{ flex: 1 }} contentContainerStyle={{ paddingVertical: 20 }} showsVerticalScrollIndicator={false}>

                    {/* 1. Room Info Card */}
                    <View style={styles.sectionHeader}>
                        <Text style={styles.sectionTitle}>토론 내용</Text>
                    </View>

                    <View style={styles.roomContentContainer}>
                        <BreakAllText style={styles.roomContent}>{room.content}</BreakAllText>
                    </View>

                    <View style={styles.sectionHeader}>
                        <Text style={styles.sectionTitle}>참여자</Text>
                        <View style={styles.participantBadge}>
                            <Text style={styles.participantCount}>
                                {room.participantCount} / {room.maxParticipantCount}
                            </Text>
                        </View>
                    </View>

                    {/* 2. Participants List */}
                    <View style={styles.participantsList}>
                        {/* Author */}
                        <View style={styles.participantItem}>
                            <ProfileAvatar profileImage={room.author.profileImage} />
                            <View style={styles.nameRow}>
                                <Text style={styles.nickname}>{room.author.nickname}</Text>
                                <View style={styles.crownBadge}>
                                    <IconSymbol name="crown" size={12} color="#FFF" />
                                </View>
                            </View>
                        </View>

                        {/* Other Participants */}
                        {participants.map((user) => (
                            <View key={user.username} style={styles.participantItem}>
                                <ProfileAvatar profileImage={user.profileImage} />
                                <Text style={styles.nickname}>{user.nickname}</Text>
                            </View>
                        ))}
                    </View>
                </ScrollView>

                {/* 3. Footer Actions */}
                <View style={[styles.footerActions, { paddingBottom: 0 }]}>
                    {isAuthor ? (
                        <>
                            <Pressable
                                style={({ pressed }) => [
                                    styles.actionButton,
                                    styles.updateButton,
                                    pressed && styles.pressedButton
                                ]}
                                onPress={onUpdate}
                            >
                                <IconSymbol name="plus.pen" size={18} color={COLOR.TEXT.NAVY} />
                                <Text style={[styles.actionButtonText, { color: COLOR.TEXT.NAVY }]}>정보 수정</Text>
                            </Pressable>

                            <Pressable
                                style={({ pressed }) => [
                                    styles.actionButton,
                                    styles.deleteButton,
                                    pressed && styles.pressedButton
                                ]}
                                onPress={onDelete}
                            >
                                <IconSymbol name="trash" size={18} color={COLOR.BUTTON.RED} />
                                <Text style={[styles.actionButtonText, { color: COLOR.BUTTON.RED }]}>삭제하기</Text>
                            </Pressable>
                        </>
                    ) : (
                        <Pressable
                            style={({ pressed }) => [
                                styles.actionButton,
                                styles.deleteButton,
                                pressed && styles.pressedButton
                            ]}
                            onPress={onDelete}
                        >
                            <IconSymbol name="exit" size={18} color={COLOR.BUTTON.RED} />
                            <Text style={[styles.actionButtonText, { color: COLOR.BUTTON.RED }]}>나가기</Text>
                        </Pressable>
                    )}
                </View>
            </Animated.View>
        </View>
    );
}

const styles = StyleSheet.create({
    overlayContainer: {
        ...StyleSheet.absoluteFillObject,
        zIndex: 1000
    },
    backdrop: {
        ...StyleSheet.absoluteFillObject,
        backgroundColor: 'rgba(0,0,0,0.5)'
    },
    menuPanel: {
        position: 'absolute',
        right: 0,
        height: '100%',
        width: MENU_WIDTH,
        backgroundColor: '#FFFFFF',
        paddingHorizontal: 20,
        ...Platform.select({
            ios: {
                shadowColor: "#000",
                shadowOffset: { width: -4, height: 0 },
                shadowOpacity: 0.1,
                shadowRadius: 10,
            },
            android: {
                elevation: 10,
            }
        })
    },
    roomTitle: {
        fontSize: 18,
        fontWeight: 'bold',
        color: COLOR.TEXT.GRAY_DARK,
        marginBottom: 24
    },
    /* Card Style for Room Info */
    metaInfoRow: {
        flexDirection: 'row',
        alignItems: 'center',
        marginBottom: 6,
        gap: 6,
    },
    metaText: {
        fontSize: 13,
        color: COLOR.TEXT.GRAY_MEDIUM,
        fontWeight: '500',
    },
    roomContentContainer: {
        padding: 12,
        borderRadius: 12,
        marginBottom: 30,
        borderWidth: 1,
        backgroundColor: COLOR.BACKGROUND.SLATE_LIGHT,
        borderColor: COLOR.TINT.SLATE,
    },
    roomContent: {
        fontSize: 15,
        color: COLOR.TEXT.GRAY_CHARCOAL,
        lineHeight: 22
    },

    /* Headers */
    sectionHeader: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
        marginBottom: 16
    },
    sectionTitle: {
        fontSize: 17,
        fontWeight: '700',
        color: COLOR.TEXT.GRAY_DARK
    },
    participantBadge: {
        backgroundColor: COLOR.BACKGROUND.SLATE_LIGHT,
        paddingHorizontal: 10,
        paddingVertical: 4,
        borderRadius: 12,
    },
    participantCount: {
        fontSize: 13,
        color: COLOR.TEXT.NAVY,
        fontWeight: '600'
    },

    /* Participant List */
    participantsList: { marginBottom: 20 },
    participantItem: {
        flexDirection: 'row',
        alignItems: 'center',
        padding: 12,
        backgroundColor: '#F9FAFB',
        borderRadius: 12,
        marginBottom: 10,
        borderWidth: 1,
        borderColor: '#F0F0F0',
    },
    nameRow: {
        flexDirection: 'row',
        alignItems: 'center',
        gap: 6,
    },
    nickname: {
        fontSize: 15,
        fontWeight: '600',
        color: COLOR.TEXT.GRAY_DARK,
    },
    roleText: {
        fontSize: 12,
        color: COLOR.TEXT.GRAY_MEDIUM,
        marginTop: 2,
    },
    crownBadge: {
        backgroundColor: '#FFD700',
        borderRadius: 10,
        width: 18,
        height: 18,
        justifyContent: 'center',
        alignItems: 'center',
    },

    /* Footer Buttons */
    footerActions: {
        marginTop: 10,
        gap: 12,
    },
    actionButton: {
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'center',
        paddingVertical: 14,
        borderRadius: 12,
        gap: 8,
        borderWidth: 1,
    },
    updateButton: {
        backgroundColor: COLOR.BACKGROUND.SLATE_LIGHT,
        borderColor: 'transparent',
    },
    deleteButton: {
        backgroundColor: '#FFF0F0',
        borderColor: 'transparent',
    },
    pressedButton: {
        opacity: 0.7,
        transform: [{ scale: 0.98 }]
    },
    actionButtonText: {
        fontSize: 15,
        fontWeight: '600',
    },
});