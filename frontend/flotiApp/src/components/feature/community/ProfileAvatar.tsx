import { Image, StyleSheet } from 'react-native';

export default function ProfileAvatar({
    profileImage,
    size = 40,
    borderRadius = 8
}: {
    profileImage: string | null;
    size?: number;
    borderRadius?: number;
}) {
    const styles = StyleSheet.create({
        profile: {
            width: size, height: size,
            justifyContent: 'center',
            alignItems: 'center',
            marginRight: 10,
            borderRadius
        }
    });

    return (
        profileImage
            ? <Image source={{ uri: profileImage }} style={styles.profile} />
            : <Image source={require('@assets/images/no-profile.jpg')} style={styles.profile} />
    );
}