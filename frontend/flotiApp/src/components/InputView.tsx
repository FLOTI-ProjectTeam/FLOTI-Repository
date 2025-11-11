import { TextInput, StyleSheet } from 'react-native';

import COLOR from '@/constants/colors';

export default function InputView({ 
    title, content, file, onChangeTitle, onChangeContent, onChangeFile 
}: {
    title?: string;
    content?: string;
    file?: File;
    onChangeTitle?: (title: string) => void;
    onChangeContent: (content: string) => void;
    onChangeFile?: (file: File) => void;
}) {
    return (
        <>
            {onChangeTitle && (
                <TextInput
                    style={styles.input}
                    value={title}
                    onChangeText={onChangeTitle}
                    placeholder='제목을 입력하세요'
                    placeholderTextColor={COLOR.TEXT.GRAY_MEDIUM}
                />
            )}
            <TextInput
                style={[styles.input, styles.textArea]}
                value={content}
                onChangeText={onChangeContent}
                placeholder='내용을 입력하세요'
                multiline
                textAlignVertical='top'
                placeholderTextColor={COLOR.TEXT.GRAY_MEDIUM}
            />
      </>
    );
}

const styles = StyleSheet.create({
    input: {
        borderWidth: 0,
        paddingVertical: 12,
        marginHorizontal: 24,
        fontSize: 20,
        fontWeight: 'bold',
        backgroundColor: 'white'
    },
    textArea: { flex: 1, fontSize: 15, fontWeight: 'normal' }
});