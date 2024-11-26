import Stomp from "stompjs";
import SockJS from "sockjs-client";

class WebSocketService {
  constructor() {
    this.stompClient = null;
    this.subscriptions = new Map(); // Set을 Map으로 변경
  }

  connect(onConnected, onError) {
    try {
      if (this.stompClient && this.stompClient.connected) {
        onConnected();
        return;
      }

      const socket = new SockJS("http://192.168.210.52:8080/chat");
      this.stompClient = Stomp.over(socket);
      this.stompClient.debug = null;

      const headers = {
        Authorization: `Bearer ${sessionStorage.getItem("accessToken")}`,
      };

      this.stompClient.connect(
        headers,
        () => {
          console.log("WebSocket Connected!");
          if (typeof onConnected === "function") {
            onConnected();
          }
        },
        (error) => {
          console.error("WebSocket Error:", error);
          if (typeof onError === "function") {
            onError(error);
          }
        }
      );
    } catch (e) {
      console.error("WebSocket Exception:", e);
      if (typeof onError === "function") {
        onError(e);
      }
    }
  }

  subscribe(destination, callback) {
    if (!this.stompClient?.connected)
      throw new Error("WebSocket not connected");

    if (!this.subscriptions.has(destination)) {
      const subscription = this.stompClient.subscribe(
        destination,
        (message) => {
          try {
            callback(message);
          } catch (error) {
            console.error("Message processing error:", error);
          }
        }
      );
      this.subscriptions.set(destination, subscription); // Map에 저장
      return subscription;
    }
  }

  unsubscribe(destination) {
    const subscription = this.subscriptions.get(destination);
    if (subscription) {
      subscription.unsubscribe();
      this.subscriptions.delete(destination);
    }
  }

  send(destination, body) {
    if (!this.stompClient?.connected)
      throw new Error("WebSocket not connected");
    this.stompClient.send(
      destination,
      {},
      typeof body === "string" ? body : JSON.stringify(body)
    );
  }

  disconnect() {
    if (this.stompClient?.connected) {
      this.subscriptions.forEach((subscription) => subscription.unsubscribe());
      this.subscriptions.clear();
      this.stompClient.disconnect();
    }
  }

  isConnected() {
    return Boolean(this.stompClient?.connected);
  }
}

export default new WebSocketService();
