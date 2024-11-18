package com.peach.careerfit.reply.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.peach.careerfit.reply.model.dto.Reply;
import com.peach.careerfit.reply.model.dto.ReplyResponse;

@Mapper
public interface ReplyDao {
	List<ReplyResponse> selectReplyChildByParentId(int replyId);
	List<ReplyResponse> selectReplyByBoardId(int boardId);
	int deleteReplyByReplyId(int replyId);
	int insertReply(Reply reply);
	int updateReply(Reply reply);
}
