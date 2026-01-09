import { View, TextInput, StyleSheet, Image, TouchableOpacity, Text, ScrollView } from 'react-native';
import { useState } from 'react';
import * as ImagePicker from 'expo-image-picker';

import { useKeyboardHeight } from '@/hooks/useKeyboardHeight';

import { ImageFile } from '@/types/community/common';

import { IconSymbol } from '@/components/ui/IconSymbol';

import COLOR from '@/constants/colors';
import { STYLE } from '@/constants/styles';

type TextInputProps = {
    title?: string;
    content: string;
    onChangeTitle?: (title: string) => void;
    onChangeContent: (content: string) => void;
};

function TextInputSection({
    title, content, onChangeTitle, onChangeContent
}: TextInputProps) {
    const marginBottom = useKeyboardHeight(); // 키보드 높이를 하단 여백으로 사용

    return (
        <>
            {onChangeTitle && (
                <TextInput
                    style={styles.titleInput}
                    value={title}
                    onChangeText={onChangeTitle}
                    placeholder='제목을 입력하세요'
                    placeholderTextColor={COLOR.TEXT.GRAY_MEDIUM}
                />
            )}

            <ScrollView
                style={[STYLE.BASE_CONTAINER, { marginBottom }]}
                showsVerticalScrollIndicator={false}
                keyboardShouldPersistTaps='handled' // 키보드가 열린 상태에서도 onPress 이벤트 먼저 처리
            >
                <TextInput
                    style={[styles.contentInput]}
                    value={content}
                    onChangeText={onChangeContent}
                    placeholder='내용을 입력하세요'
                    placeholderTextColor={COLOR.TEXT.GRAY_MEDIUM}
                    textAlignVertical='top'
                    multiline
                    scrollEnabled={false}
                />
            </ScrollView>
        </>
    );
}

export function TipInputView({
    title, content, file, thumbnail, onChangeTitle, onChangeContent, onChangeFile, onDeleteThumbnail
}: TextInputProps & {
    file?: ImageFile;
    thumbnail?: string | null;
    onChangeFile: (file?: ImageFile) => void;
    onDeleteThumbnail?: () => void;
}) {
    const [expanded, setExpanded] = useState(true);  // 펼침 여부

    /* 이벤트 핸들러 */
    const handlePickImage = async () => {
        const result = await ImagePicker.launchImageLibraryAsync({
            mediaTypes: ['images'],
            allowsEditing: true,
            aspect: [1, 1],
            quality: 1
        });

        if (!result.canceled) {
            const asset = result.assets[0];
            const fileData = {
                uri: asset.uri,
                name: asset.fileName || 'photo.jpg',
                type: asset.mimeType || 'image/jpeg'
            };
            onChangeFile(fileData);
        }
    };

    const handleRemoveImage = () => {
        if (file && onChangeFile) onChangeFile(undefined);
        else if (thumbnail && onDeleteThumbnail) onDeleteThumbnail();
    };

    const imageSource = file ? { uri: file.uri } : (thumbnail ? { uri: thumbnail } : null);
    const hasImage = !!imageSource;

    return (
        <>
            <TouchableOpacity
                activeOpacity={0.7}
                onPress={() => setExpanded(!expanded)}
                style={styles.thumbnailHeader}
            >
                <View style={[STYLE.CENTER, STYLE.ROW]}>
                    <Text style={styles.thumbnailTitle}>대표 이미지</Text>
                    {hasImage && <Text style={styles.activeLabel}>  (등록)</Text>}
                </View>
                <IconSymbol
                    name={expanded ? "chevron.up" : "chevron.down"}
                    size={20}
                    color={COLOR.TEXT.GRAY_CHARCOAL}
                />
            </TouchableOpacity>

            {expanded && (
                <>
                    {imageSource ? (
                        <View style={styles.coverImageContainer}>
                            <View style={styles.imageTouchable}>
                                <Image source={imageSource} style={styles.imagePreview} />
                                <View style={styles.imageOverlay} />
                            </View>
                            <TouchableOpacity activeOpacity={0.7} onPress={handleRemoveImage} style={styles.deleteButton}>
                                <IconSymbol name="close" size={28} color={COLOR.TINT.GRAY_DARK} />
                            </TouchableOpacity>
                        </View>
                    ) : (
                        <TouchableOpacity activeOpacity={0.7} onPress={handlePickImage} style={styles.emptyImageContainer}>
                            <IconSymbol name="camera" size={56} color={COLOR.TEXT.GRAY_CHARCOAL} />
                        </TouchableOpacity>
                    )}
                </>
            )}

            <TextInputSection
                title={title}
                content={content}
                onChangeTitle={onChangeTitle}
                onChangeContent={onChangeContent}
            />
        </>
    );
}

export function QnaInputView({
    title, content, onChangeTitle, onChangeContent
}: TextInputProps) {
    return (
        <TextInputSection
            title={title}
            content={content}
            onChangeTitle={onChangeTitle}
            onChangeContent={onChangeContent}
        />
    );
}

const styles = StyleSheet.create({
    coverImageContainer: {
        width: '100%',
        height: 150,
        backgroundColor: COLOR.BACKGROUND.SLATE_LIGHT,
        marginBottom: 12,
        alignItems: 'center',
        justifyContent: 'center',
        position: 'relative'
    },
    imageTouchable: { width: '100%', height: '100%', borderRadius: 8 },
    imagePreview: { width: '100%', height: '100%', resizeMode: 'cover' },
    imageOverlay: { ...StyleSheet.absoluteFillObject, backgroundColor: COLOR.OVERLAY },
    deleteButton: {
        position: 'absolute',
        top: 14,
        right: 14,
        backgroundColor: 'white',
        borderRadius: 20
    },
    titleInput: {
        paddingVertical: 12,
        paddingHorizontal: 24,
        fontSize: 22,
        fontWeight: '700',
        color: COLOR.TEXT.GRAY_DARK
    },
    contentInput: {
        minHeight: 300,
        fontSize: 16,
        lineHeight: 24,
        color: COLOR.TEXT.GRAY_DARK,
        paddingHorizontal: 24,
        paddingBottom: 24
    },
    thumbnailHeader: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
        paddingVertical: 12, paddingHorizontal: 24
    },
    thumbnailTitleContainer: { flexDirection: 'row', alignItems: 'center' },
    thumbnailTitle: { fontSize: 16, fontWeight: 600, color: COLOR.TEXT.GRAY_DARK },
    activeLabel: { fontSize: 13, fontWeight: 500, color: COLOR.TINT.MINT },
    emptyImageContainer: {
        height: 150,
        backgroundColor: COLOR.BACKGROUND.SLATE_LIGHT,
        borderRadius: 8,
        justifyContent: 'center',
        alignItems: 'center',
        marginHorizontal: 24,
        marginBottom: 12
    },
});