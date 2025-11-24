import { TouchableOpacity, View, StyleSheet } from 'react-native';
import { useState } from 'react';

import BreakAllText from '@/components/ui/BreakAllText';
import { IconSymbol } from '@/components/ui/IconSymbol';
import COLOR from '@/constants/colors';

export default function QnaInfo({
    title, content
}: {
    title: string;
    content: string;
}) {
    const [expanded, setExpanded] = useState(false);  // 펼침 여부

    return (
        <View style={styles.postContainer}>
            <BreakAllText style={styles.title}>{title}</BreakAllText>
            {expanded && <BreakAllText style={styles.content}>{content}</BreakAllText>}

            <TouchableOpacity activeOpacity={0.7} style={styles.iconContainer} onPress={() => setExpanded(prev => !prev)}>
                <View style={styles.iconWrapper}>
                    <IconSymbol
                        name={expanded ? "chevron.up" : "chevron.down"} // 펼침 여부에 따라 아이콘 변경
                        size={20}
                        color={COLOR.TINT.GRAY_DARK}
                    />
                </View>
            </TouchableOpacity>
        </View>
    );
}

const styles = StyleSheet.create({
    postContainer: {
        position: 'relative',
        paddingVertical: 20, paddingHorizontal: 16,
        backgroundColor: COLOR.BACKGROUND.SLATE_LIGHT,
        marginBottom: 20,
        gap: 8
    },
    title: { fontSize: 18, fontWeight: 700, color: COLOR.TEXT.GRAY_DARK },
    content: { fontSize: 15, lineHeight: 22, color: COLOR.TEXT.GRAY_DARK },
    iconContainer: { 
        position: 'absolute', 
        bottom: -16, 
        left: 0, right: 0, 
        alignItems: 'center' 
    },
    iconWrapper: {
        width: 32, height: 32,
        borderRadius: 16,
        borderWidth: 1,
        borderColor: COLOR.TINT.SLATE_SOFT,
        backgroundColor: 'white',
        justifyContent: 'center',
        alignItems: 'center'
    }
});