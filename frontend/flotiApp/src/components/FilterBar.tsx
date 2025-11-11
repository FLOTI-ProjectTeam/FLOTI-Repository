import { View, Text, TouchableOpacity, Modal, StyleSheet } from 'react-native';
import { useState } from 'react';

import { IconSymbol } from '@/components/ui/IconSymbol';
import COLOR from '@/constants/colors';

export type SortType = 'latest' | 'registered' | 'likes' | 'comments' | 'recentActivity';

export type SortOption = {
  value: SortType;
  label: string;
};

type SortProps = {
  options: SortOption[];
  value: SortType;
  onChange: (value: SortType) => void;
}

type CheckProps = {
  label: string;
  value: boolean;
  onChange: (value: boolean) => void;
}

export default function FilterBar({ 
  sort, check
}: {
  sort: SortProps;
  check?: CheckProps;
}) {
  return (
    <View style={[
      styles.filterContainer, 
      !check && styles.sortJustify  // 정렬 박스만 있는 경우
    ]}>
      {check &&
        <CheckBox label={check.label} value={check.value} onChange={check.onChange} />  // 체크 박스 표시
      }
      <SortBox options={sort.options} value={sort.value} onChange={sort.onChange} />
    </View>
  );
};

function CheckBox({ label, value, onChange }: CheckProps) {
  return(
    <>
      <TouchableOpacity activeOpacity={1} style={styles.checkButton} onPress={() => onChange(!value)}>
        <IconSymbol
          name="check.bold"
          size={18}
          color={value ? 'skyblue' : COLOR.TINT.SLATE}  // 체크 여부에 따라 아이콘 색상 변경
        />
        <Text style={styles.checkLabel}>{label}</Text>
      </TouchableOpacity>
    </>
  )
}

function SortBox({ options, value, onChange }: SortProps) {
  const [showModal, setShowModal] = useState(false);
  const currentLabel = options.find(option => option.value === value)?.label || options[0].label;

  return (
    <>
      {/* 정렬 버튼 */}
      <TouchableOpacity activeOpacity={1} style={styles.sortButton} onPress={() => setShowModal(true)}>
        <Text style={styles.sortText}>{currentLabel}</Text>
        <IconSymbol name="chevron.down" size={16} color={COLOR.TEXT.NAVY} />
      </TouchableOpacity>

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
                  onChange(option.value);
                  setShowModal(false);
                }}
              >
                <Text style={[
                  styles.sortText,
                  value === option.value && styles.sortTextActive  // 선택 여부에 따라 스타일 변경
                ]}>
                  {option.label}
                </Text>
                {value === option.value && 
                  <IconSymbol name="check" size={18} color={COLOR.TINT.SLATE} />  // 선택 정렬에 아이콘 추가
                }
              </TouchableOpacity>
            ))}
          </View>
        </TouchableOpacity>
      </Modal>
    </>
  );
}

const styles = StyleSheet.create({
  filterContainer: { 
    flexDirection: 'row', 
    justifyContent: 'space-between', 
    padding: 16,
    backgroundColor: COLOR.BACKGROUND.SLATE_LIGHT
  },
  sortJustify: { justifyContent: 'flex-end' },
  checkButton: { flexDirection: 'row', alignItems: 'center', gap: 4 },
  checkLabel: { fontSize: 14, color: COLOR.TEXT.NAVY },
  sortButton: { flexDirection: 'row', alignItems: 'center', gap: 4 },
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