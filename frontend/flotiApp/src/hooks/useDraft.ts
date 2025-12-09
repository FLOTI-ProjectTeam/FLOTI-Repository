import { useEffect, useState } from 'react';
import AsyncStorage from '@react-native-async-storage/async-storage';

import { showToast } from '@/utils/toast';

type DraftData = {
    title?: string;
    content: string;
    maxParticipantCount?: number;
}

export default function useDraft({
    storageKey, onLoad
}: {
    storageKey: string;
    onLoad?: (data: DraftData) => void;
}) {
    const [isLoaded, setIsLoaded] = useState(false);

    useEffect(() => {
        loadDraft();
    }, []);

    const loadDraft = async () => {
        try {
            const saved = await AsyncStorage.getItem(storageKey);
            if (saved) {
                const parsedData = JSON.parse(saved) as DraftData;
                if (onLoad) {
                    onLoad(parsedData);
                }
            }
        } catch (error) {
            showToast('임시저장 불러오기 실패', 'error');
        } finally {
            setIsLoaded(true);
        }
    };

    const saveDraft = async (data: DraftData) => {
        try {
            await AsyncStorage.setItem(storageKey, JSON.stringify(data));
            showToast('임시저장 성공');
        } catch (error) {
            showToast('임시저장 실패', 'error');
        }
    };

    const clearDraft = async () => {
        try {
            await AsyncStorage.removeItem(storageKey);
        } catch (error) {
            console.error('Failed to clear draft', error);
        }
    };

    return { saveDraft, clearDraft, isLoaded };
}