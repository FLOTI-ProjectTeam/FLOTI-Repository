import { View, Text, TouchableOpacity, StyleSheet, ViewStyle } from 'react-native';
import { Fragment } from 'react';

export default function MorePopup({ 
    actions, style
}: {
    actions: { 
        label: string; 
        onPress: () => void;
    }[];
    style: ViewStyle;
}) {
    const width = (actions.length == 1 ? 60 : 110); // 메뉴 개수에 따라 너비 조절

    return (
        <View style={[styles.popup, style, { width }]}>
            {actions.map((action, idx) => (
            <Fragment key={idx}>
                <TouchableOpacity activeOpacity={0.5} onPress={() => action.onPress()}>
                    <Text style={[
                        styles.menuText, 
                        actions.length - 1 === idx && styles.lastText  // 마지막 메뉴인 경우
                    ]}>
                        {action.label}
                    </Text>
                </TouchableOpacity>
            </Fragment>
            ))}
        </View>
    );
}
  
const styles = StyleSheet.create({
    popup: {
        position: 'absolute',
        alignItems: 'center',
        backgroundColor: 'white',
        borderRadius: 8,
        flexDirection: 'row', 
        justifyContent: 'center', 
        gap: 18,
        shadowColor: 'black',
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.25,
        shadowRadius: 3,
        elevation: 3,
        paddingVertical: 12, paddingHorizontal: 16
    },
    menuText: { fontSize: 16 },
    lastText: { color: 'red' }
});