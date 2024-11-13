package com.peach.careerfit.chat.model.service;

import java.util.List;

import org.springframework.stereotype.Service;


import com.peach.careerfit.chat.model.dto.ChatMessageRequest;
import com.peach.careerfit.chat.model.dto.ChatMessageResponse;

public interface ChatHistoryService {
	// 메세지 전송
	void sendMessage(int chatRoomId, int sendUserId, ChatMessageRequest request);
	
	// 특정 채팅방 메세지 목록 조회
	List<ChatMessageResponse> getMessage (int chatRoomId);
}
