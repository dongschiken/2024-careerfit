package com.peach.careerfit.reply.model.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@AllArgsConstructor
@ToString
public class Reply {
	private int replyId;
	private int userId;
	private int boardId;
	private Integer parentReplyId;
	private String content;
	private int depth;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
	public void setParentReplyId(Integer parentReplyId) {
		this.parentReplyId = parentReplyId;
	}
	
}
