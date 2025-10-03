import { TextInput, View, Button, StyleSheet } from "react-native";

import COLORS from "../constants/colors";
import { useCommunitySearch } from "../contexts/CommunitySearchContext";

function SearchBar() {
  const { search, setSearch } = useCommunitySearch();

  return (
    <View style={styles.container}>
      <TextInput
        placeholder="Search"
        placeholderTextColor={COLORS.TEXT.LIGHT_GRAY}
        value={search}
        onChangeText={setSearch}
        style={styles.input}
      />
      <Button title="검색" onPress={() => {/* 검색어 상태 전달 */}} />
    </View>
  );
};

const styles = StyleSheet.create({
  container: { 
    flexDirection: 'row', 
    marginHorizontal: 20, 
    marginTop: 16, 
    alignItems: 'center', 
    gap: 8
  },
  input: { 
    flex: 1, 
    height: 40,
    paddingVertical: 8,
    paddingHorizontal: 14,
    borderRadius: 30, 
    color: COLORS.BLACK,
    backgroundColor: COLORS.TINT.LIGHT_GRAY,
  },
});

export default SearchBar;