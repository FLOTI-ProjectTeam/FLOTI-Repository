import { View, Text, TouchableOpacity, Modal, StyleSheet } from 'react-native';
import { useState } from 'react';

import { IconSymbol } from '@/components/ui/IconSymbol';
import COLOR from '@/constants/colors';

export type SortOption = {
  value: string;
  label: string;
};

export default function SortDropdown({ 
  options, selectedValue, onSelect 
}: {
  options: SortOption[];
  selectedValue: string;
  onSelect: (value: string) => void;
}) {
  const [showModal, setShowModal] = useState(false);
  const currentLabel = options.find(option => option.value === selectedValue)?.label || options[0].label;

  return (
    <>
      {/* 정렬 버튼 */}
      <View style={styles.sortContainer}>
        <TouchableOpacity
          activeOpacity={1} // 클릭 시 투명도 설정
          style={styles.sortButton}
          onPress={() => setShowModal(true)}
        >
          <Text style={styles.sortText}>{currentLabel}</Text>
          <IconSymbol name="chevron.down" size={16} color={COLOR.TEXT.NAVY} />
        </TouchableOpacity>
      </View>

      {/* 정렬 팝업 */}
      <Modal transparent visible={showModal} onRequestClose={() => setShowModal(false)}>
        <TouchableOpacity activeOpacity={1} style={styles.overlay} onPress={() => setShowModal(false)}>
          <View style={styles.modalContent}>
            {options.map((option) => (
              <TouchableOpacity
                activeOpacity={0.5}
                key={option.value}
                style={styles.sortOption}
                onPress={() => {
                  onSelect(option.value);
                  setShowModal(false);
                }}
              >
                <Text style={[
                  styles.sortText,
                  selectedValue === option.value && styles.sortTextActive
                ]}>
                  {option.label}
                </Text>
                {selectedValue === option.value && (
                  <IconSymbol name="check" size={18} color={COLOR.TINT.SLATE} />
                )}
              </TouchableOpacity>
            ))}
          </View>
        </TouchableOpacity>
      </Modal>
    </>
  );
}

const styles = StyleSheet.create({
  sortContainer: {
    flexDirection: 'row',
    justifyContent: 'flex-end',
    paddingBottom: 12
  },
  sortButton: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 4
  },
  overlay: { flex: 1, alignItems: 'flex-end' },
  modalContent: {
    top: 160,
    right: 16,
    backgroundColor: 'white',
    borderRadius: 8,
    width: 120,
    shadowColor: 'black',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.25,
    shadowRadius: 3.84,
    elevation: 5,
    paddingVertical: 12, paddingHorizontal: 16,
    gap: 12
  },
  sortOption: { flexDirection: 'row', justifyContent: 'space-between' },
  sortText: { fontSize: 14, color: COLOR.TEXT.NAVY },
  sortTextActive: { color: COLOR.TINT.SLATE, fontWeight: 600 }
});