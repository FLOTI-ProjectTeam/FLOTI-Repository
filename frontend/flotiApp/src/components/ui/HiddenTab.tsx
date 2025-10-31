import { Tabs } from 'expo-router';

export default function HiddenTab() {
  return (
    <Tabs
      screenOptions={{
        headerShown: false,
        tabBarStyle: {display: 'none'}
      }}
    />
  );
}