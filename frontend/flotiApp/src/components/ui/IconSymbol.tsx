// This file is a fallback for using MaterialIcons on Android and web.

import MaterialIcons from '@expo/vector-icons/MaterialIcons';
import FontAwesome from '@expo/vector-icons/FontAwesome';
import FontAwesome5 from '@expo/vector-icons/FontAwesome5';
import Ionicons from '@expo/vector-icons/Ionicons';
import { SymbolWeight } from 'expo-symbols';
import React from 'react';
import { OpaqueColorValue, StyleProp, ViewStyle } from 'react-native';

// Add your SFSymbol to MaterialIcons mappings here.
const MAPPING = {
  // See MaterialIcons here: https://icons.expo.fyi
  // See SF Symbols in the SF Symbols app on Mac.
  
  // MaterialIcons
  'house.fill': { lib: 'MaterialIcons', name: 'home' },
  'paperplane.fill': { lib: 'MaterialIcons', name: 'send' },
  'chevron.left.forwardslash.chevron.right': { lib: 'MaterialIcons', name: 'code' },
  'chevron.right': { lib: 'MaterialIcons', name: 'chevron-right' },
  'report.fill': { lib: 'MaterialIcons', name: 'auto-graph' },

  // FontAwesome
  'community.fill': { lib: 'FontAwesome', name: 'wechat' },
  'mindmap.fill': { lib: 'FontAwesome5', name: 'brain' },

  // Ionicons
  'mypage.fill': { lib: 'Ionicons', name: 'happy' },
} as const

export type IconSymbolName = keyof typeof MAPPING;

/**
 * An icon component that uses native SFSymbols on iOS, and MaterialIcons on Android and web. This ensures a consistent look across platforms, and optimal resource usage.
 *
 * Icon `name`s are based on SFSymbols and require manual mapping to MaterialIcons.
 */

export function IconSymbol({
  name,
  size = 24,
  color,
  style,
}: {
  name: IconSymbolName;
  size?: number;
  color: string | OpaqueColorValue;
  style?: StyleProp<ViewStyle>;
  weight?: SymbolWeight;
}) {
  const mapping = MAPPING[name];
  if (!mapping) return null;

  switch (mapping.lib) {
    case 'MaterialIcons':
      return <MaterialIcons color={color} size={size} name={mapping.name} style={style as any} />;
    case 'FontAwesome':
      return <FontAwesome color={color} size={size} name={mapping.name} style={style as any} />;
    case 'FontAwesome5':
      return <FontAwesome5 color={color} size={size} name={mapping.name} style={style as any} />;
    case 'Ionicons':
      return <Ionicons color={color} size={size} name={mapping.name} style={style as any} />;
  }
}
