package com.peach.careerfit.chat.model.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.peach.careerfit.chat.model.dao.ChatHistoryMapper;
import com.peach.careerfit.chat.model.dto.ChatMessageRequest;
import com.peach.careerfit.chat.model.dto.ChatMessageResponse;
import com.peach.careerfit.user.model.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatHistoryServiceImpl implements ChatHistoryService {

    private final ChatHistoryMapper chatHistoryMapper; // 채팅 기록 매퍼
    private final UserService userService; // 사용자 서비스
    private final RedisPublisher redisPublisher; // Redis 메시지 발행기

    @Override
    public void sendMessage(int chatRoomId, int userId, ChatMessageRequest request) {
        // 메세지 내용이 비어있거나 공백일 경우 예외 처리
        if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            throw new IllegalArgumentException("메세지 내용이 유효하지 않습니다.");
        }

        // 사용자 정보 조회
        var user = userService.getUserById(userId);

        // 메세지를 DB에 저장
        chatHistoryMapper.insertMessage(chatRoomId, userId, request.getMessage(), user.getNickname(), user.getProfileUrl());
        chatHistoryMapper.updateLastMessageTime(chatRoomId); // 마지막 메시지 시간 업데이트

        // Redis를 통해 메시지 발행
        redisPublisher.publish(ChatMessageResponse.builder()
        	    .chatRoomId(chatRoomId)
        	    .userId(userId) // 변경된 필드 이름 사용
        	    .message(request.getMessage())
        	    .sendDate(new java.sql.Timestamp(System.currentTimeMillis()))
        	    .userNickname(user.getNickname()) // 수정된 필드 이름 사용
        	    .userProfile(user.getProfileUrl()) // 수정된 필드 이름 사용
        	    .build());

    }

    @Override
    public List<ChatMessageResponse> getMessage(int chatRoomId) {
        return chatHistoryMapper.getMessagesByChatRoomId(chatRoomId);
    }
}
