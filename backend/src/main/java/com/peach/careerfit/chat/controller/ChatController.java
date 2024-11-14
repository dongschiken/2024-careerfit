package com.peach.careerfit.chat.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import com.peach.careerfit.chat.model.dto.ChatMessageResponse;

@Controller
public class ChatController {

    @MessageMapping("/sendMessage/{chatRoomId}")  // 클라이언트가 "/app/sendMessage"로 보낸 메시지를 처리합니다.
    @SendTo("/topic/chatRoom/{chatRoomId}")  // "/topic/chatRoom/{chatRoomId}" 경로를 구독한 모든 클라이언트에게 메시지를 전송합니다.
    public ChatMessageResponse sendMessage(@DestinationVariable int chatRoomId, ChatMessageResponse message) {
    	System.out.println(chatRoomId);
        System.out.println(message);
        return message;  // 받은 메시지를 그대로 반환하여 브로드캐스트합니다.
    }
    
    // 유저가 채팅방에 입장할 때 처리 (입장 메시지 전송)
    @MessageMapping("/enterChat") // "/app/enterChat"로 클라이언트가 보낸 메시지를 처리합니다.
    @SendTo("/topic/chatRoom/{chatRoomId}")  // 특정 채팅방에 입장한 유저 정보를 해당 채팅방을 구독한 클라이언트들에게 전송
    public ChatMessageResponse userEntered(ChatMessageResponse message) {
        // 입장 메시지를 만들어서 브로드캐스트
        message.setMessage(message.getSenderNickname() + "님이 채팅방에 입장했습니다.");
        return message;  // 입장 메시지 전송
    }
}
