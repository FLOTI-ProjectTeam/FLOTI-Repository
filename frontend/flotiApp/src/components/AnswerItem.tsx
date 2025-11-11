import { Dispatch, SetStateAction, useRef, useState } from 'react';
import { View, Text, Pressable, Modal, StyleSheet } from 'react-native';

import { IconSymbol } from '@/components/ui/IconSymbol';
import BreakAllText from '@/components/ui/BreakAllText';
import MorePopup from '@/components/ui/MorePopup';
import { AnswerReponse } from '@/types/community/qna';
import COLOR from '@/constants/colors';
import { STYLE } from '@/constants/styles';

export default function AnswerItem({
    answer, menuId, onChangeMenuId, onGoToUpdate, onDeleteConfirm, onToggleLike
}: {
    answer: AnswerReponse;
    menuId: number | null;
    onChangeMenuId: Dispatch<SetStateAction<number | null>>;
    onGoToUpdate: (answer: AnswerReponse) => void;
    onDeleteConfirm: (answerId: number) => void;
    onToggleLike: (answer: AnswerReponse) => void;
}) {
    const buttonRef = useRef<View>(null); // 컴포넌트의 레퍼런스 저장
    const [menuPosition, setMenuPosition] = useState({ x: 0, y: 0 });

    /* 이벤트 핸들러 */
    const handleOpenMenu = () => {
        if (buttonRef.current) {
            buttonRef.current.measure((x, y, width, height, pageX, pageY) => {
                setMenuPosition({ x: pageX, y: pageY });  // 컴포넌트의 레퍼런스에 따라 메뉴 위치 변경
                onChangeMenuId(answer.id);
            });
        }
    };

    return (
        <View style={styles.answerItem}>
            {/* 작성자 정보, 더보기 버튼 */}
            <View style={styles.itemRowHeader}>
                <Text style={styles.author}>{answer.author?.nickname ?? '탈퇴한 사용자'}</Text>
                <Pressable ref={buttonRef} onPress={handleOpenMenu}>
                    <IconSymbol name="more.horizontal" size={20} color={COLOR.TINT.GRAY_DARK} />
                </Pressable>
            </View>

            {/* 내용 */}
            <BreakAllText style={styles.content}>{answer.content}</BreakAllText>

            {/* 좋아요수 & 시간 */}
            <View style={styles.infoRow}>
                <Pressable style={styles.likeButton} onPress={() => onToggleLike(answer)}>
                <IconSymbol 
                    size={20} 
                    name={answer.liked ? "heart.fill" : "heart"} // 좋아요 여부에 따라 아이콘 변경
                    color={answer.liked ? 'tomato' : COLOR.TINT.GRAY_DARK} // 좋아요 여부에 따라 색상 변경
                />
                <Text style={styles.likeCount}> {answer.likeCount}</Text>
                </Pressable>
                <Text style={styles.time}>{answer.createdAt}</Text>
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
                    style={{ top: menuPosition.y, left: menuPosition.x - 90 }}  // 더보기 버튼 위치
                />
                </Modal>
            )}
        </View>
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
    author: { fontSize: 14, fontWeight: 'bold', color: COLOR.TEXT.GRAY_DARK },
    content: { fontSize: 15, lineHeight: 22, color: COLOR.TEXT.GRAY_DARK },
    infoRow: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
        paddingTop: 8,
        borderTopWidth: 1,
        borderColor: COLOR.TINT.SLATE_SOFT
    },
    likeButton: { flexDirection: 'row', alignItems: 'center' },
    likeCount: { fontSize: 12, color: COLOR.TEXT.GRAY_MEDIUM },
    time: { fontSize: 12, color: COLOR.TEXT.GRAY_MEDIUM }
});