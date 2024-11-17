package com.peach.careerfit.reply.model.dto;

import java.time.LocalDateTime;

import com.peach.careerfit.user.model.dto.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ReplyResponse {
	private int replyId;
	private User user;
	private	int boardId;
	private int parentReplyId;
	private String content;
	private int depth;
	private String dateAgo;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
