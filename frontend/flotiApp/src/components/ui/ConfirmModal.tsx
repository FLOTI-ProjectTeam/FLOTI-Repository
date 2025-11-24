import { View, Text, StyleSheet, Modal, TouchableOpacity, Pressable } from 'react-native';

import COLOR from '@/constants/colors';

export default function ConfirmModal({
    visible, title, onClose, onAction
}: {
    visible: boolean;
    title: string;
    onClose: () => void;
    onAction?: () => void;
}) {
    return (
        <Modal visible={visible} transparent animationType='fade' onRequestClose={onClose}>
            <Pressable style={styles.modalOverlay} onPress={onClose}>
                <View style={styles.modalBox}>
                    <Text style={styles.modalTitle}>{title}</Text>
                    <View style={styles.modalActions}>
                        <TouchableOpacity activeOpacity={0.5} style={styles.modalButton} onPress={onClose}>
                            <Text style={styles.buttonText}>{onAction ? '아니요' : '확인'}</Text>
                        </TouchableOpacity>
                        {onAction && (
                            <TouchableOpacity
                                activeOpacity={0.5}
                                style={styles.modalButton}
                                onPress={onAction}
                            >
                                <Text style={[styles.buttonText, styles.actionText]}>예</Text>
                            </TouchableOpacity>
                        )}
                    </View>
                </View>
            </Pressable>
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