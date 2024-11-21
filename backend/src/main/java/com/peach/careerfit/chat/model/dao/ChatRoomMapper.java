package com.peach.careerfit.chat.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.peach.careerfit.chat.model.dto.ChatParticipantResponse;
import com.peach.careerfit.chat.model.dto.ChatRoom;

import io.lettuce.core.dynamic.annotation.Param;

@Mapper
public interface ChatRoomMapper {
	// 채팅방 생성
	void insertChatRoom(@Param("title") String title, @Param("placeId") int placeId, @Param("userId") int userId, @Param("userNickname") String userNickname, @Param("userProfile") String userProfile);
	
	// 채팅방 삭제
	void deleteChatRoom(@Param("chat_room_id") int chatRoomId);

	// 채팅방에 유저 추가 (채팅방 참여)
    void insertChatRoomUser(@Param("chatRoomId") int chatRoomId,
                            @Param("userId") int userId);	
	
    // 유저가 이미 채팅방에 있는지 확인
    int countChatRoomUser(@Param("chatRoomId") int chatRoomId, 
    					  @Param("userId") int userId);
    
    // 채팅방에 유저 삭제 (채팅방 나가기)
    void deleteChatRoomUser(@Param("chatRoomId") int chatRoomId, @Param("userId") int userId);

    // 채팅방 참여자 목록 조회
    List<ChatParticipantResponse> selectParticipantsByChatRoom(@Param("chatRoomId") int chatRoomId,
            @Param("offset") int offset,
            @Param("size") int size);
    
    // 특정 채팅방 정보 조회
    ChatRoom getChatRoomById(@Param("chatRoomId") int chatRoomId);
    
    // 채팅방 목록 조회
    List<ChatRoom> getAllChatRooms();
    
    // 마지막으로 생성된 채팅방 ID 가져오기
    int getLastInsertedId();
    
    // 해당 장소의 채팅방 리스트 반환
    List<ChatRoom> getChatRoomsByPlaceId(@Param("placeId") int placeId);
    
}

