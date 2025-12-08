import { View, Image, Text, StyleSheet } from 'react-native';

import COLOR from '@/constants/colors';

export default function ProfileAvatar({
    profileImage,
    nickname,
    size = 32,
    borderRadius = 16,
    fontSize = 14,
}: {
    profileImage: string | null;
    nickname: string | null;
    size?: number;
    borderRadius?: number;
    fontSize?: number;
}) {
  return (
    profileImage ? (
        <Image
            source={{ uri: profileImage }}
            style={[styles.profile, { width: size, height: size, borderRadius }]}
        />
    ) : (
        <View style={[styles.profile, { width: size, height: size, borderRadius }]}>
            <Text style={[styles.profileText, { fontSize }]}>{nickname?.[0]}</Text>
        </View>
    )
  );
}

const styles = StyleSheet.create({
    profile: { 
        backgroundColor: 'lightgray', 
        justifyContent: 'center', 
        alignItems: 'center', 
        marginRight: 10 
    },
    profileText: { fontWeight: 700, color: COLOR.TEXT.GRAY_MEDIUM }
});