<template>
  <div></div>
</template>
<script>
// import WebSocketService from "@/services/WebSocketService";

// export default {
//   props: {
//     currentUser: {
//       type: Object,
//       required: true,
//     },
//     currentChatRoomId: {
//       type: Number,
//       required: true,
//     },
//   },
//   data() {
//     return {
//       messages: [],
//       newMessage: "",
//       isConnected: false,
//       currentSubscription: null,
//       processedMessages: new Set(), // processedMessages를 컴포넌트 데이터로 선언
//     };
//   },
//   created() {
//     this.loadMessages();
//   },
//   mounted() {
//     console.log("Component mounted");
//     this.connectWebSocket();
//   },
//   beforeDestroy() {
//     if (this.currentSubscription) {
//       console.log("구독 해제");
//       this.currentSubscription.unsubscribe();
//     }
//     WebSocketService.disconnect();
//   },
//   methods: {
//     async loadMessages() {
//       try {
//         const response = await fetch(
//           `/api/chat-room/${this.currentChatRoomId}/history`
//         );
//         const messages = await response.json();
//         this.messages = messages;
//       } catch (error) {
//         console.error("메시지 로드 실패:", error);
//       }
//     },
//     connectWebSocket() {
//       if (WebSocketService.isConnected()) {
//         console.log("이미 WebSocket 연결됨");
//         this.subscribeToChat();
//         return;
//       }

//       WebSocketService.connect(
//         () => {
//           this.isConnected = true;
//           console.log("WebSocket 연결됨");
//           this.subscribeToChat();
//         },
//         (error) => {
//           console.error("WebSocket 연결 실패:", error);
//           this.isConnected = false;
//         }
//       );
//     },
//     subscribeToChat() {
//       if (this.currentSubscription) {
//         console.log("기존 구독 해제");
//         this.currentSubscription.unsubscribe();
//       }

//       const destination = `/topic/chatRoom/${this.currentChatRoomId}`;
//       console.log("구독 시작:", destination);

//       this.currentSubscription = WebSocketService.subscribe(
//         destination,
//         this.onMessageReceived
//       );
//     },
//     onMessageReceived(payload) {
//       try {
//         const message = JSON.parse(payload.body);

//         // 수신한 메시지의 userId와 현재 사용자의 userId를 비교
//         if (message.userId !== this.currentUser.userId) {
//           const messageId = `${message.userId}-${message.message}-${message.sendDate}`;

//           if (this.processedMessages.has(messageId)) {
//             console.log("Duplicate message ignored:", messageId);
//             return;
//           }

//           this.messages.push(message);
//           this.processedMessages.add(messageId);

//           this.$nextTick(() => {
//             const container = this.$refs.messageContainer;
//             if (container) {
//               container.scrollTop = container.scrollHeight;
//             }
//           });
//         }
//       } catch (error) {
//         console.error("메시지 처리 중 오류:", error);
//       }
//     },
//     sendMessage() {
//       if (!this.newMessage.trim()) return;

//       const message = {
//         chatRoomId: this.currentChatRoomId,
//         userId: this.currentUser.userId,
//         message: this.newMessage.trim(),
//         userNickname:
//           this.currentUser.nickname || this.currentUser.userNickname || "익명",
//         userProfile:
//           this.currentUser.profileUrl ||
//           this.currentUser.userProfile ||
//           "/img/default-profile.png",
//       };

//       WebSocketService.send(
//         `/app/sendMessage/${this.currentChatRoomId}`,
//         message
//       );

//       this.newMessage = "";
//       // 서버에서 처리 후 메시지를 전송하므로, 여기서는 메시지를 추가하지 않습니다.
//       // this.messages.push(message);
//     },
//   },
//   watch: {
//     // chatRoomId가 변경될 때마다 새로운 채팅방 구독
//     currentChatRoomId: {
//       handler(newId) {
//         console.log("채팅방 ID 변경:", newId);
//         if (this.isConnected) {
//           this.subscribeToChat();
//         }
//       },
//     },
//   },
// };
</script>

<style></style>
