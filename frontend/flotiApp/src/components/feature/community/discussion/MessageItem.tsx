import { Dispatch, SetStateAction, useContext, useState } from 'react';
import { View, Text, Pressable, Modal, StyleSheet, useWindowDimensions } from 'react-native';

import { UserContext } from '@/contexts/UserContext';

import { TouchEvent } from '@/types/event'
import { MessageResponse } from '@/types/community/discussion';

import MorePopup from '@/components/MorePopup';
import { IconSymbol } from '@/components/ui/IconSymbol';
import { ContentText } from '@/components/ui/BreakAllText';
import ProfileAvatar from '@/components/feature/community/ProfileAvatar';

import { computePopupPosition } from '@/utils/position';
import { formatTimeOnly } from '@/utils/time';
import COLOR from '@/constants/colors';
import { STYLE } from '@/constants/styles';

export default function MessageItem({
    message, menuId, onChangeMenuId, onDeleteConfirm, onToggleLike
}: {
    message: MessageResponse;
    menuId: number | null;
    onChangeMenuId: Dispatch<SetStateAction<number | null>>;
    onDeleteConfirm: (messageId: number) => void;
    onToggleLike: (message: MessageResponse) => void;
}) {
    const { width: screenWidth, height: screenHeight } = useWindowDimensions();

    const [menuPosition, setMenuPosition] = useState({ x: 0, y: 0 });

    const userContext = useContext(UserContext);  // 사용자 상태
    const isAuthor = (message.author?.username === userContext?.username);

    /* 이벤트 핸들러 */
    const handleOpenMenu = (event: TouchEvent) => {
        const { pageX, pageY } = event.nativeEvent;
        const position = computePopupPosition(pageX, pageY, screenWidth, screenHeight);

        setMenuPosition(position);  // 터치 레퍼런스에 따라 메뉴 위치 변경
        onChangeMenuId(message.id);
    };

    return (
        <View style={STYLE.ROW}>
            <ProfileAvatar
                profileImage={message.author?.profileImage ?? null}
                size={50}
                borderRadius={10}
            />

            <View style={styles.MessageItem}>
                {/* 작성자 */}
                <Text style={styles.author}>{message.author?.nickname ?? '탈퇴한 사용자'}</Text>

                {/* 내용 */}
                <Pressable style={styles.card} onLongPress={handleOpenMenu}>
                    <ContentText style={styles.content}>{message.content}</ContentText>
                </Pressable>

                {/* 좋아요수, 시간 */}
                <View style={styles.infoContainer}>
                    <Pressable style={styles.likeItem} onPress={() => onToggleLike(message)}>
                        <IconSymbol
                            size={20}
                            name={message.liked ? "heart.fill" : "heart"} // 좋아요 여부에 따라 아이콘 변경
                            color={message.liked ? 'tomato' : COLOR.TINT.GRAY_DARK} // 좋아요 여부에 따라 색상 변경
                        />
                        <Text style={styles.infoText}>{message.likeCount}</Text>
                    </Pressable>
                    <Text style={styles.infoText}>{formatTimeOnly(message.createdAt)}</Text>
                </View>
            </View>

            {/* 더보기 팝업 */}
            {menuId === message.id && isAuthor && (
                <Modal transparent visible animationType='fade' onRequestClose={() => onChangeMenuId(null)}>
                    <Pressable style={STYLE.FLEX} onPress={() => onChangeMenuId(null)} />
                    <MorePopup
                        actions={[{ label: '삭제', onPress: () => onDeleteConfirm(message.id) }]}
                        style={{ top: menuPosition.y, left: menuPosition.x }}  // 더보기 팝업 위치
                    />
                </Modal>
            )}
        </View>
    );
}

const styles = StyleSheet.create({
    MessageItem: { flexShrink: 1, marginBottom: 4 },
    card: { ...STYLE.CARD, marginBottom: 0 },
    author: {
        paddingVertical: 4,
        fontSize: 14,
        fontWeight: 600,
        color: COLOR.TEXT.GRAY_DARK
    },
    content: { fontSize: 15, lineHeight: 22, color: COLOR.TEXT.GRAY_DARK },
    infoContainer: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
        paddingRight: 8,
        gap: 8
    },
    likeItem: { padding: 4, flexDirection: 'row', alignItems: 'center', gap: 4 },
    infoText: { fontSize: 12, color: COLOR.TEXT.GRAY_MEDIUM }
});