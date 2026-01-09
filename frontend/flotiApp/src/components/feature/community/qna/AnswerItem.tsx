import { Dispatch, SetStateAction, useContext, useRef, useState } from 'react';
import { View, Text, Pressable, Modal, StyleSheet, TouchableOpacity } from 'react-native';

import { UserContext } from '@/contexts/UserContext';

import { AnswerReponse } from '@/types/community/qna';

import MorePopup from '@/components/MorePopup';
import { IconSymbol } from '@/components/ui/IconSymbol';
import BreakAllText from '@/components/ui/BreakAllText';

import { formatDetailTime } from '@/utils/time';
import COLOR from '@/constants/colors';
import { STYLE } from '@/constants/styles';

export default function AnswerItem({
    answer, questioner, menuId, accepted, onChangeMenuId, onGoToUpdate, onDeleteConfirm, onAcceptConfirm, onToggleLike
}: {
    answer: AnswerReponse;
    questioner: string;
    menuId: number | null;
    accepted: boolean;
    onChangeMenuId: Dispatch<SetStateAction<number | null>>;
    onGoToUpdate: (answer: AnswerReponse) => void;
    onDeleteConfirm: (answerId: number) => void;
    onAcceptConfirm: (answerId: number) => void;
    onToggleLike: (answer: AnswerReponse) => void;
}) {
    const [menuPosition, setMenuPosition] = useState({ x: 0, y: 0 });

    const buttonRef = useRef<View>(null); // 컴포넌트의 레퍼런스 저장
    const userContext = useContext(UserContext);  // 사용자 상태
    const isQuestioner = (questioner === userContext?.username);
    const isAuthor = (answer.author?.username === userContext?.username);

    /* 이벤트 핸들러 */
    const handleOpenMenu = () => {
        if (buttonRef.current) {
            buttonRef.current.measure((x, y, width, height, pageX, pageY) => {
                setMenuPosition({ x: pageX - 78, y: pageY });  // 컴포넌트의 레퍼런스에 따라 메뉴 위치 변경
                onChangeMenuId(answer.id);
            });
        }
    };

    return (
        <Pressable style={styles.answerItem}>
            {/* 작성·채택 정보 */}
            <View style={styles.itemRowHeader}>
                <View style={STYLE.COLUMN}>
                    <Text style={styles.author}>{answer.author?.nickname ?? '탈퇴한 사용자'}</Text>
                    <Text style={styles.time}>{formatDetailTime(answer.createdAt)}</Text>
                </View>
                {accepted ? (
                    answer.accepted && (
                        <View style={styles.acceptedBadge}>
                            <IconSymbol name="crown" size={20} color='white' />
                            <Text style={styles.acceptedText}>채택됨</Text>
                        </View>
                    )
                ) : (
                    isQuestioner && (
                        <TouchableOpacity
                            activeOpacity={0.8}
                            style={styles.acceptedButton}
                            onPress={() => onAcceptConfirm(answer.id)}
                        >
                            <IconSymbol name="check.bold" size={20} color='white' />
                            <Text style={styles.acceptedText}>채택하기</Text>
                        </TouchableOpacity>
                    )
                )}
            </View>

            {/* 내용 */}
            <BreakAllText style={styles.content}>{answer.content}</BreakAllText>

            {/* 좋아요 정보, 더보기 버튼 */}
            <View style={styles.infoContainer}>
                <Pressable style={styles.likeItem} onPress={() => onToggleLike(answer)}>
                    <IconSymbol
                        size={20}
                        name={answer.liked ? "heart.fill" : "heart"} // 좋아요 여부에 따라 아이콘 변경
                        color={answer.liked ? 'tomato' : COLOR.TINT.GRAY_DARK} // 좋아요 여부에 따라 색상 변경
                    />
                    <Text style={styles.likeCount}>{answer.likeCount}</Text>
                </Pressable>
                {isAuthor && !answer.liked && (
                    <Pressable ref={buttonRef} onPress={handleOpenMenu}>
                        <IconSymbol name="more.horizontal" size={20} color={COLOR.TINT.GRAY_DARK} />
                    </Pressable>
                )}
            </View>

            {/* 더보기 팝업 */}
            {menuId === answer.id && (
                <Modal transparent visible animationType='fade' onRequestClose={() => onChangeMenuId(null)}>
                    <Pressable style={STYLE.FLEX} onPress={() => onChangeMenuId(null)} />
                    <MorePopup
                        actions={[
                            { label: '수정', onPress: () => onGoToUpdate(answer) },
                            { label: '삭제', onPress: () => onDeleteConfirm(answer.id) },
                        ]}
                        style={{ top: menuPosition.y, left: menuPosition.x }}  // 더보기 팝업 위치
                    />
                </Modal>
            )}
        </Pressable>
    );
}

const styles = StyleSheet.create({
    answerItem: {
        marginBottom: 16,
        padding: 12,
        borderRadius: 10,
        backgroundColor: COLOR.BACKGROUND.POWDER_LIGHT,
        gap: 8
    },
    itemRowHeader: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center' },
    author: { fontSize: 14, fontWeight: 600, color: COLOR.TEXT.GRAY_DARK },
    time: { fontSize: 12, color: COLOR.TEXT.GRAY_MEDIUM },
    acceptedBadge: {
        flexDirection: 'row',
        justifyContent: 'center',
        alignItems: 'center',
        borderRadius: 8,
        backgroundColor: COLOR.TINT.MINT,
        padding: 6,
        gap: 4
    },
    acceptedButton: {
        flexDirection: 'row',
        justifyContent: 'center',
        alignItems: 'center',
        borderRadius: 8,
        backgroundColor: COLOR.BUTTON.SLATE_DARK,
        padding: 6,
        gap: 4
    },
    acceptedText: { fontSize: 14, fontWeight: 600, color: 'white' },
    likeItem: { flexDirection: 'row', alignItems: 'center', gap: 4 },
    content: { fontSize: 15, lineHeight: 22, color: COLOR.TEXT.GRAY_DARK },
    infoContainer: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
        paddingTop: 8,
        borderTopWidth: 1,
        borderTopColor: COLOR.TINT.SLATE_SOFT
    },
    likeCount: { fontSize: 12, color: COLOR.TEXT.GRAY_MEDIUM }
});