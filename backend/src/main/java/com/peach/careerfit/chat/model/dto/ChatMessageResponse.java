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
	private int chatHistoryId;
	private int chatRoomId;
	private int userId;
	private String email;
	private String nickname;		// 닉네임 추가
	private String message;
	private Timestamp sendDate;
	private String profileUrl;		// 프로필 사진 URL 추가
	private String timeAgo;
}
