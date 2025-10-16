// This file is a fallback for using MaterialIcons on Android and web.

import MaterialIcons from '@expo/vector-icons/MaterialIcons';
import MaterialCommunityIcons from '@expo/vector-icons/MaterialCommunityIcons';
import FontAwesome from '@expo/vector-icons/FontAwesome';
import FontAwesome6 from '@expo/vector-icons/FontAwesome6';
import Ionicons from '@expo/vector-icons/Ionicons';
import Feather from '@expo/vector-icons/Feather';

import { SymbolWeight } from 'expo-symbols';
import { OpaqueColorValue, StyleProp, ViewStyle } from 'react-native';
import React from 'react';

// Add your SFSymbol to MaterialIcons mappings here.
const MAPPING = {
  // See MaterialIcons here: https://icons.expo.fyi
  // See SF Symbols in the SF Symbols app on Mac.
  
  // MaterialIcons
  'paperplane.fill': { lib: 'MaterialIcons', name: 'send' },
  'chevron.left': { lib: 'MaterialIcons', name: 'chevron-left' },
  'chevron.right': { lib: 'MaterialIcons', name: 'chevron-right' },
  'report': { lib: 'MaterialIcons', name: 'analytics' },
  'more.horizontal': { lib: 'MaterialIcons', name: 'more-horiz' },
  'plus': { lib: 'MaterialIcons', name: 'add' },

  // MaterialCommunityIcons
  'pen.plus' : {lib: 'MaterialCommunityIcons', name: 'pencil-plus' },

  // FontAwesome
  'community': { lib: 'FontAwesome', name: 'wechat' },
  'thumbs': { lib: 'FontAwesome', name: 'thumbs-o-up' },
  'thumbs.fill': { lib: 'FontAwesome', name: 'thumbs-up' },
  'mindMap': { lib: 'FontAwesome6', name: 'brain' },
  'house': { lib: 'FontAwesome6', name: 'house' },
  'comment': { lib: 'FontAwesome6', name: 'commenting' },

  // Ionicons
  'mypage': { lib: 'Ionicons', name: 'happy' },

  // Feather
  'x': { lib: 'Feather', name: 'x' },
  'chevron.down': { lib: 'Feather', name: 'chevron-down' },
  'check': { lib: 'Feather', name: 'check' },

  // 임시
  'pen.paper': { lib: 'FontAwesome', name: 'pencil-square-o' },
  'trash': { lib: 'FontAwesome', name: 'trash-o' },
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
    case 'MaterialCommunityIcons':
      return <MaterialCommunityIcons color={color} size={size} name={mapping.name} style={style as any} />;
    case 'FontAwesome':
      return <FontAwesome color={color} size={size} name={mapping.name} style={style as any} />;
    case 'FontAwesome6':
      return <FontAwesome6 color={color} size={size} name={mapping.name} style={style as any} />;
    case 'Ionicons':
      return <Ionicons color={color} size={size} name={mapping.name} style={style as any} />;
    case 'Feather':
      return <Feather color={color} size={size} name={mapping.name} style={style as any} />;
  }
}
