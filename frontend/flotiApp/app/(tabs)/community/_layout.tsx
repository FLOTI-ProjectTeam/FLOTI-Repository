import { View } from 'react-native';
import { Tabs } from 'expo-router';

import CommunityTabBar from '@/components/CommunityTabBar'
import SearchBar from '@/components/SearchBar';
import COLORS from '@/constants/colors';
import { CommunitySearchProvider } from '@/contexts/CommunitySearchContext';

export default function CommunityLayout() {
  return (
    <CommunitySearchProvider>
      <View style={{ flex: 1, backgroundColor: COLORS.WHITE }}>
        <SearchBar />
        <CommunityTabBar />

        <Tabs
          screenOptions={{
            headerShown: false,
            tabBarStyle: { display: 'none' },
          }}
        >
        </Tabs>
      </View>
    </CommunitySearchProvider>
  );
}