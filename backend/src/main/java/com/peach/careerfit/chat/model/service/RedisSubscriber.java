package com.peach.careerfit.chat.model.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisSubscriber {
    private final SimpMessagingTemplate messagingTemplate;

    public void handleMessage(String message) {
    	 try {
             // Redis로부터 수신된 메시지를 WebSocket으로 전송
             messagingTemplate.convertAndSend("/topic/chatRoom", message);
         } catch (Exception e) {
             e.printStackTrace();
             System.err.println("Redis 메시지 처리 중 오류: " + e.getMessage());
         }
}
}
