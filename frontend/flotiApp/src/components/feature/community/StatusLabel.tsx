import { View, Text, StyleSheet } from 'react-native';

import COLOR from '@/constants/colors';

export default function StatusLabel({ type }: { type: 'JOINED' | 'FULL' | 'ACCEPTED' }) {
    let label = '채택';
    let backgroundColor = COLOR.BUTTON.MINT;

    if (type === 'JOINED') {
        label = '참여 중';
        backgroundColor = COLOR.BUTTON.SLATE_DARK;
    } else if (type === 'FULL') {
        label = '참여 불가';
        backgroundColor = COLOR.BUTTON.RED;
    }

    const styles = StyleSheet.create({
        container: { paddingHorizontal: 6, paddingVertical: 2, borderRadius: 4, backgroundColor },
        text: { fontSize: 10, fontWeight: 'bold', color: 'white' }
    });

    return (
        <View style={styles.container}>
            <Text style={styles.text}>{label}</Text>
        </View>
    );
}