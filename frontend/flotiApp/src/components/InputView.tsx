import { TextInput, StyleSheet } from "react-native";

import COLOR from "@/constants/colors";

export function CreateInputView() {
    return (
        <>
            <TextInput
                style={styles.input}
                placeholder='제목을 입력하세요'
                placeholderTextColor={COLOR.TEXT.GRAY_MEDIUM}
            />
            <TextInput
                style={[styles.input, styles.textArea]}
                placeholder='내용을 입력하세요'
                multiline
                textAlignVertical='top'
                placeholderTextColor={COLOR.TEXT.GRAY_MEDIUM}
            />
      </>
    );
}

export function UpdateInputView({
    title, content, onChangeTitle, onChangeContent
}: {
    title: string;
    content: string;
    onChangeTitle: (title: string) => void;
    onChangeContent: (content: string) => void;
}) {
    return (
        <>
            <TextInput
                style={styles.input}
                value={title}
                onChangeText={onChangeTitle}
                placeholder='제목을 입력하세요'
                placeholderTextColor={COLOR.TEXT.GRAY_MEDIUM}
            />
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
    textArea: { 
        flex: 1,
        fontSize: 15,
        fontWeight: 'normal'
    }
  });