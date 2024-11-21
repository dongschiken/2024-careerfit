import Stomp from "stompjs";
import SockJS from "sockjs-client";

class WebSocketService {
  constructor() {
    this.stompClient = null;
  }

  connect(onConnected, onError) {
    // SockJS 인스턴스 생성
    const socket = new SockJS("http://localhost:8080/chat");
    this.stompClient = Stomp.over(socket);

    // Headers 정의
    const headers = {
      Authorization: `Bearer ${sessionStorage.getItem("accessToken")}`, // 토큰을 세션스토리지에서 가져옴
    };

    // WebSocket 연결 시도
    this.stompClient.connect(
      headers, // Headers 추가
      () => {
        console.log("WebSocket 연결 성공");
        onConnected(); // 연결 성공 시 콜백 호출
      },
      (error) => {
        console.error("WebSocket 연결 실패:", error);
        onError(error); // 연결 실패 시 콜백 호출
      }
    );
  }

  subscribe(destination, callback) {
    if (this.stompClient && this.stompClient.connected) {
      this.stompClient.subscribe(destination, callback);
    } else {
      console.error("WebSocket이 연결되지 않았습니다.");
    }
  }

  send(destination, body) {
    if (this.stompClient && this.stompClient.connected) {
      this.stompClient.send(destination, {}, JSON.stringify(body));
    } else {
      console.error(
        "메시지를 전송할 수 없습니다. WebSocket이 연결되지 않았습니다."
      );
    }
  }
}

export default new WebSocketService();
