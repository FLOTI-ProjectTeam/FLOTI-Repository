import { Text, TextStyle } from 'react-native';

export default function BreakAllText({ 
    children, style 
}: {
    children: string | null;   // React 태그 내부 문자열
    style?: TextStyle;
}) {
    const text = children ?? '';
    return <Text style={style}>{text.split('').join('\u200B')}</Text>;
}