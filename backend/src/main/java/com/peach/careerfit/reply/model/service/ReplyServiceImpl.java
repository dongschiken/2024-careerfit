package com.peach.careerfit.reply.model.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.peach.careerfit.reply.model.dao.ReplyDao;
import com.peach.careerfit.reply.model.dto.Reply;
import com.peach.careerfit.reply.model.dto.ReplyResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReplyServiceImpl implements ReplyService{
	
	private final ReplyDao replyDao;
	
	@Override
	public List<ReplyResponse> getReply(int boardId) {
		List<ReplyResponse> parents = replyDao.selectReplyByBoardId(boardId);
		for (ReplyResponse replyResponse : parents) {
			replyResponse.setReplyResponses(replyDao.selectReplyChildByParentId(replyResponse.getReplyId()));		
		}
		System.out.println(parents);
		return parents;
	}

	@Override
	public int registReply(Reply reply) {
		int status = replyDao.insertReply(reply);
		return status;
	}

	@Override
	public int setReply(Reply reply) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int deleteReply(int replyId) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
