import { IMessage } from '@stomp/stompjs';

import socketClient from '@/api/socketClient';
import { DISCUSSION_API } from '@/constants/endpoints';
import { ErrorMessage } from '@/types/community/common';
import { MessageRequest, MessageResponse } from '@/types/community/discussion';

// 메시지 구독
export const subscribeMessages = (
    roomId: number,
    callback: (message: MessageResponse) => void
) => {
    return socketClient.subscribe(
        DISCUSSION_API.WS_SUBSCRIBE(roomId),
        (msg: IMessage) => callback(JSON.parse(msg.body))
    );
};

// 에러 구독
export const subscribeErrors = (callback: (error: ErrorMessage) => void) => {
    return socketClient.subscribe(
        DISCUSSION_API.WS_ERROR,
        (msg: IMessage) => callback(JSON.parse(msg.body))
    );
};

/* 메시지 API */
// 전송
export const createMessage = (roomId: number, message: MessageRequest) => {
    socketClient.publish({
        destination: DISCUSSION_API.WS_SEND(roomId),
        body: JSON.stringify(message)
    });
};
  
// 삭제
export const deleteMessage = (roomId: number, messageId: number) => {
    socketClient.publish({
        destination: DISCUSSION_API.WS_DELETE(roomId, messageId)
    });
};
  
// 좋아요 토글
export const toggleLikeMessage = (roomId: number, messageId: number) => {
    socketClient.publish({
        destination: DISCUSSION_API.WS_LIKE(roomId, messageId)
    });
};