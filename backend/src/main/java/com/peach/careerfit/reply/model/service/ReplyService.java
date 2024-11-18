package com.peach.careerfit.reply.model.service;

import java.util.List;

import com.peach.careerfit.reply.model.dto.Reply;
import com.peach.careerfit.reply.model.dto.ResponseReply;

public interface ReplyService {
	List<ResponseReply> getReply(int boardId);
	int registReply(Reply reply);
	int setReply(Reply reply);
	int deleteReply(int replyId);
}
