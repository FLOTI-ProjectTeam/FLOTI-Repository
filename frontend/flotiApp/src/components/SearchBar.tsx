import { TextInput, View, Button, StyleSheet } from 'react-native';

import COLORS from '@/constants/colors';
import { useCommunitySearch } from '@/contexts/CommunitySearchContext';

export default function SearchBar() {
  const { search, setSearch } = useCommunitySearch();

  return (
    <View style={styles.searchContainer}>
      <TextInput
        placeholder="Search"
        placeholderTextColor={COLORS.TEXT.GRAY_LIGHT}
        value={search}
        onChangeText={setSearch}
        style={styles.searchInput}
      />
      <Button title="검색" onPress={() => {/* 검색어 상태 전달 */}} />
    </View>
  );
};

const styles = StyleSheet.create({
  searchContainer: { 
    flexDirection: 'row', 
    marginHorizontal: 20, 
    marginTop: 16, 
    alignItems: 'center', 
    gap: 8
  },
  searchInput: { 
    flex: 1, 
    height: 40,
    paddingVertical: 8,
    paddingHorizontal: 14,
    borderRadius: 30, 
    color: 'black',
    backgroundColor: COLORS.TINT.GRAY_LIGHT
  }
});