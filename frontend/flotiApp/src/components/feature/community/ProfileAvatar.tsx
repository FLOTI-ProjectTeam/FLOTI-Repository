import { Image, StyleSheet } from 'react-native';

export default function ProfileAvatar({
    profileImage, size = 40, borderRadius = 8
}: {
    profileImage: string | null;
    size?: number;
    borderRadius?: number;
}) {
    return (
        profileImage
            ? <Image
                source={{ uri: profileImage }}
                style={[styles.profile, { width: size, height: size, borderRadius }]}
            />
            : <Image
                source={require('@assets/images/no-profile.jpg')}
                style={[styles.profile, { width: size, height: size, borderRadius }]}
            />
    );
}

const styles = StyleSheet.create({
    profile: { justifyContent: 'center', alignItems: 'center', marginRight: 10 }
});