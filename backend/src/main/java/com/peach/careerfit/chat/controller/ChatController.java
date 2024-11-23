package com.peach.careerfit.chat.controller;

import java.sql.Timestamp;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.peach.careerfit.chat.model.dto.ChatMessageRequest;
import com.peach.careerfit.chat.model.dto.ChatMessageResponse;
import com.peach.careerfit.chat.model.service.ChatHistoryService;
import com.peach.careerfit.user.model.dto.User;
import com.peach.careerfit.user.model.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatHistoryService chatHistoryService;
    private final UserService userService;
    
    @MessageMapping("/sendMessage/{chatRoomId}")
    public void sendMessage(@DestinationVariable int chatRoomId, ChatMessageRequest request) {
        try {
            System.out.println("메시지 수신 - chatRoomId: " + chatRoomId + ", request: " + request);

            ChatMessageResponse response = ChatMessageResponse.builder()
                    .chatRoomId(chatRoomId)
                    .userId(request.getUserId())
                    .message(request.getMessage())
                    .sendDate(new Timestamp(System.currentTimeMillis()))
                    .userNickname(request.getUserNickname())
                    .userProfile(request.getUserProfile())
                    .build();

            // DB에 저장
            chatHistoryService.saveMessage(response);

            // 클라이언트에게 메시지 전송
            messagingTemplate.convertAndSend("/topic/chatRoom/" + chatRoomId, response);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("메시지 처리 실패: " + e.getMessage());
        }
    }

    @MessageMapping("/enterChat/{chatRoomId}")
    public void userEntered(@DestinationVariable int chatRoomId, ChatMessageRequest request) {
        ChatMessageResponse response = ChatMessageResponse.builder()
                .chatRoomId(chatRoomId)
                .userId(request.getUserId())
                .message(request.getUserNickname() + "님이 채팅방에 입장했습니다.")
                .sendDate(new Timestamp(System.currentTimeMillis()))
                .userNickname(request.getUserNickname())
                .userProfile(request.getUserProfile())
                .build();
        
        // DB에 입장 메시지 저장
        chatHistoryService.saveMessage(response);
        
        // 입장 메시지 발행
        messagingTemplate.convertAndSend("/topic/chatRoom/" + chatRoomId, response);
    }
}