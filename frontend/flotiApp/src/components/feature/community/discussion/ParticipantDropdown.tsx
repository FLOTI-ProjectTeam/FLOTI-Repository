import { View, StyleSheet, Text, TouchableOpacity, Modal, Pressable } from 'react-native';
import { useState } from 'react';

import { IconSymbol } from '@/components/ui/IconSymbol';
import COLOR from '@/constants/colors';

export default function ParticipantDropdown({
    value, onChange
}: {
    value: number;
    onChange: (value: number) => void;
}) {
    const [popupVisible, setPopupVisible] = useState(false);
    const options = [2, 3, 4, 5];
    const currentLabel = `${value}명`;
  
    return (
      <>
        {/* 버튼 */}
        <TouchableOpacity activeOpacity={0.7} style={styles.participantButton} onPress={() => setPopupVisible(true)}>
            <Text style={styles.optionText}>{currentLabel}</Text>
            <IconSymbol name="chevron.down" size={16} color={COLOR.TEXT.NAVY} />
        </TouchableOpacity>
  
        {/* 팝업 */}
        <Modal transparent visible={popupVisible} animationType='fade' onRequestClose={() => setPopupVisible(false)}>
            <Pressable style={styles.overlay} onPress={() => setPopupVisible(false)}>
            <View style={styles.modalContent}>
                {options.map((option) => (
                    <TouchableOpacity
                        key={option}
                        style={styles.option}
                        onPress={() => {
                            onChange(option);
                            setPopupVisible(false);
                        }}
                    >
                    <Text style={[
                        styles.optionText, 
                        value === option && styles.optionTextActive // 선택 여부에 따라 스타일 추가
                    ]}>
                        {option}명
                    </Text>
                    {value === option && <IconSymbol name="check" size={18} color={COLOR.TINT.SLATE} />}
                    </TouchableOpacity>
                ))}
            </View>
          </Pressable>
        </Modal>
      </>
    );
}
  
const styles = StyleSheet.create({
    participantButton: {
        flexDirection: 'row', 
        alignItems: 'center', 
        justifyContent: 'space-between', 
        marginHorizontal: 12,
        paddingVertical: 12, paddingHorizontal: 16,
        width: 100,
        borderWidth: 1, 
        borderColor: COLOR.TINT.SLATE_SOFT, 
        borderRadius: 12
    },
    overlay: { flex: 1, justifyContent: 'center', alignItems: 'center', backgroundColor: COLOR.OVERLAY },
    modalContent: {
        position: 'relative',
        backgroundColor: 'white',
        borderRadius: 8,
        width: 200,
        paddingVertical: 16, paddingHorizontal: 20,
        shadowColor: 'black',
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.25,
        shadowRadius: 3.84,
        elevation: 5,
        gap: 16
    },
    option: { flexDirection: 'row', justifyContent: 'space-between' },
    optionText: { fontSize: 15, color: COLOR.TEXT.NAVY },
    optionTextActive: { color: COLOR.TINT.SLATE, fontWeight: 600 }
});