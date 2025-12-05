import { View, Text, StyleSheet } from 'react-native';

import { formatDetailTime } from '@/utils/time';

import { QnaPostResponse } from '@/types/community/qna';

import BreakAllText from '@/components/ui/BreakAllText';

import COLOR from '@/constants/colors';

export default function QnaDetailHeader({ post }: { post: QnaPostResponse }) {
    return (
        <>
            {/* 제목 */}
            <BreakAllText style={styles.title}>{post.title}</BreakAllText>

            {/* 작성자, 작성일 */}
            <Text style={styles.author}>{post.author.nickname}</Text>
            <Text style={styles.time}>{formatDetailTime(post.createdAt)}</Text>

            {/* 본문 */}
            <View style={styles.contentContainer}>
                <BreakAllText style={styles.content}>{post.content}</BreakAllText>
            </View>

            {/* 답변수 */}
            <View style={styles.answerHeader}>
                <Text style={styles.answerLabel}>답변 </Text>
                <Text style={[styles.answerLabel, styles.answerCount]}>{post.answerCount}</Text>
            </View>
        </>
    );
}

const styles = StyleSheet.create({
    title: {
        marginBottom: 8,
        fontSize: 20,
        fontWeight: 700,
        color: 'black'
    },
    author: { fontSize: 14, fontWeight: 600, color: COLOR.TEXT.GRAY_DARK },
    time: { fontSize: 12, color: COLOR.TEXT.GRAY_MEDIUM },
    contentContainer: {
        marginTop: 12,
        padding: 16,
        borderRadius: 12,
        backgroundColor: COLOR.BACKGROUND.SLATE_LIGHT
    },
    content: { fontSize: 15, lineHeight: 22, color: COLOR.TEXT.GRAY_DARK },
    answerHeader: {
        flexDirection: 'row',
        alignItems: 'center',
        marginTop: 16,
        paddingVertical: 12,
        borderTopWidth: 1,
        borderTopColor: COLOR.TINT.GRAY
    },
    answerLabel: { fontSize: 18, fontWeight: 600, color: COLOR.TEXT.GRAY_DARK },
    answerCount: { color: 'skyblue' }
});