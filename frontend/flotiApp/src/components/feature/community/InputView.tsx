import { View, TextInput, StyleSheet, Image, TouchableOpacity, Text, ScrollView } from 'react-native';
import * as ImagePicker from 'expo-image-picker';
import { Ionicons } from '@expo/vector-icons';

import COLOR from '@/constants/colors';
import { ImageFile } from '@/types/community/common';

type TextInputProps = {
    title?: string;
    content: string;
    onChangeTitle?: (title: string) => void;
    onChangeContent: (content: string) => void;
};

function TextInputSection({
    title, content, onChangeTitle, onChangeContent
}: TextInputProps) {
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
        </>
    );
}

export function TipInputView({
    title, content, file, thumbnail, onChangeTitle, onChangeContent, onChangeFile, onDeleteThumbnail
}: TextInputProps & {
    file: ImageFile;
    thumbnail?: string | null;
    onChangeTitle: (title: string) => void;
    onChangeContent: (content: string) => void;
    onChangeFile: (file: ImageFile | undefined) => void;
    onDeleteThumbnail?: () => void;
}) {
    /* 이벤트 핸들러 */
    const handlePickImage = async () => {
        const result = await ImagePicker.launchImageLibraryAsync({
            mediaTypes: ['images'],
            allowsEditing: true,
            aspect: [1, 1],
            quality: 1
        });

        if (!result.canceled && onChangeFile) {
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

    return (
        <ScrollView
            style={styles.container}
            contentContainerStyle={styles.contentContainer}
            keyboardShouldPersistTaps='handled'
        >
            {imageSource ? (
                <View style={styles.coverImageContainer}>
                    <View style={styles.imageTouchable}>
                        <Image source={imageSource} style={styles.imagePreview} />
                        <View style={styles.imageOverlay} />
                    </View>
                    <TouchableOpacity onPress={handleRemoveImage} style={styles.deleteButton}>
                        <Ionicons name="close-circle" size={28} color={COLOR.TEXT.GRAY_CHARCOAL} />
                    </TouchableOpacity>
                </View>
            ) : (
                <TouchableOpacity onPress={handlePickImage} style={styles.addCoverButton}>
                    <Ionicons name="camera-outline" size={20} color={COLOR.TEXT.GRAY_CHARCOAL} />
                    <Text style={styles.addCoverText}>대표 이미지 등록</Text>
                </TouchableOpacity>
            )}

            <TextInputSection
                title={title}
                content={content}
                onChangeTitle={onChangeTitle}
                onChangeContent={onChangeContent}
            />
        </ScrollView>
    );
}

export function QnaInputView({
    title, content, onChangeTitle, onChangeContent
}: TextInputProps) {
    return (
        <ScrollView
            style={styles.container}
            contentContainerStyle={styles.contentContainer}
            keyboardShouldPersistTaps='handled'
        >
            <TextInputSection
                title={title}
                content={content}
                onChangeTitle={onChangeTitle}
                onChangeContent={onChangeContent}
            />
        </ScrollView>
    );
}

const styles = StyleSheet.create({
    container: { flex: 1, backgroundColor: 'white' },
    contentContainer: { flexGrow: 1, paddingBottom: 40 },
    coverImageContainer: {
        width: '100%',
        height: 200,
        backgroundColor: COLOR.BACKGROUND.SLATE_LIGHT,
        marginBottom: 20,
        position: 'relative'
    },
    addCoverButton: {
        flexDirection: 'row',
        alignItems: 'center',
        paddingVertical: 12,
        paddingHorizontal: 24,
        gap: 6
    },
    addCoverText: { fontSize: 14, fontWeight: '600', color: COLOR.TEXT.GRAY_CHARCOAL },
    imageTouchable: { width: '100%', height: '100%' },
    imagePreview: { width: '100%', height: '100%', resizeMode: 'cover' },
    imageOverlay: { ...StyleSheet.absoluteFillObject, backgroundColor: 'rgba(0,0,0,0.05)' },
    deleteButton: {
        position: 'absolute',
        top: 12,
        right: 12,
        backgroundColor: 'rgba(255,255,255,0.8)',
        borderRadius: 20,
        padding: 4
    },
    titleInput: {
        paddingVertical: 12,
        paddingHorizontal: 24,
        fontSize: 22,
        fontWeight: '700',
        color: COLOR.TEXT.GRAY_DARK,
        borderBottomColor: COLOR.TINT.GRAY,
        borderBottomWidth: 1
    },
    contentInput: {
        minHeight: 300,
        fontSize: 16,
        lineHeight: 24,
        color: COLOR.TEXT.GRAY_DARK,
        paddingHorizontal: 24,
        paddingBottom: 24
    }
});