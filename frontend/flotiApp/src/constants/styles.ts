import { StyleSheet, ViewStyle } from 'react-native';
import COLOR from '@/constants/colors';

const COMMON = {
    WRAPPER: { flex: 1, padding: 16 },
    CENTER: { justifyContent: 'center', alignItems: 'center' } as ViewStyle,
    SHADOW: {
        shadowColor: 'black',
        shadowOffset: { width: 0, height: 4 },
        shadowOpacity: 0.3,
        shadowRadius: 4.5,
        elevation: 6
    }
};

export const STYLE = StyleSheet.create({
    /* CONTAINERS */
    BASE_CONTAINER: { flex: 1, backgroundColor: 'white' },
    CONTENT_CONTAINER: {
        ...COMMON.WRAPPER,
        backgroundColor: COLOR.BACKGROUND.SLATE_LIGHT
    },
    WRAPPER: { ...COMMON.WRAPPER },

    /* BOXS */
    CARD: {
        padding: 12,
        marginBottom: 8,
        borderWidth: 0,
        borderRadius: 10,
        backgroundColor: 'white',
        gap: 8
    },

    /* BUTTONS */
    FAD: {
        position: 'absolute',
        bottom: 24,
        right: 24,
        width: 56, height: 56,
        borderRadius: 28,
        backgroundColor: COLOR.BUTTON.NAVY,
        ...COMMON.CENTER,
        ...COMMON.SHADOW
    },

    /* TEXTS */
    EMPTY_TEXT: { fontSize: 16, color: COLOR.TEXT.GRAY_MEDIUM },

    /* LAYOUT HELPERS */
    ROW: { flexDirection: 'row' },
    CENTER: { ...COMMON.CENTER },
});