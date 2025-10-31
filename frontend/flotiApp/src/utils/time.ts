import dayjs from 'dayjs';
import relativeTime from 'dayjs/plugin/relativeTime';
import customParseFormat from 'dayjs/plugin/customParseFormat';
import 'dayjs/locale/ko';

dayjs.extend(relativeTime);
dayjs.extend(customParseFormat);
dayjs.locale('ko');

// 입력된 날짜를 기준으로 상대 시간으로 변환
export function formatRelative(createdAt: string): string {
    return dayjs(createdAt, 'YYYY.MM.DD HH:mm:ss').fromNow();
}

// 입력된 날짜가 24시간 이내면 상대 시간으로 변환
export function formatRelativeOrDate(createdAt: string): string {
    const date = dayjs(createdAt, 'YYYY.MM.DD HH:mm:ss');
    const diffHours = dayjs().diff(date, 'hour');
  
    if (diffHours < 24) return date.fromNow();
    return date.format('YYYY.MM.DD');
}