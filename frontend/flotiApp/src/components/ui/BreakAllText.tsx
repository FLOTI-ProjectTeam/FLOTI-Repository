import { Text, TextStyle, ViewStyle } from 'react-native';
import { useMemo } from 'react';
import Markdown from 'react-native-markdown-display';

import { withZeroWidthSpace } from '@/utils/markdown';

import COLOR from '@/constants/colors';

export function ContentText({
    children, style
}: {
    children: string | null;   // React 태그 내부 문자열
    style?: TextStyle;
}) {
    const text = children ?? '';
    return <Text style={style}>{text.split('').join('\u200B')}</Text>;
}

export function MarkdownText({ content }: { content: string }) {
    const renderContent = useMemo(
        () => withZeroWidthSpace(content),
        [content]
    );

    return <Markdown style={markdownStyles}>{renderContent}</Markdown>;
}

const markdownStyles: {
    [key: string]: TextStyle | ViewStyle;
} = {
    body: { fontSize: 15, lineHeight: 22, color: COLOR.TEXT.GRAY_DARK },
    paragraph: { marginTop: 0, marginBottom: 0 },
    strong: { fontWeight: 700, color: COLOR.TEXT.GRAY_DARK },
    em: { fontStyle: 'italic' },
    listItem: { flexDirection: 'row', marginBottom: 4 },
    link: { color: COLOR.TINT.MINT, textDecorationLine: 'underline' }
};