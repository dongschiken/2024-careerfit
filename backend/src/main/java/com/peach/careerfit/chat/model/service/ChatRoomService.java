package com.peach.careerfit.chat.model.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.peach.careerfit.chat.model.dto.ChatParticipantResponse;
import com.peach.careerfit.chat.model.dto.ChatRoom;
import com.peach.careerfit.chat.model.dto.ChatRoomRequest;
import com.peach.careerfit.chat.model.dto.ChatRoomUserRequest;

import io.lettuce.core.dynamic.annotation.Param;

@Service
public interface ChatRoomService {
	void createChatRoom(ChatRoomRequest request);
	
	void deleteChatRoom(int chatRoomId);
	
	void joinChatRoom(int chatRoomId, ChatRoomUserRequest request);
	
	boolean isUserAlreadyInChatRoom(int chatRoomId, int userId);
	
	void leaveChatRoom(int chatRoomId, int userId); 
	
	List<ChatParticipantResponse> getParticipants(int chatRoomId, int page, int size);
	
    ChatRoom getChatRoomById(int chatRoomId);
	
}
