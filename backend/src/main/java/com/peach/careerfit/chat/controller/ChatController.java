package com.peach.careerfit.chat.controller;

import java.sql.Timestamp;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import com.peach.careerfit.chat.model.dto.ChatMessageRequest;
import com.peach.careerfit.chat.model.dto.ChatMessageResponse;
import com.peach.careerfit.chat.model.service.ChatHistoryService;
import com.peach.careerfit.chat.model.service.RedisPublisher;
import com.peach.careerfit.user.model.dto.User;
import com.peach.careerfit.user.model.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final RedisPublisher redisPublisher; // Redis 메시지 발행 서비스
    private final ChatHistoryService chatHistoryService; // 채팅 히스토리 관리 서비스
    private final UserService userService;
    
    @MessageMapping("/sendMessage/{chatRoomId}")
    public void sendMessage(@DestinationVariable int chatRoomId, ChatMessageRequest request) {
        try {
            System.out.println("요청받음: chatRoomId=" + chatRoomId + ", request=" + request);

            User user = userService.getUserById(request.getUserId());
           
            ChatMessageResponse response = ChatMessageResponse.builder()
                    .chatRoomId(chatRoomId)
                    .userId(request.getUserId())
                    .message(request.getMessage())
                    .sendDate(new Timestamp(System.currentTimeMillis()))
                    .userNickname(request.getUserNickname())
                    .userProfile(request.getUserProfile())
                    .build();

            redisPublisher.publish(response);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("메시지 전송 실패: " + e.getMessage());
        }
    }


    @MessageMapping("/enterChat/{chatRoomId}")
    public void userEntered(@DestinationVariable int chatRoomId, ChatMessageRequest request) {
        // 사용자 입장 메시지 생성
        ChatMessageResponse response = ChatMessageResponse.builder()
                .chatRoomId(chatRoomId)
                .userId(request.getUserId()) // getUserId로 수정
                .message(request.getUserNickname() + "님이 채팅방에 입장했습니다.") // getUserNickname으로 수정
                .sendDate(new Timestamp(System.currentTimeMillis()))
                .build();

        // Redis를 통해 메시지 발행
        redisPublisher.publish(response);
    }
}
