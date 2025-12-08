import { TextInput, View, StyleSheet, Pressable } from 'react-native';

import { useCommunitySearch } from '@/contexts/CommunitySearchContext';

import { IconSymbol } from '@/components/ui/IconSymbol';

import COLOR from '@/constants/colors';

export default function SearchBar() {
  const { search, setSearch, setSearchTrigger } = useCommunitySearch();

  return (
    <View style={styles.searchContainer}>
      <TextInput
        placeholder='Search'
        placeholderTextColor={COLOR.TEXT.GRAY_LIGHT}
        value={search}
        onChangeText={setSearch}
        style={styles.searchInput}
        returnKeyType='search'  // 키보드 엔터키를 검색 아이콘으로 변경
        onSubmitEditing={() => setSearchTrigger(search)} // 키보드의 확인 버튼으로 검색
      />
      <Pressable
        onPress={() => setSearchTrigger(search)}  // 돋보기 아이콘으로 검색
      >
        <IconSymbol name='search' color={COLOR.TINT.GRAY_DARK} />
      </Pressable>
    </View>
  );
};

const styles = StyleSheet.create({
  searchContainer: { 
    flexDirection: 'row', 
    marginHorizontal: 20, 
    marginTop: 16,
    paddingHorizontal: 14,
    alignItems: 'center', 
    gap: 8,
    borderRadius: 30,
    backgroundColor: COLOR.TINT.GRAY_LIGHT
  },
  searchInput: { 
    flex: 1, 
    height: 42,
    paddingVertical: 8,
    color: 'black'
  }
});