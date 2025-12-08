import { StyleSheet, ViewStyle } from 'react-native';

import COLOR from '@/constants/colors';

export const SHADOW = {
    shadowColor: 'black',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.3,
    shadowRadius: 4.5,
    elevation: 6
};

const COMMON = {
    CENTER: { justifyContent: 'center', alignItems: 'center' } as ViewStyle,
    CARD: {
        padding: 12,
        marginBottom: 12,
        borderRadius: 10,
        backgroundColor: 'white',
        gap: 8
    },
    SHADOW
};

export const STYLE = StyleSheet.create({
    /* Container */
    BASE_CONTAINER: { flex: 1, backgroundColor: 'white' },
    CONTENT_CONTAINER: { flex: 1, backgroundColor: COLOR.BACKGROUND.SLATE_LIGHT },
    WRAPPER: { flex: 1, paddingHorizontal: 16 },

    /* Text */
    EMPTY_TEXT: { fontSize: 16, color: COLOR.TEXT.GRAY_MEDIUM },

    /* Box */
    CARD: { ...COMMON.CARD },
    CARD_OUTLINE: { ...COMMON.CARD, borderWidth: 1, borderColor: COLOR.TINT.SLATE_SOFT },

    /* Button */
    BUTTON: {
        ...COMMON.CENTER,
        flexDirection: 'row',
        paddingVertical: 10,
        borderRadius: 12,
        backgroundColor: COLOR.BUTTON.NAVY
    },
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

    /* Layout Helper */
    ROW: { flexDirection: 'row' },
    COLUMN: { flexDirection: 'column' },
    CENTER: { ...COMMON.CENTER },
    FLEX: { flex: 1 },
});