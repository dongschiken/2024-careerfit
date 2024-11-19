package com.peach.careerfit.chat.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.peach.careerfit.chat.model.dao.ChatHistoryMapper;
import com.peach.careerfit.chat.model.dto.ChatMessageRequest;
import com.peach.careerfit.chat.model.dto.ChatMessageResponse;
import com.peach.careerfit.user.model.service.UserService;

@Service
public class ChatHistoryServiceImpl implements ChatHistoryService{

	@Autowired
	private ChatHistoryMapper chatHistoryMapper;

	// 사용자 닉네임 및 프로필 사진 조회를 위한 서비스
	@Autowired
	private UserService userService;
	
	@Override
	public void sendMessage(int chatRoomId, int sendUserId, ChatMessageRequest request) {
			
		// 메세지 내용이 비어있거나 공백일 경우 예외 처리
		if(request.getMessage() == null || request.getMessage().trim().isEmpty()) {
			throw new IllegalArgumentException("메세지 내용이 유효하지 않습니다.");
		}
		
		// 메세지를 DB에 저장
		 chatHistoryMapper.insertMessage(chatRoomId, sendUserId, request.getMessage());
		 chatHistoryMapper.updateLastMessageTime(chatRoomId);		// 마지막 메시지 시간 업데이트
	}

	@Override
	public List<ChatMessageResponse> getMessage(int chatRoomId) {
		// 특정 채팅방의 메세지 목록을 조회
		List<ChatMessageResponse> messages = chatHistoryMapper.getMessagesByChatRoomId(chatRoomId);
		
		// 각 메시지에 대한 발신자의 닉네임과 프로필 사진을 추가로 조회하여 설정
		for(ChatMessageResponse message : messages) {
			var user = userService.getUserById(message.getSendUserId());
			message.setSenderNickname(user.getNickname());
			message.setSenderProfileUrl(user.getProfileUrl());
		}
		return messages;
	}
}