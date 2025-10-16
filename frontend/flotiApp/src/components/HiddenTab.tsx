import { Tabs } from 'expo-router';
import React from 'react';

export default function HiddenTab() {
  return (
    <Tabs
      screenOptions={{
        headerShown: false,
        tabBarStyle: { display: 'none' }
      }}
    />
  );
}