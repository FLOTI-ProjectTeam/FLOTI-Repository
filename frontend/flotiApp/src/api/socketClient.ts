import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

import { BASE_URL } from '@/constants/endpoints';

const SOCKET_URL = BASE_URL + '/ws';

// STOMP 클라이언트 생성: WebSocket 연결 및 재연결 설정
const socketClient = new Client({
    webSocketFactory: () => new SockJS(SOCKET_URL),
    reconnectDelay: 5000,
    debug: (str) => console.log(str)
});

// WebSocket 연결 시작
const connectSocket = () => {
    if (!socketClient.active) socketClient.activate();
};

// WebSocket 연결 종료
const disconnectSocket = () => {
    if (socketClient.active) socketClient.deactivate();
};

export default socketClient;
export { connectSocket, disconnectSocket };