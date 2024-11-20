package com.peach.careerfit.chat.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomRequest {
	private String title;
	private int placeId;
	private String creatorNickname;
	private String creatorProfile;
	private int creatorId;
}
