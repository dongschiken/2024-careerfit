package com.peach.careerfit.chat.controller;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.peach.careerfit.chat.model.dto.ChatMessageRequest;
import com.peach.careerfit.chat.model.dto.ChatMessageResponse;
import com.peach.careerfit.chat.model.service.ChatHistoryService;

@Controller
public class ChatController {
	
	@Autowired
	private ChatHistoryService chatHistoryService;

	  @MessageMapping("/sendMessage/{chatRoomId}")
	    @SendTo("/topic/chatRoom/{chatRoomId}")
	    public ChatMessageResponse sendMessage(@DestinationVariable int chatRoomId, ChatMessageRequest request) {
	        // 메시지를 DB에 저장
	        chatHistoryService.sendMessage(chatRoomId, request.getSendUserId(), request);

	        // 클라이언트로 전송할 메시지 생성
	        ChatMessageResponse response = ChatMessageResponse.builder()
	                .chatRoomId(chatRoomId)
	                .sendUserId(request.getSendUserId())
	                .message(request.getMessage())
	                .sendDate(new Timestamp(System.currentTimeMillis()))
	                .senderNickname(request.getSenderNickname())
	                .senderProfileUrl(request.getSenderProfileUrl())
	                .build();
	        return response; // 클라이언트로 브로드캐스트
	    }

	    @MessageMapping("/enterChat")
	    @SendTo("/topic/chatRoom/{chatRoomId}")
	    public ChatMessageResponse userEntered(ChatMessageRequest request) {
	        ChatMessageResponse response = ChatMessageResponse.builder()
	                .chatRoomId(request.getChatRoomId())
	                .sendUserId(request.getSendUserId())
	                .message(request.getSenderNickname() + "님이 채팅방에 입장했습니다.")
	                .sendDate(new java.sql.Timestamp(System.currentTimeMillis()))
	                .build();

	        return response; // 입장 메시지 전송
    }
}
