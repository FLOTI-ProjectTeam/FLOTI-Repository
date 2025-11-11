import { View, Text, TouchableOpacity, StyleSheet, ViewStyle } from 'react-native';
import React from 'react';

export default function MorePopup({ 
    actions, style 
}: {
    actions: { 
        label: string; 
        onPress: () => void;
    }[];
    style: ViewStyle;
}) {
    return (
        <View style={[styles.popup, style]}>
            {actions.map((action, idx) => (
            <React.Fragment key={idx}>
                <TouchableOpacity activeOpacity={0.5} onPress={() => action.onPress()}>
                    <Text style={[
                        styles.menuText, 
                        actions.length - 1 === idx && styles.lastText  // 마지막 메뉴인 경우
                    ]}>
                        {action.label}
                    </Text>
                </TouchableOpacity>
            </React.Fragment>
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
        width: 110,
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