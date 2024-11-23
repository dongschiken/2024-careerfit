import Stomp from "stompjs";
import SockJS from "sockjs-client";

class WebSocketService {
  constructor() {
    this.stompClient = null;
    this.subscriptions = new Set(); // Map 대신 Set 사용
  }

  connect(onConnected, onError) {
    try {
      if (this.stompClient && this.stompClient.connected) {
        console.log("Already connected to WebSocket");
        onConnected();
        return;
      }

      console.log("Attempting to connect to WebSocket...");
      const socket = new SockJS("http://localhost:8080/chat");
      this.stompClient = Stomp.over(socket);

      // 디버그 메시지 비활성화
      this.stompClient.debug = null;

      const headers = {
        Authorization: `Bearer ${sessionStorage.getItem("accessToken")}`,
      };

      this.stompClient.connect(
        headers,
        (frame) => {
          console.log("WebSocket Connected!");
          onConnected();
        },
        (error) => {
          console.error("WebSocket Connection Error:", error);
          onError(error);
        }
      );
    } catch (e) {
      console.error("WebSocket Connection Exception:", e);
      onError(e);
    }
  }

  subscribe(destination, callback) {
    if (!this.stompClient?.connected) {
      console.error("Cannot subscribe: WebSocket is not connected");
      return null;
    }

    // 이미 구독 중인 경우 새 구독 생성하지 않음
    if (this.subscriptions.has(destination)) {
      console.log(`Already subscribed to ${destination}`);
      return;
    }

    console.log(`Subscribing to ${destination}`);
    const subscription = this.stompClient.subscribe(destination, (message) => {
      console.log(`Message received from ${destination}`);
      try {
        callback(message);
      } catch (error) {
        console.error("Error processing message:", error);
      }
    });

    this.subscriptions.add(destination);
    return subscription;
  }

  unsubscribe(destination) {
    if (this.subscriptions.has(destination)) {
      console.log(`Unsubscribing from ${destination}`);
      this.subscriptions.delete(destination);
    }
  }

  send(destination, body) {
    if (!this.stompClient?.connected) {
      console.error("Cannot send message: WebSocket is not connected");
      return;
    }

    console.log(`Sending message to ${destination}`);
    this.stompClient.send(destination, {}, JSON.stringify(body));
  }

  disconnect() {
    if (this.stompClient) {
      // 모든 구독 해제
      this.subscriptions.clear();

      this.stompClient.disconnect(() => {
        console.log("WebSocket Disconnected");
      });
    }
  }

  isConnected() {
    return this.stompClient && this.stompClient.connected;
  }
}

export default new WebSocketService();
