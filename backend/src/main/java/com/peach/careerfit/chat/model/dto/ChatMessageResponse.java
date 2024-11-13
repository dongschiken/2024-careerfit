package com.peach.careerfit.chat.model.dto;

import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageResponse {
	private int chatRoomId;
	private int sendUserId;
	private String message;
	private Timestamp sendDate;
}
