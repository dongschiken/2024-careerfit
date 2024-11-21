package com.peach.careerfit.chat.model.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisSubscriber {
    private final SimpMessagingTemplate messagingTemplate;

    public void handleMessage(String message) {
        // 수신 메시지를 WebSocket으로 브로드캐스트
        messagingTemplate.convertAndSend("/topic/chatroom", message);
    }
}
