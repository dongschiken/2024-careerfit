package com.peach.careerfit.reply.model.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.peach.careerfit.user.model.dto.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ResponseReply {
	private int replyId;
	private User user;
	private List<ResponseReply> replyResponses;
	private	int boardId;
	private int parentReplyId;
	private String content;
	private int depth;
	private String dateAgo;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public void setReplyResponses(List<ResponseReply> replyResponses) {
		this.replyResponses = replyResponses;
	}
}
