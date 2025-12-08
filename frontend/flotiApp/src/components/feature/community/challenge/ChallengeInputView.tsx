import { View, Text, StyleSheet, TextInput, TouchableOpacity, Platform } from 'react-native';
import { useState, createElement } from 'react';
import ParticipantDropdown from '@/components/feature/community/discussion/ParticipantDropdown';
import COLOR from '@/constants/colors';
import { STYLE, SHADOW } from '@/constants/styles';
import DateTimePicker from '@react-native-community/datetimepicker';
import dayjs from 'dayjs';

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

    const isWeb = Platform.OS === 'web';
    const [showStart, setShowStart] = useState(false);
    const [showEnd, setShowEnd] = useState(false);

    const handleDateChange = (type: 'start' | 'end', event: any, selectedDate?: Date) => {
        // Mobile Only Logic
        if (Platform.OS === 'android') {
            if (type === 'start') setShowStart(false);
            else setShowEnd(false);
        }

        if (selectedDate) {
            const formatted = dayjs(selectedDate).format('YYYY.MM.DD');
            if (type === 'start') onChangeStartDate(formatted);
            else onChangeEndDate(formatted);
        }
    };

    // Web-specific raw input renderer
    const renderWebInput = (type: 'start' | 'end', valueStr: string) => {
        const isoValue = valueStr ? dayjs(valueStr, 'YYYY.MM.DD').format('YYYY-MM-DD') : '';

        return createElement('input', {
            type: 'date',
            value: isoValue,
            style: {
                position: 'absolute',
                top: 0,
                left: 0,
                width: '100%',
                height: '100%',
                opacity: 0,
                zIndex: 20,
                cursor: 'pointer',
                border: 'none'
            },
            onClick: (e: any) => {
                // Force picker to show on click
                try {
                    if (e.target && typeof e.target.showPicker === 'function') {
                        e.target.showPicker();
                    }
                } catch (err) {
                    console.log('showPicker not supported', err);
                }
            },
            onChange: (e: any) => {
                const newVal = e.target.value; // YYYY-MM-DD
                if (newVal) {
                    const formatted = dayjs(newVal).format('YYYY.MM.DD');
                    if (type === 'start') onChangeStartDate(formatted);
                    else onChangeEndDate(formatted);
                }
            }
        });
    };

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
                <Text style={styles.counter}>{title.length}/100</Text>
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
                <Text style={styles.counter}>{content.length}/100</Text>
            </View>

            {/* 상세 내용 */}
            <View style={styles.card}>
                <Text style={styles.label}>📝 상세 내용</Text>
                <TextInput
                    style={styles.textArea}
                    value={detail}
                    onChangeText={onChangeDetail}
                    placeholder={'예:\n\u2022 목표: 하루 30분 이상 독서 후 인증\n\u2022 인증 방식: 피드에 읽은 책 & 느낀 점 공유하기'}
                    placeholderTextColor={COLOR.TEXT.GRAY_MEDIUM}
                    multiline
                />
            </View>

            {/* 기간 */}
            <View style={styles.card}>
                <Text style={styles.label}>⏰ 기간</Text>
                <View style={[styles.dateRow, { zIndex: 10 }]}>
                    {/* Start Date */}
                    <View style={styles.dateBox}>
                        <Text style={[styles.dateInput, !startDate && { color: COLOR.TEXT.GRAY_LIGHT }]} pointerEvents="none">
                            {startDate || '시작일'}
                        </Text>
                        {isWeb ? (
                            // Render Raw HTML Input for Web to force showPicker
                            renderWebInput('start', startDate)
                        ) : (
                            <TouchableOpacity
                                style={styles.mobileTouchArea}
                                onPress={() => setShowStart(true)}
                            />
                        )}
                    </View>

                    <Text style={{ fontSize: 16, color: COLOR.TINT.SLATE }}>~</Text>

                    {/* End Date */}
                    <View style={styles.dateBox}>
                        <Text style={[styles.dateInput, !endDate && { color: COLOR.TEXT.GRAY_LIGHT }]} pointerEvents="none">
                            {endDate || '종료일'}
                        </Text>
                        {isWeb ? (
                            renderWebInput('end', endDate)
                        ) : (
                            <TouchableOpacity
                                style={styles.mobileTouchArea}
                                onPress={() => setShowEnd(true)}
                            />
                        )}
                    </View>
                </View>

                {/* Mobile Pickers (Modal) */}
                {!isWeb && showStart && (
                    <DateTimePicker
                        value={startDate ? dayjs(startDate, 'YYYY.MM.DD').toDate() : new Date()}
                        mode="date"
                        display={Platform.OS === 'ios' ? 'spinner' : 'default'}
                        onChange={(e, d) => handleDateChange('start', e, d)}
                    />
                )}
                {!isWeb && showEnd && (
                    <DateTimePicker
                        value={endDate ? dayjs(endDate, 'YYYY.MM.DD').toDate() : new Date()}
                        mode="date"
                        display={Platform.OS === 'ios' ? 'spinner' : 'default'}
                        onChange={(e, d) => handleDateChange('end', e, d)}
                    />
                )}
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
                        style={{ fontSize: 14, color: '#7D8CFF', flex: 1 }}
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
        height: 48,
        alignItems: 'center',
        justifyContent: 'center',
        position: 'relative',
        overflow: 'hidden'
    },
    dateInput: { fontSize: 14, color: COLOR.TEXT.GRAY_DARK, textAlign: 'center' },
    mobileTouchArea: {
        ...StyleSheet.absoluteFillObject,
        zIndex: 10
    },
    tagInputContainer: {
        backgroundColor: '#F0F4FF',
        borderRadius: 20,
        paddingHorizontal: 12,
        paddingVertical: 6,
        alignSelf: 'flex-start',
        borderWidth: 1,
        borderColor: '#E0E6FF',
        flexDirection: 'row',
        minWidth: 100
    }
});
