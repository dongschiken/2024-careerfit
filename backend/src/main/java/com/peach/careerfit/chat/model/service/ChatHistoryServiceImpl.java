package com.peach.careerfit.chat.model.service;

import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.peach.careerfit.chat.model.dao.ChatHistoryMapper;
import com.peach.careerfit.chat.model.dto.ChatMessageRequest;
import com.peach.careerfit.chat.model.dto.ChatMessageResponse;
import com.peach.careerfit.user.model.service.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatHistoryServiceImpl implements ChatHistoryService {
    private final ChatHistoryMapper chatHistoryMapper;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void saveMessage(ChatMessageResponse message) {
        chatHistoryMapper.insertMessage(
            message.getChatRoomId(),
            message.getUserId(),
            message.getMessage(),
            message.getUserNickname(),
            message.getUserProfile()
        );
        chatHistoryMapper.updateLastMessageTime(message.getChatRoomId());
    }

    @Override
    public void sendMessage(int chatRoomId, int userId, ChatMessageRequest request) {
        if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            throw new IllegalArgumentException("메시지 내용이 유효하지 않습니다.");
        }
        
        var user = userService.getUserById(userId);
        
        ChatMessageResponse response = ChatMessageResponse.builder()
            .chatRoomId(chatRoomId)
            .userId(userId)
            .message(request.getMessage())
            .sendDate(new java.sql.Timestamp(System.currentTimeMillis()))
            .userNickname(user.getNickname())
            .userProfile(user.getProfileUrl())
            .build();
            
        // DB에 저장
        saveMessage(response);
        
        // WebSocket으로 전송
        messagingTemplate.convertAndSend("/topic/chat/" + chatRoomId, response);
    }

    @Override
    public List<ChatMessageResponse> getMessage(int chatRoomId) {
        return chatHistoryMapper.getMessagesByChatRoomId(chatRoomId);
    }
}