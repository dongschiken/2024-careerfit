package com.peach.careerfit.chat.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.peach.careerfit.chat.model.dao.ChatHistoryMapper;
import com.peach.careerfit.chat.model.dto.ChatMessageRequest;
import com.peach.careerfit.chat.model.dto.ChatMessageResponse;

@Service
public class ChatHistoryServiceImpl implements ChatHistoryService{

	@Autowired
	private ChatHistoryMapper chatHistoryMapper;
	
	@Override
	public void sendMessage(int chatRoomId, int sendUserId, ChatMessageRequest request) {
			
		// 메세지 내용이 비어있거나 공백일 경우 예외 처리
		if(request.getMessage() == null || request.getMessage().trim().isEmpty()) {
			throw new IllegalArgumentException("메세지 내용이 유효하지 않습니다.");
		}
		
		// 메세지를 DB에 저장
		 chatHistoryMapper.insertMessage(chatRoomId, sendUserId, request.getMessage());
	}

	@Override
	public List<ChatMessageResponse> getMessage(int chatRoomId) {
		// 특정 채팅방의 메세지 목록을 조회
		return chatHistoryMapper.getMessagesByChatRoomId(chatRoomId);
	}
}