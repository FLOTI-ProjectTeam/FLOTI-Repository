import dayjs from 'dayjs';
import relativeTime from 'dayjs/plugin/relativeTime';
import customParseFormat from 'dayjs/plugin/customParseFormat';
import 'dayjs/locale/ko';

import { MessageResponse } from '@/types/community/discussion';

dayjs.extend(relativeTime);
dayjs.extend(customParseFormat);
dayjs.locale('ko');

type MessageWithLabel = {
    type: 'label' | 'message';
    id: string;
    message?: MessageResponse;
    dateLabel: string;
};

// 날짜를 상대 시간으로 변환
export const formatRelativeTime = (createdAt: string) =>
    dayjs(createdAt, 'YYYY.MM.DD HH:mm').fromNow();

// 날짜가 24시간 이내면 상대 시간으로 변환
export const formatSmartTime = (
    createdAt: string, 
    type: 'date' | 'detail' = 'date'
) => {
    const date = dayjs(createdAt, 'YYYY.MM.DD HH:mm');
    const diffHours = dayjs().diff(date, 'hour');
  
    if (diffHours < 24) return date.fromNow();
    if (type === 'date') return date.format('YYYY.MM.DD');
    return createdAt; 
};

// 날짜를 시분으로 변환
export const formatTimeOnly = (createdAt: string) =>
    dayjs(createdAt, 'YYYY.MM.DD HH:mm:ss').format('a h:mm');

// 날짜 라벨 삽입
export const insertDateLabels = (messages: MessageResponse[]): MessageWithLabel[] => {
    const result: MessageWithLabel[] = [];
    let lastDate = '';
  
    messages.forEach(msg => {
        const dateLabel = dayjs(msg.createdAt, 'YYYY.MM.DD HH:mm:ss').format('YYYY.MM.DD');
    
        // 날짜가 바뀌면 라벨 추가
        if (dateLabel !== lastDate) {
            result.push({ type: 'label', id: `label-${dateLabel}`, dateLabel });
            lastDate = dateLabel;
        }
    
        result.push({ type: 'message', id: msg.id.toString(), message: msg, dateLabel });
    });
  
    return result;
};