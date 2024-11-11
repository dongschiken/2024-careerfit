package com.peach.careerfit.chat.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.peach.careerfit.chat.model.dto.ChatParticipantResponse;

import io.lettuce.core.dynamic.annotation.Param;

@Mapper
public interface ChatRoomMapper {
	// 채팅방 생성
	void insertChatRoom(@Param("title") String title);
	
	// 채팅방 삭제
	void deleteChatRoom(@Param("chat_room_id") int chatRoomId);

	// 채팅방에 유저 추가
    void insertChatRoomUser(@Param("chatRoomId") int chatRoomId,
                            @Param("userId") int userId);	
	
    // 유저가 이미 채팅방에 있는지 확인
    int countChatRoomUser(@Param("chatRoomId") int chatRoomId, 
    					  @Param("userId") int userId);
    
    // 채팅방에 유저 삭제
    void deleteChatRoomUser(@Param("chatRoomId") int chatRoomId, @Param("userId") int userId);

    // 채팅방 참여자 목록 조회
    List<ChatParticipantResponse> selectParticipantsByChatRoom(@Param("chatRoomId") int chatRoomId,
            @Param("offset") int offset,
            @Param("size") int size);
}

