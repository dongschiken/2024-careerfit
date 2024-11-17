package com.peach.careerfit.reply.model.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Reply {
	private int replyId;
	private int userId;
	private int boardId;
	private int parentReplyId;
	private String content;
	private int depth;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
