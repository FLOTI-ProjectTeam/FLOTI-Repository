// This file is a fallback for using MaterialIcons on Android and web.
import MaterialIcons from '@expo/vector-icons/MaterialIcons';
import MaterialCommunityIcons from '@expo/vector-icons/MaterialCommunityIcons';
import FontAwesome from '@expo/vector-icons/FontAwesome';
import FontAwesome6 from '@expo/vector-icons/FontAwesome6';
import Ionicons from '@expo/vector-icons/Ionicons';
import Feather from '@expo/vector-icons/Feather';

import { SymbolWeight } from 'expo-symbols';
import { OpaqueColorValue, StyleProp, TextStyle, ViewStyle } from 'react-native';

// Add your SFSymbol to MaterialIcons mappings here.
const MAPPING = {
  // See MaterialIcons here: https://icons.expo.fyi
  // See SF Symbols in the SF Symbols app on Mac.
  
  // 하단바
  'community': { lib: 'FontAwesome', name: 'wechat' },
  'mindMap': { lib: 'FontAwesome6', name: 'brain' },
  'home': { lib: 'FontAwesome6', name: 'house' },
  'report': { lib: 'MaterialIcons', name: 'analytics' },
  'mypage': { lib: 'Ionicons', name: 'happy' },

  // 커뮤니티
  'heart': { lib: 'Ionicons', name: 'heart-outline' },
  'heart.fill': { lib: 'Ionicons', name: 'heart-sharp' },
  'comment': { lib: 'Ionicons', name: 'chatbox-ellipses-outline' },
  'time': { lib: 'MaterialIcons', name: 'access-time' },
  'people': { lib: 'Ionicons', name: 'people' },
  'send': { lib: 'FontAwesome', name: 'send' },
  'search': { lib: 'Feather', name: 'search' },
  'trash': { lib: 'FontAwesome', name: 'trash' },
  'crown': { lib: 'MaterialCommunityIcons', name: 'crown' },

  // 공통
  'chevron.left': { lib: 'Feather', name: 'chevron-left' },
  'chevron.right': { lib: 'Feather', name: 'chevron-right' },
  'chevron.up': { lib: 'Feather', name: 'chevron-up' },
  'chevron.down': { lib: 'Feather', name: 'chevron-down' },
  'x': { lib: 'Feather', name: 'x' },
  'check': { lib: 'Feather', name: 'check' },
  'check.bold': { lib: 'FontAwesome', name: 'check' },
  'pen' : {lib: 'MaterialCommunityIcons', name: 'pencil' },
  'plus.pen' : {lib: 'MaterialCommunityIcons', name: 'pencil-plus' },
  'more.horizontal': { lib: 'MaterialIcons', name: 'more-horiz' },
  'menu': { lib: 'Feather', name: 'menu' },
} as const

export type IconSymbolName = keyof typeof MAPPING;

/**
 * An icon component that uses native SFSymbols on iOS, and MaterialIcons on Android and web. This ensures a consistent look across platforms, and optimal resource usage.
 *
 * Icon `name`s are based on SFSymbols and require manual mapping to MaterialIcons.
 */

export function IconSymbol({
  name, size = 24, color, style, weight = 'regular'
}: {
  name: IconSymbolName;
  size?: number;
  color: string | OpaqueColorValue;
  style?: StyleProp<ViewStyle>;
  weight?: SymbolWeight;
}) {
  const mapping = MAPPING[name];
  const styleProp =  style as StyleProp<TextStyle>;
  if (!mapping) return null;

  switch (mapping.lib) {
    case 'MaterialIcons':
      return <MaterialIcons color={color} size={size} name={mapping.name} style={styleProp} weight={weight} />;
    case 'MaterialCommunityIcons':
      return <MaterialCommunityIcons color={color} size={size} name={mapping.name} style={styleProp} weight={weight} />;
    case 'FontAwesome':
      return <FontAwesome color={color} size={size} name={mapping.name} style={styleProp} weight={weight} />;
    case 'FontAwesome6':
      return <FontAwesome6 color={color} size={size} name={mapping.name} style={styleProp} weight={weight} />;
    case 'Ionicons':
      return <Ionicons color={color} size={size} name={mapping.name} style={styleProp} weight={weight} />;
    case 'Feather':
      return <Feather color={color} size={size} name={mapping.name} style={styleProp} weight={weight} />;
  }
}
