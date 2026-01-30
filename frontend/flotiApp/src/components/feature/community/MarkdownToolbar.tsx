import { View, StyleSheet, TouchableOpacity } from 'react-native';
import { MaterialCommunityIcons } from '@expo/vector-icons';

import COLOR from '@/constants/colors';

const ICON_SIZE = 24;
const ICON_COLOR = COLOR.TEXT.GRAY_CHARCOAL;

export default function MarkdownToolbar({
    content, onChangeContent
}: {
    content: string;
    onChangeContent: (text: string) => void;
}) {
    const insert = (syntax: string) => onChangeContent(content + syntax);

    return (
        <View style={styles.toolbar}>
            <TouchableOpacity style={styles.toolButton} onPress={() => insert('**굵게**')}>
                <MaterialCommunityIcons name="format-bold" size={ICON_SIZE} color={ICON_COLOR} />
            </TouchableOpacity>

            <TouchableOpacity style={styles.toolButton} onPress={() => insert('*기울임*')}>
                <MaterialCommunityIcons name="format-italic" size={ICON_SIZE} color={ICON_COLOR} />
            </TouchableOpacity>

            <TouchableOpacity style={styles.toolButton} onPress={() => insert('~~취소선~~')}>
                <MaterialCommunityIcons name="format-strikethrough" size={ICON_SIZE} color={ICON_COLOR} />
            </TouchableOpacity>

            <TouchableOpacity style={styles.toolButton} onPress={() => insert('\n> 인용')}>
                <MaterialCommunityIcons name="format-quote-close" size={ICON_SIZE} color={ICON_COLOR} />
            </TouchableOpacity>

            <TouchableOpacity style={styles.toolButton} onPress={() => insert('\n- 리스트')}>
                <MaterialCommunityIcons name="format-list-bulleted" size={ICON_SIZE} color={ICON_COLOR} />
            </TouchableOpacity>

            <TouchableOpacity style={styles.toolButton} onPress={() => insert('\n[링크](https://)')}>
                <MaterialCommunityIcons name="link-variant" size={ICON_SIZE} color={ICON_COLOR} />
            </TouchableOpacity>
        </View>
    );
}

const styles = StyleSheet.create({
    toolbar: {
        flexDirection: 'row',
        alignItems: 'center',
        paddingVertical: 12, paddingHorizontal: 24,
        gap: 12
    },
    toolButton: {
        flex: 1,
        padding: 8,
        borderRadius: 8,
        backgroundColor: COLOR.BACKGROUND.SLATE_LIGHT,
        alignItems: 'center',
        justifyContent: 'center'
    }
});