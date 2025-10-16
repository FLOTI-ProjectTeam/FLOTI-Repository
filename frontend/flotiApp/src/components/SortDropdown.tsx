import { View, Text, TouchableOpacity, Modal, StyleSheet } from 'react-native';
import { useState } from 'react';
import { IconSymbol } from '@/components/ui/IconSymbol';
import COLORS from '@/constants/colors';

export type SortOption = {
  value: string;
  label: string;
};

type SortDropdownProps = {
  options: SortOption[];
  selectedValue: string;
  onSelect: (value: string) => void;
};

export default function SortDropdown({ options, selectedValue, onSelect }: SortDropdownProps) {
  const [showModal, setShowModal] = useState(false);

  const currentLabel = options.find(opt => opt.value === selectedValue)?.label || options[0].label;

  return (
    <>
      <View style={styles.sortContainer}>
        <TouchableOpacity 
          style={styles.sortButton}
          onPress={() => setShowModal(true)}
        >
          <Text style={styles.sortText}>{currentLabel}</Text>
          <IconSymbol name="chevron.down" size={16} color={COLORS.TEXT.NAVY} />
        </TouchableOpacity>
      </View>

      <Modal
        visible={showModal}
        transparent
        animationType="fade"
        onRequestClose={() => setShowModal(false)}
      >
        <TouchableOpacity 
          style={styles.modalOverlay}
          activeOpacity={1}
          onPress={() => setShowModal(false)}
        >
          <View style={styles.modalContent}>
            {options.map((option) => (
              <TouchableOpacity
                key={option.value}
                style={styles.sortOption}
                onPress={() => {
                  onSelect(option.value);
                  setShowModal(false);
                }}
              >
                <Text style={[
                  styles.sortOptionText,
                  selectedValue === option.value && styles.sortOptionTextActive
                ]}>
                  {option.label}
                </Text>
                {selectedValue === option.value && (
                  <IconSymbol name="check" size={18} color={COLORS.TINT.SLATE} />
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
    paddingBottom: 12,
  },
  sortButton: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 4,
  },
  sortText: {
    fontSize: 14,
    color: COLORS.TEXT.NAVY,
  },
  modalOverlay: {
    flex: 1,
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
    justifyContent: 'flex-start',
    alignItems: 'flex-end',
    paddingTop: 120,
    paddingRight: 16,
  },
  modalContent: {
    backgroundColor: 'white',
    borderRadius: 8,
    minWidth: 120,
    shadowColor: 'black',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.25,
    shadowRadius: 3.84,
    elevation: 5,
  },
  sortOption: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingVertical: 12,
    paddingHorizontal: 16,
    borderBottomWidth: 0,
  },
  sortOptionText: {
    fontSize: 14,
    color: COLORS.TEXT.NAVY,
  },
  sortOptionTextActive: {
    color: COLORS.TINT.SLATE,
    fontWeight: '600',
  },
});