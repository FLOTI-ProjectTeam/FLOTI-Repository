import { View, Text, StyleSheet, Pressable, Alert } from 'react-native';
import { router } from 'expo-router';
import React, { useState } from 'react';

import { IconSymbol } from '@/components/ui/IconSymbol';
import COLORS from '@/constants/colors';
import { TipPostResponse } from '@/types/community/tip';
import { deleteTipPost } from '@/api/community/tip';

interface Props {
    post: TipPostResponse;
}

export default function BottomBar({ post }: Props) {
    const [menuVisible, setMenuVisible] = useState(false);

    const handleEdit = () => {
        router.push(`/community/tip/update/${post.id}`);
        setMenuVisible(false);
    };
    
    const handleDelete = async () => {
        setMenuVisible(false);
    
        Alert.alert(
          '삭제 확인',
          '정말 삭제하시겠습니까?',
          [
            { text: '취소', style: 'cancel' },
            { 
              text: '삭제', 
              style: 'destructive', 
              onPress: async () => {
                try {
                //   await deleteTipPost(post.id);
                  Alert.alert('삭제 완료');
                  router.back(); // 삭제 후 이전 화면으로
                } catch (error) {
                  console.error(error);
                  Alert.alert('삭제 실패', '잠시 후 다시 시도해주세요.');
                }
              } 
            },
          ]
        );
      };

    return (
        <View style={styles.bottomBar}>
            {/* 댓글 & 추천 */}
            <View style={styles.leftActions}>
                <Pressable 
                  style={styles.actionButton} 
                  onPress={() => router.push(`/community/tip/${post.id}/comments`)}
                >
                    <IconSymbol name="comment" size={24} color={COLORS.ICON.GRAY_DARK} />
                    <Text style={styles.bottomText}>{post.commentCount}</Text>
                </Pressable>

                <Pressable style={styles.actionButton}>
                    <IconSymbol name="thumbs" size={24} color={COLORS.ICON.GRAY_DARK} />
                    <Text style={styles.bottomText}>{post.likeCount}</Text>
                </Pressable>
            </View>

            {/* 더보기 */}
            <View>
                <Pressable onPress={() => setMenuVisible(!menuVisible)}>
                    <IconSymbol name="more.horizontal" size={24} color={COLORS.ICON.GRAY_DARK} />
                </Pressable>

                {menuVisible && (
                    <PopupMenu
                        onClose={() => setMenuVisible(false)}
                        actions={[
                            { label: "수정", onPress: handleEdit },
                            { label: "삭제", onPress: handleDelete, textStyle: styles.deleteText },
                          ]}
                    />
                )}
            </View>
        </View>
    );
}

function PopupMenu({
    actions,
    onClose,
  }: {
    actions: { label: string; onPress: () => void; textStyle?: any }[];
    onClose: () => void;
  }) {
    return (
      <Pressable onPress={onClose}>
        <View style={styles.popupMenu}>
          {actions.map((action, idx) => (
            <React.Fragment key={idx}>
              <Pressable
                style={styles.menuItem}
                onPress={() => {
                  action.onPress();
                  onClose();
                }}
              >
                <Text style={[styles.menuText, action.textStyle]}>{action.label}</Text>
              </Pressable>
              {/* 구문선 역할 */}
              {idx < actions.length - 1 && <View style={styles.divider} />}
            </React.Fragment>
          ))}
        </View>
      </Pressable>
    );
  }

const styles = StyleSheet.create({
    bottomBar: {
        height: 60,
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
        paddingHorizontal: 18,
        borderTopWidth: 1,
        borderTopColor: COLORS.TINT.GRAY,
        backgroundColor: 'white'
    },
    bottomText: {
        color: COLORS.TEXT.GRAY_MEDIUM,
        fontSize: 14,
        marginLeft: 8
    },
    leftActions: {
        flexDirection: 'row'
    },
    actionButton: {
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'center',
        marginRight: 16
    },
    popupMenu: {
        position: 'absolute',
        right: 0,
        bottom: 40,
        backgroundColor: 'white',
        borderRadius: 8,
        shadowColor: 'black',
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.25,
        shadowRadius: 3,
        elevation: 3,
        minWidth: 100
    },
    menuItem: {
        padding: 16,
    },
    menuText: {
        fontSize: 16,
        textAlign: 'center'
    },
    deleteText: {
        color: 'red'
    },
    divider: {
        height: 1,
        backgroundColor: '#ddd'
    }
});