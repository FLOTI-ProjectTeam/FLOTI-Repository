import { View, Text, TouchableOpacity, Modal, StyleSheet, Pressable } from 'react-native';
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
  const [popupVisible, setPopupVisible] = useState(false);

  const currentLabel = sort.options.find(option => option.value === sort.value)?.label || sort.options[0].label;

  /* 이벤트 핸들러 */
  const handleChangeValue = (value: SortType) => {
    sort.onChange(value);
    setPopupVisible(false);
  };

  return (
    <View style={[
      styles.filterContainer,
      !check && styles.sortJustify  // 정렬 박스만 있는 경우
    ]}>
      {check && (
        <>
          <Pressable style={styles.checkButton} onPress={() => check.onChange(!check.value)}>
            <IconSymbol
              name="check.bold"
              size={18}
              color={check.value ? 'skyblue' : COLOR.TINT.SLATE}  // 체크 여부에 따라 아이콘 색상 변경
            />
            <Text style={styles.checkLabel}>{check.label}</Text>
          </Pressable>
        </>
      )}

      {/* 버튼 */}
      <Pressable style={styles.sortButton} onPress={() => setPopupVisible(true)}>
        <Text style={styles.optionText}>{currentLabel}</Text>
        <IconSymbol name={popupVisible ? "chevron.up" : "chevron.down"} size={16} color={COLOR.TEXT.NAVY} />
      </Pressable>

      {/* 팝업 */}
      <Modal transparent visible={popupVisible} onRequestClose={() => setPopupVisible(false)}>
        <Pressable style={styles.overlay} onPress={() => setPopupVisible(false)}>
          <View style={styles.modalContent}>
            {sort.options.map((option) => (
              <TouchableOpacity
                activeOpacity={0.5}
                key={option.value}
                style={styles.option}
                onPress={() => handleChangeValue(option.value)}
              >
                <Text style={[
                  styles.optionText,
                  sort.value === option.value && styles.optionTextActive  // 선택 여부에 따라 스타일 추가
                ]}>
                  {option.label}
                </Text>
                {sort.value === option.value &&
                  <IconSymbol name="check" size={18} color={COLOR.TINT.SLATE} />  // 선택 정렬에 아이콘 추가
                }
              </TouchableOpacity>
            ))}
          </View>
        </Pressable>
      </Modal>
    </View>
  );
};

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
  option: { flexDirection: 'row', justifyContent: 'space-between' },
  optionText: { fontSize: 14, color: COLOR.TEXT.NAVY },
  optionTextActive: { color: COLOR.TINT.SLATE, fontWeight: 600 }
});