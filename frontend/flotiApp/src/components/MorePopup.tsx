import { View, Text, TouchableOpacity, StyleSheet, ViewStyle } from 'react-native';
import React from 'react';

export default function MorePopup({ 
    actions, popupStyle 
}: {
    actions: { 
        label: string; 
        onPress: () => void;
    }[];
    popupStyle: ViewStyle;
}) {
    return (
        <View style={[styles.popup, popupStyle]}>
            {actions.map((action, idx) => (
            <React.Fragment key={idx}>
                <TouchableOpacity activeOpacity={0.5} onPress={() => action.onPress()}>
                <Text style={[
                    styles.menuText, 
                    actions.length - 1 === idx && styles.redText  // 마지막 메뉴는 빨간색으로 지정정
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
        shadowColor: 'black',
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.25,
        shadowRadius: 3,
        elevation: 3,
        paddingVertical: 12, paddingHorizontal: 16
    },
    menuText: { fontSize: 16 },
    redText: { color: 'red' }
});