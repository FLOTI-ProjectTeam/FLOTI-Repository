import { View, Text, StyleSheet } from 'react-native';
import COLOR from '@/constants/colors';

type StatusType = 'JOINED' | 'FULL' | 'ACCEPTED';

interface StatusLabelProps {
    type: StatusType;
}

export default function StatusLabel({ type }: StatusLabelProps) {
    let label = '';
    let backgroundColor = '';

    switch (type) {
        case 'JOINED':
            label = '참여 중';
            backgroundColor = COLOR.BUTTON.SLATE_DARK;
            break;
        case 'FULL':
            label = '참여 불가';
            backgroundColor = COLOR.BUTTON.RED;
            break;
        case 'ACCEPTED':
            label = '채택';
            backgroundColor = COLOR.BUTTON.MINT;
            break;
    }

    return (
        <View style={[styles.container, { backgroundColor }]}>
            <Text style={styles.text}>{label}</Text>
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        paddingHorizontal: 6,
        paddingVertical: 2,
        borderRadius: 4,
    },
    text: {
        fontSize: 10,
        fontWeight: 'bold',
        color: 'white'
    },
});
