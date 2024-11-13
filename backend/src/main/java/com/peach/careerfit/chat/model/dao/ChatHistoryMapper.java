package com.peach.careerfit.chat.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.peach.careerfit.chat.model.dto.ChatMessageResponse;

import io.lettuce.core.dynamic.annotation.Param;

@Mapper
public interface ChatHistoryMapper {
	void insertMessage(@Param("chatRoomId") int chatRoomId, @Param("sendUserId") int sendUserId, @Param("message") String message);
	
	List<ChatMessageResponse> getMessagesByChatRoomId(@Param("chatRoomId") int chatRoomId);	
}
