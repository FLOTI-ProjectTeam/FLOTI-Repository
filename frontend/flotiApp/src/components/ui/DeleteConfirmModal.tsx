import { View, Text, StyleSheet, Modal, TouchableOpacity } from 'react-native';

import COLOR from '@/constants/colors';

export default function DeleteConfirmModal({
    visible, title, onCancel, onDelete
}: {
    visible: boolean;
    title: string;
    onCancel: () => void;
    onDelete: () => void;
}) {
    return (
        <Modal visible={visible}  transparent animationType='fade' onRequestClose={onCancel}>
            <TouchableOpacity activeOpacity={1} style={styles.modalOverlay} onPress={onCancel}>
                <View style={styles.modalBox}>
                    <Text style={styles.modalTitle}>{title}</Text>
        
                    <View style={styles.modalActions}>
                        <TouchableOpacity activeOpacity={0.5} style={styles.modalButton} onPress={onCancel}>
                            <Text style={styles.buttonText}>아니요</Text>
                        </TouchableOpacity>
                        <TouchableOpacity
                            activeOpacity={0.5}
                            style={styles.modalButton}
                            onPress={onDelete}
                        >
                            <Text style={[styles.buttonText, styles.actionText]}>예</Text>
                        </TouchableOpacity>
                    </View>
                </View>
            </TouchableOpacity>
        </Modal>
    );
}

const styles = StyleSheet.create({
    modalOverlay: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: COLOR.OVERLAY
    },
    modalBox: {
        width: '80%',
        backgroundColor: 'white',
        borderRadius: 12,
        alignItems: 'center'
    },
    modalTitle: { fontSize: 16, fontWeight: 600, marginVertical: 25 },
    modalActions: { flexDirection: 'row', width: '100%', gap: 2 },
    modalButton: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        fontWeight: 600,
        paddingVertical: 14,
        borderRadius: 12
    },
    buttonText: { fontSize: 16 },
    actionText: { color: 'red' }
});