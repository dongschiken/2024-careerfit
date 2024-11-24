package com.peach.careerfit.chat.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.peach.careerfit.chat.model.dao.ChatRoomMapper;
import com.peach.careerfit.chat.model.dto.ChatParticipantResponse;
import com.peach.careerfit.chat.model.dto.ChatRoom;
import com.peach.careerfit.chat.model.dto.ChatRoomRequest;
import com.peach.careerfit.chat.model.dto.ChatRoomUserRequest;

@Service
public class ChatRoomServiceImpl implements ChatRoomService {

	@Autowired
	private ChatRoomMapper chatRoomMapper;

	@Override
	public ChatRoom createChatRoom(ChatRoomRequest request) {
		// 채팅방 생성
		chatRoomMapper.insertChatRoom(request.getTitle(), request.getPlaceId(), request.getUserId(), request.getUserNickname(), request.getUserProfile());

		// 마지막으로 생성된 채팅방 ID 조회
		int chatRoomId = chatRoomMapper.getLastInsertedId();

		chatRoomMapper.insertChatRoomUser(chatRoomId, request.getUserId());
		
		// 생성된 채팅방 정보 반환
		return chatRoomMapper.getChatRoomById(chatRoomId);
	}

	@Override
	public void deleteChatRoom(int chatRoomId) {
		chatRoomMapper.deleteChatRoom(chatRoomId);
	}

	@Override
	public void joinChatRoom(int chatRoomId, ChatRoomUserRequest request) {
		chatRoomMapper.insertChatRoomUser(chatRoomId, request.getUserId());
	}

	@Override
	public boolean isUserAlreadyInChatRoom(int chatRoomId, int userId) {
		return chatRoomMapper.countChatRoomUser(chatRoomId, userId) > 0;
	}

	public void leaveChatRoom(int chatRoomId, int userId) {
		chatRoomMapper.deleteChatRoomUser(chatRoomId, userId);
	}

	@Override
	public List<ChatParticipantResponse> getParticipants(int chatRoomId, int page, int size) {
		int offset = page * size;
		return chatRoomMapper.selectParticipantsByChatRoom(chatRoomId, offset, size);
	}

	@Override
	public ChatRoom getChatRoomById(int chatRoomId) {
		return chatRoomMapper.getChatRoomById(chatRoomId);
	}

	@Override
	public List<ChatRoom> getAllChatRooms() {
		return chatRoomMapper.getAllChatRooms();
	}

	@Override
	public List<ChatRoom> getChatRoomsByPlaceId(int placeId) {
		  List<ChatRoom> chatRooms = chatRoomMapper.getChatRoomsByPlaceId(placeId);

		    return chatRooms;
		}

	@Override
	public List<ChatRoom> getMyChatRooms(int userId) {
	    return chatRoomMapper.getMyChatRooms(userId);
	}

	@Override
	public void updateLastReadAt(int chatRoomId, int userId) {
		 chatRoomMapper.updateLastReadAt(chatRoomId, userId);
	}

}
