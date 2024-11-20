package com.peach.careerfit.chat.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageRequest {
	private int sendUserId; // 발신자 ID
	private int chatRoomId; // 채팅방 ID
	private String message; // 메시지 내용
	private String senderNickname; 
	private String senderProfileUrl;
}
