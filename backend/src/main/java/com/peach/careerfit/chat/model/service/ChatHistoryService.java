package com.peach.careerfit.chat.model.service;

import java.util.List;

import org.springframework.stereotype.Service;


import com.peach.careerfit.chat.model.dto.ChatMessageRequest;
import com.peach.careerfit.chat.model.dto.ChatMessageResponse;

public interface ChatHistoryService {
	void sendMessage(int chatRoomId, int sendUserId, ChatMessageRequest request);
	List<ChatMessageResponse> getMessage (int chatRoomId);
}
