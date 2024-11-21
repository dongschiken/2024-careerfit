package com.peach.careerfit.chat.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.peach.careerfit.chat.model.dto.ChatMessageResponse;

import io.lettuce.core.dynamic.annotation.Param;

@Mapper
public interface ChatHistoryMapper {
    void insertMessage(
        @Param("chatRoomId") int chatRoomId,
        @Param("userId") int userId,
        @Param("message") String message,
        @Param("userNickname") String userNickname,
        @Param("userProfileUrl") String userProfileUrl
    );

    List<ChatMessageResponse> getMessagesByChatRoomId(@Param("chatRoomId") int chatRoomId);
    void updateLastMessageTime(@Param("chatRoomId") int chatRoomId);
}
