import { Text, TextInput, StyleSheet, View } from 'react-native';

import ParticipantDropdown from '@/components/feature/community/discussion/ParticipantDropdown';
import COLOR from '@/constants/colors';
import { STYLE } from '@/constants/styles';

export function DiscussionInputView({ 
    title, content, maxParticipantCount, onChangeTitle, onChangeContent, onChangeMaxParticipantCount
}: {
    title: string;
    content: string;
    maxParticipantCount: number;
    onChangeTitle: (title: string) => void;
    onChangeContent: (content: string) => void;
    onChangeMaxParticipantCount: (maxParticipantCount: number) => void;
}) {
    return (
        <View style={styles.cardContainer}>
            <View style={STYLE.CARD_OUTLINE}>
                <Text style={styles.label}>🔥 토론 제목</Text>
                <TextInput
                    style={[styles.input, styles.inputLine]}
                    value={title}
                    onChangeText={onChangeTitle}
                    placeholder='제목을 입력하세요'
                    placeholderTextColor={COLOR.TEXT.GRAY_MEDIUM}
                />
            </View>
            
            <View style={STYLE.CARD_OUTLINE}>
                <Text style={styles.label}>📌 토론 내용</Text>
                <TextInput
                    style={styles.input}
                    value={content}
                    onChangeText={onChangeContent}
                    placeholder='내용을 입력하세요'
                    placeholderTextColor={COLOR.TEXT.GRAY_MEDIUM}
                    textAlignVertical='top'
                    multiline
                />
            </View>

            <View style={STYLE.CARD_OUTLINE}>
                <Text style={styles.label}>👥 참여 가능 인원</Text>
                <ParticipantDropdown value={maxParticipantCount} onChange={onChangeMaxParticipantCount}
                />
            </View>
      </View>
    );
}

const styles = StyleSheet.create({
    cardContainer: { ...STYLE.WRAPPER, paddingVertical: 20, gap: 8 }, 
    label: { fontSize: 16, fontWeight: 700, color: COLOR.TEXT.GRAY_DARK },
    input: { 
        flex: 1,
        marginHorizontal: 8,
        paddingVertical: 6, 
        fontSize: 15, 
        backgroundColor: 'white' 
    },
    inputLine: { borderBottomWidth: 1, borderBottomColor: COLOR.TINT.SLATE_SOFT }
});