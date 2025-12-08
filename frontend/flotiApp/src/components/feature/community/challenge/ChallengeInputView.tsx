import { View, Text, StyleSheet, TextInput } from 'react-native';
import ParticipantDropdown from '@/components/feature/community/discussion/ParticipantDropdown';
import COLOR from '@/constants/colors';
import { STYLE, SHADOW } from '@/constants/styles';

interface Props {
    title: string;
    content: string;
    detail: string;
    startDate: string;
    endDate: string;
    maxParticipants: number;
    tags: string;
    onChangeTitle: (text: string) => void;
    onChangeContent: (text: string) => void;
    onChangeDetail: (text: string) => void;
    onChangeStartDate: (text: string) => void;
    onChangeEndDate: (text: string) => void;
    onChangeMaxParticipants: (value: number) => void;
    onChangeTags: (text: string) => void;
}

export default function ChallengeInputView({
    title, content, detail, startDate, endDate, maxParticipants, tags,
    onChangeTitle, onChangeContent, onChangeDetail, onChangeStartDate, onChangeEndDate, onChangeMaxParticipants, onChangeTags
}: Props) {
    return (
        <View style={styles.container}>
            {/* 챌린지 제목 */}
            <View style={styles.card}>
                <Text style={styles.label}>🔥 챌린지 제목</Text>
                <TextInput
                    style={styles.inputUnderline}
                    value={title}
                    onChangeText={onChangeTitle}
                    placeholder='예: 7일 동안 하루 30분 독서 챌린지'
                    placeholderTextColor={COLOR.TEXT.GRAY_LIGHT}
                    maxLength={100}
                />
                <Text style={styles.counter}>0/100</Text>
            </View>

            {/* 한줄 소개 */}
            <View style={styles.card}>
                <Text style={styles.label}>📌 한줄 소개</Text>
                <TextInput
                    style={styles.inputUnderline}
                    value={content}
                    onChangeText={onChangeContent}
                    placeholder='예: 매일 30분씩 책을 읽으며 꾸준한 습관을 만들어봐요!'
                    placeholderTextColor={COLOR.TEXT.GRAY_LIGHT}
                    maxLength={100}
                />
                <Text style={styles.counter}>0/100</Text>
            </View>

            {/* 상세 내용 */}
            <View style={styles.card}>
                <Text style={styles.label}>📝 상세 내용</Text>
                <TextInput
                    style={styles.textArea}
                    value={detail}
                    onChangeText={onChangeDetail}
                    placeholder={'예:\n• 목표: 하루 30분 이상 독서 후 인증\n• 인증 방식: 피드에 읽은 책 & 느낀 점 공유하기'}
                    placeholderTextColor={COLOR.TEXT.GRAY_MEDIUM}
                    multiline
                />
            </View>

            {/* 기간 */}
            <View style={styles.card}>
                <Text style={styles.label}>⏰ 기간</Text>
                <View style={styles.dateRow}>
                    <View style={styles.dateBox}>
                        <TextInput
                            style={styles.dateInput}
                            value={startDate}
                            onChangeText={onChangeStartDate}
                            placeholder='2025.01.01'
                            keyboardType='numeric'
                        />
                    </View>
                    <Text style={{ fontSize: 16, color: COLOR.TINT.SLATE }}>~</Text>
                    <View style={styles.dateBox}>
                        <TextInput
                            style={styles.dateInput}
                            value={endDate}
                            onChangeText={onChangeEndDate}
                            placeholder='2025.01.07'
                            keyboardType='numeric'
                        />
                    </View>
                </View>
            </View>

            {/* 참여 가능 인원 */}
            <View style={styles.card}>
                <Text style={styles.label}>👥 참여 가능 인원</Text>
                <View style={{ width: 140 }}>
                    <ParticipantDropdown value={maxParticipants} onChange={onChangeMaxParticipants} />
                </View>
            </View>

            {/* 태그 */}
            <View style={styles.card}>
                <Text style={styles.label}>🏷️ 태그</Text>
                <View style={styles.tagInputContainer}>
                    <TextInput
                        style={{ fontSize: 14, color: '#7D8CFF' }}
                        value={tags}
                        onChangeText={onChangeTags}
                        placeholder='#태그 입력'
                        placeholderTextColor={'#B0C4DE'}
                    />
                </View>
            </View>
        </View>
    );
}

const styles = StyleSheet.create({
    container: { gap: 16 },
    card: {
        backgroundColor: 'white',
        borderRadius: 12,
        padding: 16,
        paddingBottom: 12,
        ...SHADOW
    },
    label: { fontSize: 15, fontWeight: '700', color: COLOR.TEXT.GRAY_DARK, marginBottom: 12 },
    inputUnderline: {
        fontSize: 14,
        paddingVertical: 8,
        borderBottomWidth: 1,
        borderBottomColor: COLOR.TINT.GRAY_LIGHT,
        color: COLOR.TEXT.GRAY_DARK
    },
    counter: { textAlign: 'right', fontSize: 11, color: COLOR.TEXT.GRAY_LIGHT, marginTop: 4 },
    textArea: {
        minHeight: 100,
        fontSize: 14,
        lineHeight: 20,
        color: COLOR.TEXT.GRAY_DARK,
        textAlignVertical: 'top'
    },
    dateRow: { flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between', gap: 8 },
    dateBox: {
        flex: 1,
        borderWidth: 1,
        borderColor: COLOR.TINT.SLATE_SOFT,
        borderRadius: 8,
        paddingVertical: 10,
        alignItems: 'center'
    },
    dateInput: { fontSize: 14, color: COLOR.TEXT.GRAY_DARK, textAlign: 'center' },
    tagInputContainer: {
        backgroundColor: '#F0F4FF',
        borderRadius: 20,
        paddingHorizontal: 12,
        paddingVertical: 6,
        alignSelf: 'flex-start',
        borderWidth: 1,
        borderColor: '#E0E6FF'
    }
});
