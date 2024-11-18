package com.peach.careerfit.reply.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.peach.careerfit.reply.model.dto.Reply;
import com.peach.careerfit.reply.model.dto.ResponseReply;

@Mapper
public interface ReplyDao {
	List<ResponseReply> selectReplyChildByParentId(int replyId);
	List<ResponseReply> selectReplyByBoardId(int boardId);
	int deleteReplyByReplyId(int replyId);
	int insertReply(Reply reply);
	int updateReply(Reply reply);
}
